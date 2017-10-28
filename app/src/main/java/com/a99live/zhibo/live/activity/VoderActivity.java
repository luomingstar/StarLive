package com.a99live.zhibo.live.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.user.AccountActivity;
import com.a99live.zhibo.live.activity.user.FansContribution;
import com.a99live.zhibo.live.gift.GiftAnimationView;
import com.a99live.zhibo.live.model.UMengInfo;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.GiftProtocol;
import com.a99live.zhibo.live.protocol.RoomProtocol;
import com.a99live.zhibo.live.protocol.ShareProtocol;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.LoginManager;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.TCUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.utils.YMClick;
import com.a99live.zhibo.live.view.MyDialog;
import com.a99live.zhibo.live.view.weight.SendGift;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/9/23.
 */
public class VoderActivity extends BaseActivity implements ITXLivePlayListener, View.OnClickListener, SendGift.HideListener {
    private UserProtocol userProtocol;

    private TXCloudVideoView mTXCloudVideoView;

    //直播对象
    private TXLivePlayer mTXLivePlayer;
    private TXLivePlayConfig mTXPlayConfig = new TXLivePlayConfig();

    private boolean mPlaying = false;
    private long mTrackingTouchTS = 0;
    private boolean mStartSeek = false;
    private boolean mVideoPause = false;

    private String mPlayUrl = null;
    private String is_follow;
    private String mRoomId;
    private boolean isAttetion = false;

    private String anchor_id;

    private SeekBar mSeekBar;
    private ImageView mPlayIcon;
    private TextView mTextProgress;

    private ImageView mBgImageView;

    private int item = 0;
    private boolean isPlayFinish = false;

    private ArrayList<Map<String, String>> playUrlList;


    @Bind(R.id.head_icon)
    ImageView head_icon;

    @Bind(R.id.tv_user_name)
    TextView user_name;

    @Bind(R.id.tv_look_num)
    TextView tv_look_num;

    @Bind(R.id.iv_attention)
    ImageView iv_attention;

    @Bind(R.id.controll)
    RelativeLayout controll;


    @Bind(R.id.wait)
    TextView wait;

    @Bind(R.id.send_gift)
    ImageView send_gift;

    @Bind(R.id.sendgift_view)
    SendGift sendGiftView;

    @Bind(R.id.gift_animation_view)
    GiftAnimationView gift_animation_view;

    @Bind(R.id.tv_coin_num)
    TextView tv_coin_num;

    private int myGoldNum;

    private GiftProtocol giftProtocol;
    private String coin = "";

