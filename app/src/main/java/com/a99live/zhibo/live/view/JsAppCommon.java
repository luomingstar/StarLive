package com.a99live.zhibo.live.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.model.UMengInfo;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.WebProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by JJGCW on 2016/12/14.
 */

public class JsAppCommon {

    private Activity mContext;

    public JsAppCommon(Activity context) {
        this.mContext = context;
    }

    @JavascriptInterface
    public void tijiao(String url){
        WebProtocol webProtocol = new WebProtocol();
        LiveRequestParams params = new LiveRequestParams();
        webProtocol.getWebView(url,params)
                .observeOn(AndroidSchedulers.mainThread())
//                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        new MyDialog(mContext).setMessage(s).
//                                setLeftButtonState("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        }).
                                setRightButtonState("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setCanceledOnTouchOutside(false).show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
                    }
                });
    }

    /**
     * 网页调分享
     * @param share
     * window.gcw.share('{
    "title":"999d",
    "thumb":"http://www.999d.com/res/dist/images/logo.png",
    "intro":"99广场舞人人都是舞蹈家",
    "url":"http://www.999d.com"
    }');
     */
    @JavascriptInterface
    public void share(String share){
        Log.d("livelog",share);
        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(share);
        if (listMapByJson.size()>0){
            Map<String, String> map = listMapByJson.get(0);
            String title = map.get("title");
            String thumb = map.get("thumb");
            String intro = map.get("intro");
            String url = map.get("url");
            if (mContext != null){
                UMengInfo.getWebShareTool(mContext, title, intro, new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                },url,thumb);
            }
        }
    }
}
