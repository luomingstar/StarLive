package com.a99live.zhibo.live.activity.xiaovideo.weight;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.model.UMengInfo;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.protocol.VideoProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.Tools;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.BottomDialog;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.bumptech.glide.Glide;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by JJGCW on 2017/4/10.
 */

public class MediaControllerView extends FrameLayout implements View.OnClickListener {

    private Context mContext;
    private ImageView back;
    private ImageView like;
    private ImageView share;
    private ImageView more;
    private ImageView attation;
    private NewCircleImageView head;
    private String author_id;
    private String video_id;
    private boolean isLike = false;
    //    private ImageButton startPause;
//    private SeekBar seekbar;
//    private TextView seek_time;
//    private PLMediaPlayer mPlayer;

    public MediaControllerView(@NonNull Context context) {
        this(context,null);
    }

    public MediaControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MediaControllerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.mediacontrollerview,this,true);
        initView();
    }


    private void initView(){
        back = (ImageView) findViewById(R.id.back);
        like = (ImageView) findViewById(R.id.like);
        share = (ImageView) findViewById(R.id.share);
        more = (ImageView) findViewById(R.id.more);
        attation = (ImageView) findViewById(R.id.attation);
        head = (NewCircleImageView) findViewById(R.id.head);
//        startPause = (ImageButton) findViewById(R.id.startPause);
//        seekbar = (SeekBar) findViewById(R.id.seekbar);
//        seek_time = (TextView) findViewById(R.id.seek_time);

        back.setOnClickListener(this);
    }

    /**
     *  "video_id":"4",
     "author_id":"2",
     "tag_id":"3",
     "title":"this is a test data2",
     "desc":"fdsafdsafdsfdsfdsafdsafdsafdsafd",
     "thumb":"cfc5478c38412cd808a203bbf9a170cc",
     "name":"/admin/mp4/1c0f43a0f75a65b837cbdfa1b477c142.mp4",
     "duration":"00:41",
     "sort_key":"0",
     "total_like":"34",
     "total_play":"44",
     "total_desc":"34",
     "total_share":"2",
     "create_time":"2017-04-11 16:00:39",
     "nick_name":"O(∩_∩)O",
     "ucode":949,
     "number99":"10001",
     "user_head":"http://99live-10063116.image.myqcloud.com/39414f473d62016629d6cc26470eba26?imageMogr2/thumbnail/x200",
     "verify":0,
     "guan":0,
     "pg_img_url":"http://99live-10063116.image.myqcloud.com/cfc5478c38412cd808a203bbf9a170cc?imageMogr2/thumbnail/x750",
     "video_address":"http://99zhiboyidongduan-10063116.file.myqcloud.com/admin/mp4/1c0f43a0f75a65b837cbdfa1b477c142.mp4"
     video_share_url
     }
     * @param map
     */
    public void initListener(final Map<String,String> map){
        String userHead = map.get("user_head");
        author_id = map.get("author_id");
        video_id = map.get("video_id");
        final String video_share_url = map.get("video_share_url");
        final String title = map.get("title");
        final String pg_img_url = map.get("pg_img_url");
        final String desc = map.get("desc");
        String guan = map.get("guan");
        if ("0".equals(guan)){
            attation.setVisibility(VISIBLE);
        }else{
            attation.setVisibility(GONE);
        }
        Glide.with(mContext).load(userHead).dontAnimate().into(head);


        //点赞
        String videoLike = SPUtils.getString(SPUtils.VIDEO_LIKE);
        if (TextUtils.isEmpty(videoLike)){
            isLike = false;
        }else{
            ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(videoLike);
            if (listMapByJson.size()>0){
                Map<String, String> map1 = listMapByJson.get(0);
                if (TextUtils.isEmpty(map1.get(video_id))){
                    isLike = false;
                }else{
                    isLike = true;
                }

            }
        }

        if (isLike){
            like.setImageResource(R.drawable.xvideo_top_like_yes);
        }else{
            like.setImageResource(R.drawable.xvideo_top_like_no);
        }

        like.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLike){
                    UIUtils.showToast("您已点过赞了");
                }else{
                    isLike = true;
                    like.setImageResource(R.drawable.xvideo_top_like_yes);

                    //点赞
                    String videoLike = SPUtils.getString(SPUtils.VIDEO_LIKE);
                    if (TextUtils.isEmpty(videoLike)){
                        Map<String,String> map1 = new HashMap<String, String>();
                        map1.put(video_id,"0");
                        String s = Tools.map2Json(map1);
                        SPUtils.putString(SPUtils.VIDEO_LIKE,s);
                    }else{
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(videoLike);
                        if (listMapByJson.size()>0){
                            Map<String, String> map1 = listMapByJson.get(0);
                            map1.put(video_id,"0");
                            String videoLikeSend = "";
                            if (map1.size()/10==0){
                                for (Map.Entry<String, String> entry : map1.entrySet()) {
//                                   System.out.println("key= " + entry.getKey() + " and value= "
//                                                     + entry.getValue());
                                    String key = entry.getKey();
                                    String value = entry.getValue();
                                    if ("0".equals(value)){
                                        entry.setValue("1");
                                        if (TextUtils.isEmpty(videoLikeSend)){
                                            videoLikeSend = key;
                                        }else{
                                            videoLikeSend += ","+key;
                                        }
                                    }
                                }

                                sendVideoLike(videoLikeSend);

                            }
                            String s = Tools.map2Json(map1);
                            SPUtils.putString(SPUtils.VIDEO_LIKE,s);
                        }
                    }




                }
            }
        });

        share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setShare(video_share_url,title,pg_img_url,desc);
            }
        });

        more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMore();
            }
        });

        attation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttation();
            }
        });

    }

    private void sendVideoLike(String like){
        VideoProtocol videoProtocol = new VideoProtocol();
        LiveRequestParams params = new LiveRequestParams();
        params.put("vids",like);
        videoProtocol.getPlay(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){

                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private void setShare(String video_share_url, String title, String pg_img_url,String desc){
        UMengInfo.getVideoShareTool((Activity) mContext, title, desc, new UMShareListener() {
            @Override
            public void onResult(SHARE_MEDIA share_media) {
                doSuccess();
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        },video_share_url,pg_img_url);
    }

    private void doSuccess(){
        VideoProtocol videoProtocol = new VideoProtocol();
        LiveRequestParams params = new LiveRequestParams();
        params.put("vid",video_id);
        videoProtocol.getShare(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private void setAttation(){
        LiveRequestParams params = new LiveRequestParams();
        params.put("fid", author_id);
        UserProtocol userProtocol = new UserProtocol();

        userProtocol.getFollow(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                Log.d("livelog", "关注成功");
                                UIUtils.showToast("关注成功");
                                attation.setVisibility(GONE);
//                                tv_is_attention.setText(R.string.attentioned);
//                                tv_is_attention.setBackgroundResource(R.drawable.shape_btn_press);
//                                tv_is_attention.setTextColor(getResources().getColor(R.color.text_999));
//                                is_attention = true;
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{
                            UIUtils.showToast(s);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d("livelog", "关注失败");
                    }
                });
    }

    private void setMore(){
        BottomDialog dialog = new BottomDialog(mContext);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOneListener("举报", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserProtocol userProtocol = new UserProtocol();
                LiveRequestParams params = new LiveRequestParams();
                params.put("userid",author_id);
                userProtocol.getMyTip(params)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                                if (listMapByJson.size()>0){
                                    UIUtils.showToast("已收到您的举报信息，感谢您的反馈");
                                }else{
                                    UIUtils.showToast(s);
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
            }
        });
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                ((Activity)getContext()).finish();
                break;
        }
    }

}
