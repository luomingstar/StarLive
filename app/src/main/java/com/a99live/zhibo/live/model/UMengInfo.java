package com.a99live.zhibo.live.model;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.LivePlayerActivity;
import com.a99live.zhibo.live.net.LiveHttpUrl;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.ShareProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/9/29.
 */
public class UMengInfo {

    public static String text = "有趣好玩的直播平台，正在直播，赶快过来围观下？";
    public static String title = "99直播";
    public static String url1 = "http://m.test.i999d.cn/h5/share/index/id/11?url=99live%3a%2f%2f101.201.115.78%2findex";
    public static String url2 = "http://m.test.i999d.cn/h5/share/index?id=11&url=99live%253a%253f%252f101.201.115.78%252findex";

    public static String h5Index = "http://" + LiveHttpUrl.getH5Url() + "/h5/share/index?id=" + SPUtils.getString(SPUtils.USER_ID) + "&url="+ Uri.encode(LiveHttpUrl.getH5Url());
    public static String getH5Center(String code){
        String h5 = "http://" + LiveHttpUrl.getH5Url() + "/h5/share/center?id=" + code + "&url=" + Uri.encode(LiveHttpUrl.getH5Url());
        return  h5;
    }

    public static String getText(String name){
        String content = "有趣好玩的直播平台，"+  name  + "正在直播，赶快过来围观下？";

        return content;
    }

    /**获取用户自己的分享*/
    public static void getUserShare(final String uid, final String roomId, final boolean isShareLiveRoom
    , final Context context, final UMShareListener listener, final Enum e, final String name
    , final String room_title, final String imgUrl){
        ShareProtocol shareProtocol = new ShareProtocol();
        LiveRequestParams params = new LiveRequestParams();
        if (isShareLiveRoom)
            params.put("id", roomId);
        else
            params.put("id", uid);

        shareProtocol.getShareUrl(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> dataMap = listMapByJson.get(0);
                            if ("0".equals(dataMap.get("code"))){
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(dataMap.get("data"));
                                if (dataList.size() > 0){
                                    Map<String, String> map = dataList.get(0);
                                    String title = map.get("title");
                                    String content = map.get("content");
                                    if (isShareLiveRoom) {
                                        ArrayList<Map<String, String>> room = JsonUtil.getListMapByJson(map.get("room"));
                                        String h5Url = room.get(0).get("url");
                                        initShare(context,h5Url,e,listener,name,title,content,room_title,imgUrl);
                                    } else {
                                        ArrayList<Map<String, String>> user = JsonUtil.getListMapByJson(map.get("user"));
                                        String h5Url = user.get(0).get("url");
                                        initShare(context,h5Url,e,listener,name,title,content,room_title,imgUrl);
                                    }
                                }
                            }else{
                                UIUtils.showToast(dataMap.get("msg"));
                            }
                        }else{
                            UIUtils.showToast(R.string.net_error);
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
                    }
                });
    }

    private static void initShare(Context context,String url,Enum e,
                                  UMShareListener listener,String name,String title,
                                  String content,String room_title,String imgUrl){
        title = title.replace("#nickname#",name);
        content = content.replace("#nickname#",name).replace("#title#",room_title);

        UMImage umImage = null;
        if (!TextUtils.isEmpty(imgUrl) && imgUrl.startsWith("http")){
            umImage = new UMImage(LiveZhiBoApplication.getApp(),imgUrl);
        }else{
            umImage = new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher);
        }

        ShareAction qq = new ShareAction((Activity) context);
//        qq.withText(UMengInfo.getText(name));
        qq.withText(content);
//        qq.withMedia(new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher));
        qq.withMedia(umImage);
//        qq.withTitle(UMengInfo.title);
        qq.withTitle(title);
        qq.withTargetUrl(url);
        qq.setPlatform((SHARE_MEDIA) e).setCallback(listener).share();
    }


    private static void getShareTool(Activity activity,String text,UMShareListener umShareListener,
                                     String url,String title,String content,
                                     String room_title,String imgUrl){
        title = title.replace("#nickname#",text);
        content = content.replace("#nickname#",text).replace("#title#",room_title);

        UMImage umImage = null;
        if (!TextUtils.isEmpty(imgUrl) && imgUrl.startsWith("http")){
            umImage = new UMImage(LiveZhiBoApplication.getApp(),imgUrl);
        }else{
            umImage = new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher);
        }
        new ShareAction((Activity)activity).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
