package com.a99live.zhibo.live.activity.xiaovideo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.activity.xiaovideo.weight.CommentList;
import com.a99live.zhibo.live.activity.xiaovideo.weight.MediaController;
import com.a99live.zhibo.live.activity.xiaovideo.weight.MediaControllerView;
import com.a99live.zhibo.live.activity.xiaovideo.weight.VideoCommentInput;
import com.a99live.zhibo.live.model.UMengInfo;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.VideoProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.NetUtils;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by JJGCW on 2017/4/10.
 */

public class XiaoVideoPlayerActivity extends BaseActivity implements VideoViewInterface, View.OnClickListener {


    private static final String TAG = XiaoVideoPlayerActivity.class.getSimpleName();
    private static final int MESSAGE_ID_RECONNECTING = 0x01;

    private MediaController mMediaController;
    private PLVideoView mVideoView;
    private Toast mToast = null;
    private String mVideoPath = null;
    private int mDisplayAspectRatio = PLVideoView.ASPECT_RATIO_FIT_PARENT;
    private boolean mIsActivityPaused = true;
    private View mLoadingView;
//    private View mCoverView = null;
    private int mIsLiveStreaming = 1;

    private String url = "";
    private Map<String, String> map;
    private CommentList commentList;
    private VideoCommentInput input;
    private String video_id;
    private ImageView videoErroy;
    private RelativeLayout video_complete;

    /**
     * "video_id":"4",
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
     }
     * @param context
     * @param map
     */
    public static void goXiaoVideoPlayerActivity(Context context, Map<String, String> map){
        Intent intent = new Intent(context,XiaoVideoPlayerActivity.class);
        intent.putExtra("map", (Serializable) map);
        context.startActivity(intent);
    }
    private void setOptions(int codecType) {
        AVOptions options = new AVOptions();

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, mIsLiveStreaming);
        if (mIsLiveStreaming == 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        }

        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, codecType);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        mVideoView.setAVOptions(options);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_xiaovideoplayer);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (bundle.containsKey("map")){
                map = (Map<String, String>) bundle.getSerializable("map");
            }else{
                UIUtils.showToast("加载视频数据失败");
                finish();
            }
        }else{
            UIUtils.showToast("加载视频数据失败");
            finish();
        }
        FrameLayout root = (FrameLayout) findViewById(R.id.video_root);
        mVideoView = (PLVideoView) findViewById(R.id.VideoView);