    private boolean isDestroy;//页面是否已关闭
    private String roomTitle;
    private String user_id;
    private String imgUrl = "";
    private String anchor_ucode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_voder);
        ButterKnife.bind(this);
        initData();
    }

    public static void goVoderActivity(Context context,String lu_id,String avatar){
        Intent intent = new Intent(context,VoderActivity.class);
        intent.putExtra("lu_id", lu_id);
        intent.putExtra(TCConstants.COVER_PIC, avatar);
        context.startActivity(intent);
    }

    private void initView(String avatar) {
        //背景图
        mBgImageView = (ImageView) findViewById(R.id.background);
        TCUtils.blurBgPic(this, mBgImageView, avatar, R.drawable.beijingtu);

        //头像
        showHeadIcon(head_icon, avatar);

//        //关注
//        if (is_follow.equals("N")){
//            iv_attention.setImageResource(R.drawable.attention);
//            isAttetion = false;
//        }else if (is_follow.equals("Y")){
//            iv_attention.setImageResource(R.drawable.unattention);
////            iv_attention.setVisibility(View.INVISIBLE);
//            isAttetion = true;
//        }

        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mTXCloudVideoView.setMirror(false);
        mTextProgress = (TextView) findViewById(R.id.progress_time);
        mPlayIcon = (ImageView) findViewById(R.id.play_btn);
        mSeekBar = (SeekBar) findViewById(R.id.vod_seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean bFromUser) {
                if (mTextProgress != null) {
                    mTextProgress.setText(String.format(Locale.CHINA, "%02d:%02d:%02d/%02d:%02d:%02d", progress / 3600, (progress%3600)/60, (progress%3600) % 60, seekBar.getMax() / 3600, (seekBar.getMax()%3600) / 60, (seekBar.getMax()%3600) % 60));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mStartSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mTXLivePlayer != null) {
                    mTXLivePlayer.seek(seekBar.getProgress());
                }
                mTrackingTouchTS = System.currentTimeMillis();
                mStartSeek = false;
            }
        });
        getGiftList(false);//获取礼物的列表，这里只获取一次不用担心，如果获取失败，点击礼物按钮的时候会再次获取
        sendGiftView.setHideListener(VoderActivity.this);
    }

    private void showHeadIcon(ImageView view, String avatar) {
        TCUtils.showPicWithUrl(this, view, avatar, R.mipmap.head);
    }

    private void initData() {
        controll.setVisibility(View.INVISIBLE);
        userProtocol = new UserProtocol();
        giftProtocol = new GiftProtocol();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (bundle.containsKey("lu_id")) {
                String lu_id = bundle.getString("lu_id");
                String avatar = bundle.getString(TCConstants.COVER_PIC);
                imgUrl = avatar;
                initView(avatar);
                getVideoInfo(lu_id, avatar);
            }else{
                UIUtils.showToast("数据异常");
                finish();
            }

        }

    }

    /**回放接口*/
    private void getVideoInfo(String lu_id, final String avatar){
        LiveRequestParams params = new LiveRequestParams();
        params.put("hid", lu_id);

        RoomProtocol roomProtocol = new RoomProtocol();
        roomProtocol.enterRecord(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {

                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            String code = map.get("code");
                            if ("0".equals(code)){
                                String data = map.get("data");
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                if (dataList.size()>0){
                                    Map<String, String> dataMap = dataList.get(0);
                                    item = 0;
                                    user_name.setText(dataMap.get("nick_name"));
                                    tv_look_num.setText(dataMap.get("online_num")+"人");
                                    mRoomId = dataMap.get("room_id");
                                    roomTitle = dataMap.get("room_title");
                                    is_follow = dataMap.get("is_follow");
                                    anchor_id = dataMap.get("anchor_id");
                                    String playList = dataMap.get("playList");
                                    playUrlList = JsonUtil.getListMapByJson(playList);
                                    mPlayUrl = playUrlList.get(0).get("");
                                    user_id = dataMap.get("anchor_id");
                                    anchor_ucode = dataMap.get("anchor_ucode");

                                    getCoin(anchor_ucode);


                                    //关注
                                    if (is_follow.equals("N")){
                                        iv_attention.setImageResource(R.drawable.attention);
                                        isAttetion = false;
                                    }else if (is_follow.equals("Y")){
                                        iv_attention.setImageResource(R.drawable.unattention);
                                        isAttetion = true;
                                    }
                                }else{
                                    finish();
                                    return;
                                }

                            }else if ("20000006".equals(code)) {
                                Toast.makeText(LiveZhiBoApplication.getApp(), "房间未找到，请刷新列表", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            } else if ("111111110".equals(code)  || "11111113".equals(code) ) {
                                LoginManager.clearUser();
                                LoginManager.goLoginActivity(VoderActivity.this);
                                finish();
                                return;
                            } else if ("11111122".equals(code)) {
                               UIUtils.showToast("在别处登录，请重新登录");
                                LoginManager.clearUser();
                                LoginManager.goLoginActivity(VoderActivity.this);
                                finish();
                                return;
                            }else{
                                UIUtils.showToast(map.get("msg"));
                                finish();
                                return;
                            }

                        }else{
                            UIUtils.showToast(s);
                            finish();
                            return;
                        }

                        if (mPlayUrl != null && !"".equals(mPlayUrl)){
                            startPlay(mPlayUrl);
                        }else{
                            UIUtils.showToast("回放无法播放");
                            finish();
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
                        finish();
                        return;
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mTXCloudVideoView != null)
            mTXCloudVideoView.onResume();
        if (mTXLivePlayer != null)
            mTXLivePlayer.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTXCloudVideoView != null)
            mTXCloudVideoView.onPause();
        if (mTXLivePlayer != null)
            mTXLivePlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        isFinish = true;
        if (gift_animation_view != null){
            gift_animation_view.destroyed();
        }
        if (mTXCloudVideoView != null)
            mTXCloudVideoView.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 开始拉流
     */
    private void startPlay(String url){
        if (mTXLivePlayer == null) {
            mTXLivePlayer = new TXLivePlayer(this);
        }

        mTXLivePlayer.setPlayerView(mTXCloudVideoView);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mTXLivePlayer.setPlayListener(this);
        mTXLivePlayer.setConfig(mTXPlayConfig);

        int result;
        result = mTXLivePlayer.startPlay(url, TXLivePlayer.PLAY_TYPE_VOD_FLV);

        if (0 != result) {
            if (mTXCloudVideoView != null) {
                mTXCloudVideoView.onPause();
            }
            stopPlay(true);
            finish();
        } else {
            mPlaying = true;
        }
        wait.setVisibility(View.GONE);
        controll.setVisibility(View.VISIBLE);
    }

    /**
     * 停止拉流
     * @param clearLastFrame
     */
    private void stopPlay(boolean clearLastFrame) {
        if (mTXLivePlayer != null) {
            mTXLivePlayer.setPlayListener(null);
            mTXLivePlayer.stopPlay(clearLastFrame);
            mPlaying = false;
        }
    }

    @OnClick(R.id.iv_attention)
    public void attention(){
        iv_attention.setClickable(false);
        if (!isAttetion){
            follow(anchor_id);
        }else {
            unFollow(anchor_id);
        }
    }

    /**
     * 关注接口
     */
    private void follow(String ucode){
        LiveRequestParams params = new LiveRequestParams();
        params.put("fid", ucode);

        userProtocol.getFollow(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                isAttetion = true;
                                iv_attention.setImageResource(R.drawable.unattention);
//                                iv_attention.setVisibility(View.INVISIBLE);
                            }else {
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else {
                            UIUtils.showToast(s);
                        }
                        iv_attention.setClickable(true);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        iv_attention.setClickable(true);
                        UIUtils.showToast("网络连接超时");
                    }
                });
    }

    /**
     * 取消关注接口
     */
    private void unFollow(String ucode){
        LiveRequestParams params = new LiveRequestParams();
        params.put("fid", ucode);

        userProtocol.getUnFollow(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog", "取消关注成功");
                        iv_attention.setImageResource(R.drawable.attention);
                        isAttetion = false;
                        iv_attention.setClickable(true);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d("livelog", "取消关注失败");
                        iv_attention.setClickable(true);
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_btn:
                if (mPlaying) {
                    if (mVideoPause) {
                        if (mTXLivePlayer != null) {
                            mTXLivePlayer.resume();
                        }
                        if (mPlayIcon != null) {
                            mPlayIcon.setBackgroundResource(R.drawable.play_pause2);
                        }
                    } else {
                        if (mTXLivePlayer != null) {
                            mTXLivePlayer.pause();
                        }
                        if (mPlayIcon != null) {
                            mPlayIcon.setBackgroundResource(R.drawable.play_start2);
                        }
                    }
                    mVideoPause = !mVideoPause;
                } else {
                    if (isPlayFinish){
                        mPlayUrl = playUrlList.get(item).get("");
                        //enterRecordInfo.getData().getPlayList().get(item);
                        if (mPlayUrl != null && !"".equals(mPlayUrl)){
                            startPlay(mPlayUrl);
                        }else{
                            UIUtils.showToast("回放无法播放");
                            finish();
                        }
                        isPlayFinish = false;
                        if (mPlayIcon != null) {
                            mPlayIcon.setBackgroundResource(R.drawable.play_pause2);
                        }
                    }else{
                        if (mPlayIcon != null) {
                            mPlayIcon.setBackgroundResource(R.drawable.play_pause2);
                        }
                        if (mPlayUrl != null && !"".equals(mPlayUrl)){
                            startPlay(mPlayUrl);
                        }else{
                            UIUtils.showToast("回放无法播放");
                            finish();
                        }
                    }

                }
                break;
            case R.id.btn_vod_back:
                finish();
                break;
            case R.id.iv_share:
                UMengInfo.getUrlTool(this, user_name.getText().toString(), umShareListener, user_id, false,roomTitle,imgUrl,null);
                break;
            case R.id.send_gift:
                if (sendGiftView != null){
                    if (!sendGiftView.isDataSuccess){
                        getGiftList(false);
                    }
                    sendGiftView.show();
//                    tool_bar.setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.jiujiub:
                FansContribution.goFansContributionByUser(VoderActivity.this,anchor_ucode);
                break;
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            UIUtils.showToast("分享成功");
            shareCallBack(SPUtils.getString(SPUtils.USER_ID));
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable throwable) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };

    /**分享回调接口*/
    private void shareCallBack(String uid){
        LiveRequestParams params = new LiveRequestParams();
        params.put("uid", uid);

        ShareProtocol shareProtocol = new ShareProtocol();
        shareProtocol.ShareCallback(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog","sharecallback");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case 101:

                    getGiftList(true);

//                    Bundle extras = data.getExtras();
//                    if (extras != null){
//                        String gift = extras.getString("gift");
//                        if (gift != null && !"".equals(gift)&& !"null".equals(gift)){
//                            myGoldNum = Integer.parseInt(gift);
//                            sendGiftView.setDiamondNum(myGoldNum);
//                        }
//                    }
                    break;
            }
        }
    }

    private void showErrorAndQuit(String errorMsg) {
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onPause();
        }

        stopPlay(true);
        if (!isDestroy) {
            new MyDialog(this).setMessage(errorMsg).setRightButtonState("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).setCancelable(false).show();
        }

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(errorMsg);
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//
//                finish();
//            }
//        });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.setCancelable(false);
//        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.show();
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
        //视频播放进度
        if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS){
            if (mStartSeek) {
                return;
            }
            int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
            int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
            long curTS = System.currentTimeMillis();
            // 避免滑动进度条松开的瞬间可能出现滑动条瞬间跳到上一个位置
            if (Math.abs(curTS - mTrackingTouchTS) < 500) {
                return;
            }
            mTrackingTouchTS = curTS;

            if (mSeekBar != null) {
                mSeekBar.setProgress(progress);
            }
            if (mTextProgress != null) {
                mTextProgress.setText(String.format(Locale.CHINA, "%02d:%02d:%02d/%02d:%02d:%02d", progress / 3600, (progress%3600) / 60, progress % 60, duration / 3600, (duration%3600) / 60, duration % 60));
            }

            if (mSeekBar != null) {
                mSeekBar.setMax(duration);
            }
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT){//网络断连，且经过多次重连抢救无效，可放弃治疗

            showErrorAndQuit(TCConstants.ERROR_MSG_NET_DISCONNECTED);

        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {//视频播放结束
            stopPlay(false);
            isPlayFinish = true;
            ++item;
            item = item > playUrlList.size()-1 ? 0 : item;
            mVideoPause = false;
            if (mTextProgress != null) {
                mTextProgress.setText(String.format(Locale.CHINA, "%s","00:00:00/00:00:00"));
            }
            if (mSeekBar != null) {
                mSeekBar.setProgress(0);
            }
            if (mPlayIcon != null) {
                mPlayIcon.setBackgroundResource(R.drawable.play_start2);
            }

            playNext();
        }
    }

    private void playNext(){
        if (isPlayFinish) {
            mPlayUrl = playUrlList.get(item).get("");
            //enterRecordInfo.getData().getPlayList().get(item);
            if (mPlayUrl != null && !"".equals(mPlayUrl)) {
                startPlay(mPlayUrl);
            } else {
                UIUtils.showToast("回放无法播放");
                finish();
            }
            isPlayFinish = false;
            if (mPlayIcon != null) {
                mPlayIcon.setBackgroundResource(R.drawable.play_pause2);
            }
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    /**获取主播99币*/
    private void getCoin(final String uid){
        if (userProtocol == null){
            return;
        }
        LiveRequestParams params = new LiveRequestParams();
        params.put("ucode", uid);

        userProtocol.MyHomeInfo(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                ArrayList<Map<String, String>> append = JsonUtil.getListMapByJson(map.get("append"));
                                Map<String, String> appendMap = append.get(0);
                                coin = appendMap.get("TB");
                                tv_coin_num.setText(coin + "  >");
                            }else {

                            }
                        }else {

                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getCoin(uid);
                    }
                });
    }

    @Override
    public void hide() {
        sendGiftView.setSendEnable(false);
//        tool_bar.setVisibility(View.VISIBLE);
        giftMap = null;
        giftNum = 0;
        sendGiftView.setRefresh(-1,-1);
    }

    @Override
    public void onlyHideView() {
        sendGiftView.setSendEnable(false);
//        tool_bar.setVisibility(View.VISIBLE);
        giftMap = null;
        giftNum = 0;
    }

    /**获取礼物列表*/
    private void getGiftList(final boolean isGetGold){
        LiveRequestParams params = new LiveRequestParams();
        giftProtocol.getGiftList(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("msg",s);
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> stringStringMap = listMapByJson.get(0);
                            if ("0".equals(stringStringMap.get("code"))){
                                if (isGetGold){
//                                    sendGiftView.initData(stringStringMap,giftHandler);
                                    getMyGoleNum(stringStringMap);
                                    sendGiftView.setDiamondNum(myGoldNum);
//                                    sendGiftView.setHideListener(LivePlayerActivity.this);
                                }else{
                                    sendGiftView.initData(stringStringMap,giftHandler);
                                    getMyGoleNum(stringStringMap);
                                    sendGiftView.setHideListener(VoderActivity.this);
                                }

                            }else{

                            }
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private void getMyGoleNum(Map<String, String> map){
        String append = map.get("append");
        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(append);
        if (listMapByJson.size()>0){
            Map<String, String> stringStringMap = listMapByJson.get(0);
            String z = stringStringMap.get("Z");
            try {
                myGoldNum = Integer.parseInt(z);
            }catch (Exception e){
                myGoldNum = 0;
            }
        }
    }

    private boolean getCanSendGift(){
        String price = giftMap.get("price");
        try {
            int priceGift = Integer.parseInt(price);
            if (myGoldNum >= priceGift){
                if (isSendOk){
                    isSendOk = false;
                }else{
                    return true;
                }

                myGoldNum = myGoldNum - priceGift;
//                sendGiftView.setDiamondNum(myGoldNum);
            }else{
                new MyDialog(this)
                        .setMessage(R.string.no_gold_send_gift)
                        .setLeftButtonState(R.string.cancel, null)
                        .setRightButtonState(R.string.enter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(VoderActivity.this,AccountActivity.class));
                            }
                        }).setCanceledOnTouchOutside(false).show();
                isSendOk = true;
                return true;
            }

        }catch (Exception e){
            UIUtils.showToast("礼物数据异常");
            isSendOk = true;
            return true;
        }
        return  false;
    }

    /**
     * 送礼物
     *  number:数量
     *  gift_id:礼物id
     *  room_id:房间id
     *  to:接收人id
     */
    private void sendGift(final Map<String,String> giftMap , int number, String id, String room_id, String to){
        LiveRequestParams params = new LiveRequestParams();
        params.put("number", number);
        params.put("gift_id", id);
        params.put("room_id", room_id);
        params.put("to", to);
        giftProtocol.sendGift(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog",s);
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");
                                ArrayList<Map<String, String>> listMapByJson1 = JsonUtil.getListMapByJson(data);
                                if (listMapByJson1.size()>0){
                                    Map<String, String> map1 = listMapByJson1.get(0);
                                    String Z = map1.get("Z");
                                    String append = map.get("append");
                                    ArrayList<Map<String, String>> appendList = JsonUtil.getListMapByJson(append);
                                    String B = coin;
                                    if (appendList.size()>0){
                                        Map<String, String> appendMap = appendList.get(0);
                                        B = appendMap.get("TB");
                                    }
                                    //todo 礼物发送成功
                                    myGoldNum = Integer.parseInt(Z);
                                    sendGiftView.setDiamondNum(myGoldNum);
                                    giftMap.put("B", B);
                                    tv_coin_num.setText(B + "  >");
                                    showGiftAnim(giftMap);
//                                    String send = Tools.map2Json(giftMap);
//                                    chatRoom.sendMessage(TCConstants.IMCMD_GIFT,send);
                                }

                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{
                            UIUtils.showToast(s);
                        }
                        isSendOk = true;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //todo 礼物发送失败
                        isSendOk = true;
                    }
                });
    }

    private void showGiftAnim(Map<String, String> map){
            if (map != null){
                if ("1".equals(map.get("continue"))){
                    gift_animation_view.sendGift(map);
                }else{
                    gift_animation_view.sendBigGift(map);
                }

//                ChatEntity entity = new ChatEntity();
//                String nickname = map.get("user_name");
//                String userId = map.get("user_id");
//                if (nickname.equals("")){
//                    entity.setSendName(userId + ":");
//                }else {
//                    entity.setSendName(nickname+ ":");
//                }
//                entity.setContext("送了一个" + map.get("gift_name"));
//                entity.setIdentity(SPUtils.getString(SPUtils.USER_IDENTITY));
//                entity.setLevel(SPUtils.getString(SPUtils.USER_LEVEL));
//                entity.setType(TCConstants.SEND_GIFT);
//                notifyMsg(entity);
            }
    }

    private void goToPay(){
        startActivityForResult(new Intent(this, AccountActivity.class),101);
    }



    private Map<String ,String> giftMap;
    private int giftNum;
    private boolean isFinish = false;
    private boolean isSendOk = true;
    public Handler giftHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case selectGift://选了礼物
                    sendGiftView.setSendEnable(true);
                    giftNum = 0;
                    Object obj = message.obj;
                    giftMap = (Map<String, String>) obj;
                    giftMap.put("user_id", SPUtils.getString(SPUtils.USER_IDENTITY));
                    giftMap.put("user_name",SPUtils.getString(SPUtils.USER_NAME));
                    giftMap.put("user_avatar",SPUtils.getString(SPUtils.USER_AVATAR));
                    sendGiftView.selectGift(true,giftMap);
                    String pageItem = giftMap.get("pageItem");
                    String position = giftMap.get("position");
                    sendGiftView.setRefresh(Integer.parseInt(pageItem),Integer.parseInt(position));
                    break;
                case cancleGift://取消礼物
                    sendGiftView.setSendEnable(false);
                    giftMap = null;
                    giftNum = 0;
                    sendGiftView.selectGift(false, giftMap);
                    break;

                case sendGift://发送礼物
                    if (giftMap != null){
                        giftNum = 1;
                        if (!getCanSendGift()){
                            if ("1".equals(giftMap.get("continue"))){//连送
                                //发送礼物
                                sendGift(giftMap,giftNum,giftMap.get("gift_id"),mRoomId,anchor_id);
                                sendGiftView.sendContinue();
                                YMClick.onEvent(VoderActivity.this,YMClick.SEND_GIFT,giftMap.get("gift_id"),"1");
                            }else{
                                //发送礼物
                                if (giftNum >0 && giftMap != null){
                                    sendGift(giftMap,giftNum,giftMap.get("gift_id"),mRoomId,anchor_id);
                                    YMClick.onEvent(VoderActivity.this,YMClick.SEND_GIFT,giftMap.get("gift_id"),"1");
                                    sendGiftView.setSendEnable(false);
                                    giftMap = null;
                                    giftNum = 0;
                                    sendGiftView.setRefresh(-1,-1);
                                }
                            }
                        }else{

                        }

                    }else{
                        UIUtils.showToast("请选择礼物");
                    }
                    break;

                case sendGiftContinue://连送礼物
                    if (giftMap != null){
                        if(! getCanSendGift()){
                            giftNum = 1;

                            sendGift(giftMap, giftNum, giftMap.get("gift_id"), mRoomId, anchor_id);
                            YMClick.onEvent(VoderActivity.this, YMClick.SEND_GIFT, giftMap.get("gift_id"), "1");
                        }else{

                        }

                    }else{
                        UIUtils.showToast("请选择礼物");
                    }
                    break;

                case GiftAnimationStop://礼物消失
                    if (isFinish){
                        break;
                    }
                    gift_animation_view.giftLoop();
                    break;

                case BigGiftAnimationStop://大礼物消失
                    if (isFinish){
                        break;
                    }
                    gift_animation_view.bigGiftLoop();
                    break;

                case 6://连送按钮结束，进行统计发送
                    break;

                case 7://充值
                    goToPay();
                    break;

                case TCConstants.OPEN_USER_BY_IDENTITY:
                    Object objs = message.obj;
                    String identity = (String) objs;
                    Log.d("livelog",identity);
//                    Intent intent = new Intent(LivePlayerActivity.this, UserCardActivity.class);
//                    intent.putExtra(TCConstants.UCODE, identity);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);

//                    if (number != null && number.equals(identity)){
//                        UserCardActivity.goCardActivity(VoderActivity.this,identity,true,"",false,false);
//                    }else{
//                        UserCardActivity.goCardActivity(VoderActivity.this,identity,false,"",false,false);
//                    }
//                    overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
                    break;

            }

            return false;
        }
    });

    public static final int selectGift = 0;
    public static final int cancleGift = 1;
    public static final int sendGift = 2;
    public static final int sendGiftContinue = 3;
    public static final int GiftAnimationStop = 4;
    public static final int BigGiftAnimationStop = 5;

}