//                .withTitle(UMengInfo.title)
//                .withText(UMengInfo.get Text(text))
                .withTitle(title)
                .withText(content)
//                .withMedia(new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher))
                .withMedia(umImage)
                .withTargetUrl(url)
                .setCallback(umShareListener)
                .open();
    }

    public static void getH5ShareTool(Activity activity, String title, String text, UMShareListener umShareListener, String url){
        new ShareAction((Activity)activity).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .withTitle(title)
                .withText(text)
                .withMedia(new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher))
                .withTargetUrl(url)
                .setCallback(umShareListener)
                .open();
    }

    public static void getVideoShareTool(Activity activity,String title,String content,UMShareListener listener,String url,String img){
        new ShareAction((Activity)activity).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .withTitle(title)
                .withText(content)
                .withMedia(new UMImage(LiveZhiBoApplication.getApp(),img))
                .withTargetUrl(url)
                .setCallback(listener)
                .open();
    }

    public static  void shareVideo(Context context,String url,Enum e,
                                   UMShareListener listener,String title,
                                   String content,String imgUrl){
        UMImage umImage = null;
        if (!TextUtils.isEmpty(imgUrl) && imgUrl.startsWith("http")){
            umImage = new UMImage(LiveZhiBoApplication.getApp(),imgUrl);
        }else{
            umImage = new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher);
        }

        ShareAction qq = new ShareAction((Activity) context);
//        qq.withText(UMengInfo.getText(name));
        qq.withText(content);
//        qq.withMedia(new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher));
        qq.withMedia(umImage);
//        qq.withTitle(UMengInfo.title);
        qq.withTitle(title);
        qq.withTargetUrl(url);
        qq.setPlatform((SHARE_MEDIA) e).setCallback(listener).share();
    }

    /*
     * 直播间的分享
     */
    public static void getUrlTool(final Activity activity, final String text,
                                  final UMShareListener umShareListener, String code,
                                  final boolean isShareLiveRoom,
                                  final String room_tile, final String imgUrl, final LivePlayerActivity.ShareCallBack shareCallBack){
        ShareProtocol shareProtocol = new ShareProtocol();
        LiveRequestParams params = new LiveRequestParams();
        params.put("id", code);

        shareProtocol.getShareUrl(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> dataMap = listMapByJson.get(0);
                            if ("0".equals(dataMap.get("code"))){
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(dataMap.get("data"));
                                if (dataList.size() > 0){
                                    Map<String, String> map = dataList.get(0);
                                    String title = map.get("title");
                                    String content = map.get("content");
                                    if (map.containsKey("msg")) {
                                        String shareMsg = map.get("msg");
                                        if (shareCallBack != null) {
                                            shareCallBack.shareCall(shareMsg);
                                        }
                                    }
                                    if (isShareLiveRoom) {
                                        ArrayList<Map<String, String>> room = JsonUtil.getListMapByJson(map.get("room"));
                                        String h5Url = room.get(0).get("url");
                                        getShareTool(activity,text,umShareListener,h5Url,title,content,room_tile,imgUrl);
                                    }else {
                                        ArrayList<Map<String, String>> user = JsonUtil.getListMapByJson(map.get("user"));
                                        String h5Url = user.get(0).get("url");
                                        getShareTool(activity,text,umShareListener,h5Url,title,content,room_tile,imgUrl);
                                    }
                                }
                            }else{
                                UIUtils.showToast(dataMap.get("msg"));
                            }
                        }else{
                            UIUtils.showToast(R.string.net_error);
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
                    }
                });
    }

    public static void getWebShareTool(final Activity activity, final String title, final String text, final UMShareListener umShareListener, final String url, String imgUrl){
        UMImage umImage = null;
        if (!TextUtils.isEmpty(imgUrl) && imgUrl.startsWith("http")){
            umImage = new UMImage(LiveZhiBoApplication.getApp(),imgUrl);
        }else{
            umImage = new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher);
        }
        final UMImage finalUmImage = umImage;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new ShareAction(activity).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                        .withTitle(title)
                        .withText(text)
                        .withMedia(finalUmImage)
                        .withTargetUrl(url)
                        .setCallback(umShareListener)
                        .open();
            }
        });

    }

}