//        mCoverView = (ImageView) findViewById(R.id.CoverView);
//        mVideoView.setCoverView(mCoverView);
        mLoadingView = findViewById(R.id.LoadingView);
        mVideoView.setBufferingIndicator(mLoadingView);
        mLoadingView.setVisibility(View.VISIBLE);

        mVideoPath = map.get("video_address");
        video_id = map.get("video_id");
        mIsLiveStreaming = getIntent().getIntExtra("liveStreaming", 0);
        // 1 -> hw codec enable, 0 -> disable [recommended]
        int codec = getIntent().getIntExtra("mediaCodec", AVOptions.MEDIA_CODEC_SW_DECODE);
        setOptions(codec);
        // Set some listeners
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_16_9);
//        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);
        ViewGroup.LayoutParams layoutParams = mVideoView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = UIUtils.getScreenWidth()*9/16;
        mVideoView.setLayoutParams(layoutParams);
        // You can also use a custom `MediaController` widget
        mMediaController = new MediaController(root,this);
        mVideoView.setMediaController(mMediaController);
        initView();
        initPlayNum();
    }

    private void initPlayNum(){
        String videoPlay = SPUtils.getString(SPUtils.VIDEO_PLAY);
        if (!TextUtils.isEmpty(videoPlay)){
//            if (videoPlay.contains(",")){
                SPUtils.putString(SPUtils.VIDEO_PLAY,videoPlay+","+video_id);
//            }else{
//
//            }
            String string = SPUtils.getString(SPUtils.VIDEO_PLAY);
            String[] split = string.split(",");
            if (split.length > 9){
                sendPlay(string);
            }
        }else{
            SPUtils.putString(SPUtils.VIDEO_PLAY,video_id);
        }
    }

    private void sendPlay(String split){
        VideoProtocol videoProtocol = new VideoProtocol();
        LiveRequestParams params = new LiveRequestParams();
        params.put("vids",split);
        videoProtocol.getPlay(params).observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                SPUtils.putString(SPUtils.VIDEO_PLAY,"");
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (input != null){
            input.setInputMode(VideoCommentInput.InputMode.NONE);
        }
        return super.onTouchEvent(event);
    }

    private void initView(){
        commentList = (CommentList) findViewById(R.id.commentList);
        String videoId = map.get("video_id");
        commentList.setData(videoId);
        TextView play_num = (TextView) findViewById(R.id.play_num);
        TextView video_time= (TextView) findViewById(R.id.video_time);
        TextView nick_name = (TextView) findViewById(R.id.nick_name);
        TextView content = (TextView) findViewById(R.id.content);
        TextView zan = (TextView) findViewById(R.id.zan);
        TextView ping = (TextView) findViewById(R.id.ping);
        String nick_name1 = map.get("nick_name");
        nick_name.setText(nick_name1);
        String total_play = map.get("total_play");
        play_num.setText(total_play+"播放");
        String create_time = map.get("create_time");
        video_time.setText(create_time);
        String desc = map.get("desc");
        content.setText(desc);
        String total_desc = map.get("total_desc");
        String total_like = map.get("total_like");
        zan.setText(total_like+"点赞");
        ping.setText(total_desc+"评论");

        input = (VideoCommentInput) findViewById(R.id.input);
        input.setVideoViewInteface(this);
        MediaControllerView top = (MediaControllerView) findViewById(R.id.top);
        top.initListener(map);
        videoErroy = (ImageView) findViewById(R.id.video_erroy);
        videoErroy.setVisibility(View.GONE);
        videoErroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoErroy.setVisibility(View.GONE);
                sendReconnectMessage();
            }
        });
        video_complete = (RelativeLayout) findViewById(R.id.video_complete);
        video_complete.setVisibility(View.GONE);
        LinearLayout video_restart = (LinearLayout) findViewById(R.id.video_restart);
        ImageView share_qq = (ImageView) findViewById(R.id.share_qq);
        ImageView share_room = (ImageView) findViewById(R.id.share_room);
        ImageView share_weixin = (ImageView) findViewById(R.id.share_weixin);
        ImageView share_circle = (ImageView) findViewById(R.id.share_circle);
        video_restart.setOnClickListener(this);
        share_circle.setOnClickListener(this);
        share_qq.setOnClickListener(this);
        share_room.setOnClickListener(this);
        share_weixin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_restart:
                video_complete.setVisibility(View.GONE);
//                mVideoView.seekTo(0);
//                mVideoView.start();
                reStartVideo();
                break;
            case R.id.share_qq:
                if (map != null){
                    final String video_share_url = map.get("video_share_url");
                    final String title = map.get("title");
                    final String pg_img_url = map.get("pg_img_url");
                    final String desc = map.get("desc");
                    UMengInfo.shareVideo(XiaoVideoPlayerActivity.this,video_share_url,SHARE_MEDIA.QQ,umShareListener,title,desc,pg_img_url);
                }
                break;
            case R.id.share_circle:
                if (map != null){
                    final String video_share_url = map.get("video_share_url");
                    final String title = map.get("title");
                    final String pg_img_url = map.get("pg_img_url");
                    final String desc = map.get("desc");
                    UMengInfo.shareVideo(XiaoVideoPlayerActivity.this,video_share_url,SHARE_MEDIA.WEIXIN_CIRCLE,umShareListener,title,desc,pg_img_url);
                }
            break;
            case R.id.share_room:
                if (map != null){
                    final String video_share_url = map.get("video_share_url");
                    final String title = map.get("title");
                    final String pg_img_url = map.get("pg_img_url");
                    final String desc = map.get("desc");
                    UMengInfo.shareVideo(XiaoVideoPlayerActivity.this,video_share_url,SHARE_MEDIA.QZONE,umShareListener,title,desc,pg_img_url);
                }
            break;
            case R.id.share_weixin:
                if (map != null){
                    final String video_share_url = map.get("video_share_url");
                    final String title = map.get("title");
                    final String pg_img_url = map.get("pg_img_url");
                    final String desc = map.get("desc");
                    UMengInfo.shareVideo(XiaoVideoPlayerActivity.this,video_share_url,SHARE_MEDIA.WEIXIN,umShareListener,title,desc,pg_img_url);
                }
            break;

        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            UIUtils.showToast("分享成功");
            doSuccess();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable throwable) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };

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

    @Override
    protected void onResume() {
        super.onResume();
        mIsActivityPaused = false;
        if (video_complete.getVisibility()==View.VISIBLE || videoErroy.getVisibility() == View.VISIBLE){

        }else {
            mVideoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mToast = null;
        mIsActivityPaused = true;
        mVideoView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

    public void onClickSwitchScreen(View v) {
        mDisplayAspectRatio = (mDisplayAspectRatio + 1) % 5;
        mVideoView.setDisplayAspectRatio(mDisplayAspectRatio);
        switch (mVideoView.getDisplayAspectRatio()) {
            case PLVideoView.ASPECT_RATIO_ORIGIN:
                showToastTips("Origin mode");
                break;
            case PLVideoView.ASPECT_RATIO_FIT_PARENT:
                showToastTips("Fit parent !");
                break;
            case PLVideoView.ASPECT_RATIO_PAVED_PARENT:
                showToastTips("Paved parent !");
                break;
            case PLVideoView.ASPECT_RATIO_16_9:
                showToastTips("16 : 9 !");
                break;
            case PLVideoView.ASPECT_RATIO_4_3:
                showToastTips("4 : 3 !");
                break;
            default:
                break;
        }
    }

    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            Log.d(TAG, "onInfo: " + what + ", " + extra);
            return false;
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
            boolean isNeedReconnect = false;
            Log.d("livelog", "Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
//                    showToastTips("Invalid URL !");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
//                    showToastTips("404 resource not found !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
//                    showToastTips("Connection refused !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
//                    showToastTips("Connection timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
//                    showToastTips("Empty playlist !");
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
//                    showToastTips("Stream disconnected !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
//                    showToastTips("Network IO Error !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
//                    showToastTips("Unauthorized Error !");
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
//                    showToastTips("Prepare timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
//                    showToastTips("Read frame timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_HW_DECODE_FAILURE:
                    setOptions(AVOptions.MEDIA_CODEC_SW_DECODE);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
//                    showToastTips("unknown error !");
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
            if (isNeedReconnect) {
                sendReconnectMessage();
                videoErroy.setVisibility(View.GONE);
            } else {
                videoErroy.setVisibility(View.VISIBLE);
//                if (mVideoView.isPlaying()){
//                videoErroy.setVisibility(View.GONE);
//                }else{
//
//                }
//                finish();
            }
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            Log.d(TAG, "Play Completed !");
//            showToastTips("Play Completed !");
            if (mMediaController != null){
                mMediaController.setCompletion();
            }
//            mVideoView.stopPlayback();
            video_complete.setVisibility(View.VISIBLE);


//            finish();
        }
    };

    private PLMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new PLMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int precent) {
            Log.d(TAG, "onBufferingUpdate: " + precent);
        }
    };

    private PLMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new PLMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(PLMediaPlayer plMediaPlayer) {
            Log.d(TAG, "onSeekComplete !");
        }
    };

    private PLMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height, int videoSar, int videoDen) {
            Log.d(TAG, "onVideoSizeChanged: width = " + width + ", height = " + height + ", sar = " + videoSar + ", den = " + videoDen);
//            ViewGroup.LayoutParams layoutParams = mVideoView.getLayoutParams();
//            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            mVideoView.setLayoutParams(layoutParams);
        }
    };

    private void showToastTips(final String tips) {
        if (mIsActivityPaused) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(XiaoVideoPlayerActivity.this, tips, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != MESSAGE_ID_RECONNECTING) {
                return;
            }
            if (mIsActivityPaused || !NetUtils.isLiveStreamingAvailable()) {
                finish();
                return;
            }
            if (!NetUtils.isNetworkAvailable(XiaoVideoPlayerActivity.this)) {
                sendReconnectMessage();
                return;
            }
            mVideoView.setVideoPath(mVideoPath);
            mVideoView.start();
        }
    };

    private void sendReconnectMessage() {
        showToastTips("正在重连...");
        mLoadingView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 500);
    }

    /**
     * 重播
     */
    private void reStartVideo(){
        mLoadingView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 500);
    }

    @Override
    public void sendText(final Editable text) {
        VideoProtocol videoProtocol = new VideoProtocol();
        LiveRequestParams params = new LiveRequestParams();
        params.put("vid",video_id);
        params.put("content",text.toString());
        videoProtocol.getComm(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                UIUtils.showToast("发送成功");
                                if (input != null){
                                    input.sendOk();
                                }
                                if (commentList != null) {
                                    commentList.sendOk(text.toString(), map.get("video_id"));
                                }
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast("发送失败");
                    }
                });

    }


}
