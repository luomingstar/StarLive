package com.a99live.zhibo.live.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.TCChatMsgListAdapter;
import com.a99live.zhibo.live.adapter.TCUserAvatarListAdapter;
import com.a99live.zhibo.live.event.ShowHomeEvent;
import com.a99live.zhibo.live.gift.GiftAnimationView;
import com.a99live.zhibo.live.model.ChatEntity;
import com.a99live.zhibo.live.model.Robot;
import com.a99live.zhibo.live.model.TCSimpleUserInfo;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.presenter.LoginHelper;
import com.a99live.zhibo.live.presenter.TCChatRoomMgr;
import com.a99live.zhibo.live.presenter.TCDanmuMgr;
import com.a99live.zhibo.live.protocol.ImProtocol;
import com.a99live.zhibo.live.protocol.RoomProtocol;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.TCSwipeAnimationController;
import com.a99live.zhibo.live.utils.TCUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.MyDialog;
import com.a99live.zhibo.live.view.TCInputTextMsgDialog;
import com.a99live.zhibo.live.view.customviews.BeautyDialogFragment;
import com.a99live.zhibo.live.view.customviews.TCHeartLayout;
import com.tencent.TIMElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import master.flame.danmaku.controller.IDanmakuView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/9/24.
 */
public class LivePublisherActivity extends BaseActivity implements ITXLivePushListener,BeautyDialogFragment.SeekBarCallback, TCInputTextMsgDialog.OnTextSendListener, TCChatRoomMgr.TCChatRoomListener, LoginHelper.IMLoginCallback {
    private RoomProtocol roomProtocol;
    private UserProtocol userProtocol;

    //被踢下线广播监听
    private LocalBroadcastManager mLocalBroadcatManager;
    private BroadcastReceiver mExitBroadcastReceiver;

    //预览界面
    private TXCloudVideoView mTXCloudVideoView;

    //美颜美白
    private BeautyDialogFragment mBeautyDialogFragment;

    //头像列表
    private TCUserAvatarListAdapter mAvatarListAdapter;

    //发送消息框
    private TCInputTextMsgDialog mInputTextMsgDialog;
    //消息集合
    private ArrayList<ChatEntity> mArrayListChatEntity = new ArrayList<>();
    //消息adapter
    private TCChatMsgListAdapter mChatMsgListAdapter;

    String nickName = null;
    //弹窗
    private Dialog mDetailDialog;

    private float mScreenHeight;

    private long mSecond = 0;
    private Timer mBroadcastTimer;
    private BroadcastTimerTask mBroadcastTimerTask;

    private long mCurrentMemberCount = 0;

    //美颜级别
    private int mBeautyLevel = 100;
    //美白级别
    private int mWhiteningLevel = 0;

    //房间总数
    private long lTotalMemberCount = 0;

    //送心数
    private long lHeartCount = 0;

    //闪光灯
    private boolean mFlashOn = false;
    //是否暂停
    private boolean mPasuing = false;

    //推流对象
    private TXLivePusher mTXLivePusher;
    //推流自定义
    private TXLivePushConfig mTXPushConfig = new TXLivePushConfig();

    private Handler mHandler = new Handler();

    //点赞动画
    private TCHeartLayout mHeartLayout;

    //弹幕
    private TCDanmuMgr  mDanmuMgr;

    //IM类
    private TCChatRoomMgr mTCChatRoomMgr;

    private ImProtocol imProtocol;

    private LoginHelper loginPresenter;

    //滑动动画控制类
    private TCSwipeAnimationController mTCSwipeAnimationController;

    //直播时长，获赞数量，观看人数
    private TextView mDetailTime, mDetailAdmires, mDetailWatchCount;

    //头像
    @Bind(R.id.iv_head_icon)
    ImageView mHeadIcon;
    //小红点
    @Bind(R.id.iv_record_ball)
    ImageView mRecordBall;
    //直播时间
    @Bind(R.id.tv_broadcasting_time)
    TextView mBroadcastTime;
    //房间人数
    @Bind(R.id.tv_member_counts)
    TextView mMemberCount;

    //头像列表
    @Bind(R.id.rv_user_avatar)
    RecyclerView mUserAvatarList;

    @Bind(R.id.tv_coin_num)
    TextView tv_coin_num;

//    //美化
//    @Bind(R.id.layoutFaceBeauty)
//    LinearLayout mLayoutFaceBeauty;
//    //美白
//    @Bind(R.id.whitening_seekbar)
//    SeekBar mWhiteningSeekBar;
//    //美颜
//    @Bind(R.id.beauty_seekbar)
//    SeekBar mBeautySeekBar;
    //闪光灯
    @Bind(R.id.flash_btn)
    TextView mFlashView;
    //消息列表
    @Bind(R.id.im_msg_listview)
    ListView mListViewMsg;
    //主界面
    @Bind(R.id.rl_controllLayer)
    RelativeLayout mControllLayer;
    //功能按键
    @Bind(R.id.tool_bar)
    LinearLayout tool_bar;

    @Bind(R.id.gift_animation_view)
    GiftAnimationView gift_animation_view;

    @Bind(R.id.creat_live)
    TextView creat_live;

    @Bind(R.id.function_bg)
    LinearLayout function_bg;

    @Bind(R.id.background)
    ImageView mBgImageView;

    private String live_code = null;
    private String room_id = null;
//    private String nick_name;
    private String user_head;
    private String title;
    private String city;
    private String coin;
    private boolean isPushSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //当window被显示的时候，系统将把它当做一个用户活动事件，以点亮手机屏幕
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //当window对用户可见时，让设备屏幕处于高亮（bright）状态
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);

        user_head = SPUtils.getString(SPUtils.USER_AVATAR);
        //背景图
        TCUtils.blurBgPic(this, mBgImageView, user_head, R.drawable.beijingtu);
        userProtocol = new UserProtocol();

        //初始化消息回调
        mTCChatRoomMgr = TCChatRoomMgr.getInstance();
        mTCChatRoomMgr.setMessageListener(this);
        imProtocol = new ImProtocol();
        loginPresenter = LoginHelper.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
//            nick_name = bundle.getString("nick_name");
//            user_head = bundle.getString("user_head");
            title = bundle.getString("title");
            city = bundle.getString("city");
        }
        mControllLayer.setVisibility(View.INVISIBLE);
        roomProtocol = new RoomProtocol();
        //在所有操作之前，进行IM初始化并登录 然后创建聊天室
        getIM();
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        //下线广播
        mLocalBroadcatManager = LocalBroadcastManager.getInstance(this);
        mExitBroadcastReceiver = new ExitBroadcastRecevier();
        mLocalBroadcatManager.registerReceiver(mExitBroadcastReceiver, new IntentFilter(TCConstants.EXIT_APP));
    }

    private void getIM(){
        if (TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())){
//        if (TIMManager.getInstance().getLoginUser()==null || "".equals(TIMManager.getInstance().getLoginUser())){
            getImSig();
        }else {
            mTCChatRoomMgr.setMessageListener(this);
            TIMManager.getInstance().init(this);
            mTCChatRoomMgr.createGroup();
        }
    }

    private void getImSig(){
        LiveRequestParams params = new LiveRequestParams();

        imProtocol.getIMSign(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(map.get("data"));
                                if (dataList.size()>0){
                                    Map<String, String> dataMap = dataList.get(0);
                                    goLoginIM(dataMap);
                                }
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{

                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("livelog-Imsmig","error----");
                        UIUtils.showToast("网络异常，请重试或反馈给我们");
                    }
                });

    }

    private void goLoginIM(Map<String, String> dataMap){
        loginPresenter.imLogin(dataMap.get("uid"), dataMap.get("sig"));
        Log.d("livelog","登录IM");
    }

    @Override
    public void onIMLoginSuccess() {
        if (mTCChatRoomMgr != null)
            mTCChatRoomMgr.createGroup();
        Log.d("livelog","IM登录成功");
    }

    @Override
    public void onIMLoginError(int code, String msg) {
        UIUtils.showToast("创建直播失败正在重试");
        Log.d("livelog","IM登录失败 code="+code);
        getImSig();
//        finish();
    }

    @Override
    public void onJoinGroupCallback(int code, String msg) {

        if (code == 0){
            String avatarId = SPUtils.getString(SPUtils.USER_AVATAR_ID);
            startLive(msg, title, avatarId, city);
        }else if (code == 6013 || code ==6014){
            getImSig();
        }else if (code ==6012) {
            Log.d("livelog",code+"");
            UIUtils.showToast("网络连接超时，创建直播失败");
            finish();
        }else {
            Log.d("livelog",code+"");
            UIUtils.showToast("网络连接异常，创建直播失败");
            finish();
        }
    }

    @Override
    public void onQuitGroupCallback(int code, String msg) {
        if(0 == code) {
            TXLog.d("livelog", "quitGroup success");
        } else {
            TXLog.d("livelog", "quitGroup failed");
        }
    }

    private void startLive(final String group_id, String title, String avatarId, String city){

        LiveRequestParams params = new LiveRequestParams();
        params.put("group_id",group_id);
        params.put("title", title);
        params.put("image", avatarId);
        params.put("city", city);

        roomProtocol.getStartLive(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e("livelog_start_live",s);

                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(map.get("data"));
                                if (dataList.size()>0){
                                    Map<String, String> dataMap = dataList.get(0);

                                    room_id = dataMap.get("room_id");
                                    live_code = dataMap.get("live_code");
                                    String mPushUrl = dataMap.get("pushPath");

                                    doAfterIM(mPushUrl, group_id);
                                }

                            }else if ("100001".equals(map.get("code"))){
                                Toast.makeText(LivePublisherActivity.this, "签名验证失败", Toast.LENGTH_SHORT).show();
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{

                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("livelog_start_live","erro");
                        UIUtils.showToast(R.string.net_error);
                        finish();
                    }
                });
    }

    /**
     * 在IM登录后进行操作
     * @param mPushUrl
     * @param group_id
     */
    private void doAfterIM(String mPushUrl, String group_id){
        getCoin();

        initView();

        getGroupMembersList();

        //开始计时
        startRecordAnimation();
        //开启推流
        startPublish(mPushUrl);

        getImMessage(group_id);
    }

    private void getCoin(){
        LiveRequestParams params = new LiveRequestParams();

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
                                coin = appendMap.get("B");
                                tv_coin_num.setText(coin);
                            }else {

                            }
                        }else {

                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    String sysMessage = TCConstants.SYSTEM_MESSAGE;
    //获取IM系统消息
    private void getImMessage(String gid){
        LiveRequestParams params = new LiveRequestParams();
        params.put("gid", gid);

        imProtocol.getIMMessage(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> map = listMapByJson.get(0);
                            if (map.get("code").equals("0")){
                                ArrayList<Map<String, String>> data = JsonUtil.getListMapByJson(map.get("data"));
                                sysMessage = data.get(0).get("");
                                getSysTemMessage(sysMessage);
                            }else{

                            }
                        }else {

                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("livelog_im_message","erro");
                    }
                });
    }

    private void getSysTemMessage(String msg){
        ChatEntity entity = new ChatEntity();
        entity.setSendName("系统消息:");
        entity.setContext(msg);
        entity.setType(TCConstants.SYSTEM_TYPE);
        notifyMsg(entity);
    }

    /**
     * 初始化页面
     */
    private void initView() {
        function_bg.setVisibility(View.GONE);
        creat_live.setVisibility(View.GONE);
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        //动画滑动
        mTCSwipeAnimationController = new TCSwipeAnimationController(this);
        mTCSwipeAnimationController.setAnimationView(mControllLayer);
        //获取头像列表
        mAvatarListAdapter = new TCUserAvatarListAdapter(this, room_id,giftHandler);
        mUserAvatarList.setAdapter(mAvatarListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserAvatarList.setLayoutManager(linearLayoutManager);

        //美颜和美白
        mBeautyDialogFragment = new BeautyDialogFragment();

        //发消息文本框
        mInputTextMsgDialog = new TCInputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);
//        //美白
//        mWhiteningSeekBar.setOnSeekBarChangeListener(this);
//        //美颜
//        mBeautySeekBar.setOnSeekBarChangeListener(this);
        //直播时长
        mBroadcastTime.setText(String.format(Locale.US,"%s","00:00:00"));
        //头像
        showHeadIcon(mHeadIcon, user_head);

        //自定义点赞
        mHeartLayout = (TCHeartLayout) findViewById(R.id.heart_layout);

        //消息adapter
        mChatMsgListAdapter = new TCChatMsgListAdapter(this, mListViewMsg, mArrayListChatEntity,giftHandler);
        mListViewMsg.setAdapter(mChatMsgListAdapter);

        //弹幕
        IDanmakuView danmakuView = (IDanmakuView) findViewById(R.id.danmakuView);
        mDanmuMgr = new TCDanmuMgr(this);
        mDanmuMgr.setDanmakuView(danmakuView);

        gift_animation_view.setHandler(giftHandler);

        mControllLayer.setVisibility(View.VISIBLE);

    }

    //加载主播头像
    private void showHeadIcon(ImageView view, String avatar) {
        TCUtils.showPicWithUrl(this, view, avatar, R.mipmap.head);
    }


    //自定义退出广播
    public class ExitBroadcastRecevier extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TCConstants.EXIT_APP)) {
                TXLog.d("livelog", "publisher broadcastReceiver receive exit app msg");
                //在被踢下线的情况下，执行退出前的处理操作：停止推流、关闭群组
                stopRecordAnimation();
                if(mTXCloudVideoView != null)
                    mTXCloudVideoView.onPause();
                stopPublish();
                quitRoom();
            }
        }
    }

    /**
     * 开始推流
     */
    private void startPublish(String mPushUrl) {
        if (mTXLivePusher == null) {
            mTXLivePusher = new TXLivePusher(this);
            mTXLivePusher.setPushListener(this);
            //带宽自适应
            mTXPushConfig.setAutoAdjustBitrate(false);
            //视频分辨率
            mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
            //视频比特率
            mTXPushConfig.setVideoBitrate(1000);
            mTXPushConfig.setHardwareAcceleration(true);

//            if (TCHWSupportList.isHWVideoEncodeSupport()){
//                mTXPushConfig.setHardwareAcceleration(true);
//            }
//            else{
//                Log.d("livelog","当前机型不支持视频硬编码");
//                mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
//            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish,options);
            mTXPushConfig.setPauseImg(bitmap);

            mTXLivePusher.setConfig(mTXPushConfig);
        }
        mTXCloudVideoView.setVisibility(View.VISIBLE);
        if (!mTXLivePusher.setBeautyFilter(TCUtils.filtNumber(9, 100, mBeautyLevel), TCUtils.filtNumber(3, 100, mWhiteningLevel))) {
            Toast.makeText(getApplicationContext(), "当前机型的性能无法支持美颜功能", Toast.LENGTH_SHORT).show();
        }

//        //美颜效果
//        mBeautySeekBar.setProgress(100);
//        //美白效果
//        mWhiteningSeekBar.setProgress(50);

        //开始推流
        mTXLivePusher.startCameraPreview(mTXCloudVideoView);
        mTXLivePusher.startPusher(mPushUrl);

    }

    /**
     * 停止推流
     */
    private void stopPublish() {
        if (mTXLivePusher != null) {
            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.setPushListener(null);
            mTXLivePusher.stopPusher();
        }
    }

    /**
     * 美颜fragment progress更新回调
     * @param progress seekbar进度数值
     * @param state 美颜/美白
     */
    @Override
    public void onProgressChanged(int progress, int state) {
        if (state == BeautyDialogFragment.STATE_BEAUTY) {
            mBeautyLevel = progress;
        } else if (state == BeautyDialogFragment.STATE_WHITE) {
            mWhiteningLevel = progress;
        }

        if (mTXLivePusher != null) {
            //当前美颜支持0-9，美白支持0-3
            if (!mTXLivePusher.setBeautyFilter(TCUtils.filtNumber(9, 100, mBeautyLevel), TCUtils.filtNumber(3, 100, mWhiteningLevel))) {
                Toast.makeText(getApplicationContext(), "当前机型的性能无法支持美颜功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //动画
    ObjectAnimator mObjAnim;
    /**
     * 开启红点与计时动画
     */
    private void startRecordAnimation() {
        ObjectAnimator mObjAnim;
        mObjAnim = ObjectAnimator.ofFloat(mRecordBall, "alpha", 1f, 0f, 1f);
        mObjAnim.setDuration(1000);
        mObjAnim.setRepeatCount(-1);
        mObjAnim.start();

        //直播时间
        if(mBroadcastTimer == null) {
            mBroadcastTimer = new Timer(true);
            mBroadcastTimerTask = new BroadcastTimerTask();
            mBroadcastTimer.schedule(mBroadcastTimerTask, 1000, 1000);
        }
    }

    /**
     * 关闭红点与计时动画
     */
    private void stopRecordAnimation() {

        if (null != mObjAnim)
            mObjAnim.cancel();

        //直播时间
        if (null != mBroadcastTimer) {
            mBroadcastTimerTask.cancel();
        }
    }

    /**
     * 记时器
     */
    private class BroadcastTimerTask extends TimerTask {
        public void run() {
            ++mSecond;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mTCSwipeAnimationController != null && !mTCSwipeAnimationController.isMoving())
                        mBroadcastTime.setText(TCUtils.formattedTime(mSecond));
                }
            });
        }
    }

    /**
     * 退出后提示框
     */
    public void showDetailDialog() {
        //确认则显示观看detail
        stopRecordAnimation();
        mDetailTime.setText(TCUtils.formattedTime(mSecond));
        mDetailAdmires.setText(String.format(Locale.CHINA, "%d", lHeartCount));
        mDetailWatchCount.setText(String.format(Locale.CHINA,"%d",lTotalMemberCount));
        mDetailDialog.show();
    }

    /**
     * 退出提示框
     */
    public void showComfirmDialog(String msg, Boolean isError) {
        mDetailDialog = new Dialog(this, R.style.dialog);
        mDetailDialog.setContentView(R.layout.dialog_publish_detail);
        mDetailTime = (TextView) mDetailDialog.findViewById(R.id.tv_time);
        mDetailAdmires = (TextView) mDetailDialog.findViewById(R.id.tv_admires);
        mDetailWatchCount = (TextView) mDetailDialog.findViewById(R.id.tv_members);

        mDetailDialog.setCancelable(false);

        //返回首页
        TextView tvCancel = (TextView) mDetailDialog.findViewById(R.id.btn_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDetailDialog.dismiss();
                EventBus.getDefault().post(new ShowHomeEvent());
                finish();
            }
        });

        MyDialog dialog = new MyDialog(this).setMessage(msg);
        dialog.setRightButtonState("确定",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                dialog.dismiss();
                if (mTXCloudVideoView != null)
                    mTXCloudVideoView.onPause();
                //停止推流
                stopPublish();
                //退出房间
                quitRoom();
                //停止计时动画
                stopRecordAnimation();

                showDetailDialog();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (!isError){
            dialog.setLeftButtonState("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }
        dialog.show();


    }

    /**
     * 退出房间
     * 包含后台退出与IMSDK房间退出操作
     */
    public void quitRoom() {
        //主播端退出前删除直播群组
        if (mTCChatRoomMgr != null)
            mTCChatRoomMgr.deleteGroup();
        closeLive(live_code);
    }

    @Override
    public void onBackPressed() {
//
//        if(mLayoutFaceBeauty.getVisibility() == View.VISIBLE) {
//            mLayoutFaceBeauty.setVisibility(View.GONE);
//            return;
//        }

        showComfirmDialog(TCConstants.TIPS_MSG_STOP_PUSH, false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (loginPresenter != null)
            loginPresenter.setIMLoginCallback(this);
        //弹幕
        if (mDanmuMgr != null){
            mDanmuMgr.resume();
        }
        if (mTXCloudVideoView != null)
            mTXCloudVideoView.onResume();

        if (mPasuing) {
            mPasuing = false;

            if (mTXLivePusher != null) {
                mTXLivePusher.resumePusher();
                mTXLivePusher.startCameraPreview(mTXCloudVideoView);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //弹幕
        if (mDanmuMgr != null){
            mDanmuMgr.pause();
        }
        if (mTXCloudVideoView != null)
            mTXCloudVideoView.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mPasuing = true;
        if (mTXLivePusher != null) {
            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.pausePusher();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFinish = true;
        giftHandler.removeCallbacksAndMessages(null);

        if (loginPresenter != null)
            loginPresenter.removeIMLoginCallback();

        if (mDanmuMgr != null){
            mDanmuMgr.destroy();
            mDanmuMgr = null;
        }
        //停止计时
        stopRecordAnimation();
        if (mTXCloudVideoView != null)
            mTXCloudVideoView.onDestroy();
        mLocalBroadcatManager.unregisterReceiver(mExitBroadcastReceiver);

        stopPublish();
        if (mTCChatRoomMgr != null)
            mTCChatRoomMgr.removeMsgListener();
        closeLive(live_code);
    }

    /**
     * 关闭直播接口
     */
    private void closeLive(String code) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("code", code);

        roomProtocol.getCloseLive(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog","close_live"+"关闭直播成功");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d("livelog","close_live"+"关闭直播失败");
                    }
                });
    }

    @OnClick({R.id.btn_message_input, R.id.tv_share, R.id.flash_btn, R.id.switch_cam, R.id.beauty_btn, R.id.btn_close,R.id.live_more})
    void onClick(View view){
        switch (view.getId()){
            case R.id.btn_message_input:
                inputMsgDialog();
                break;
            case R.id.tv_share:

//                UMengInfo.getUrlTool(this,SPUtils.getString(SPUtils.USER_NAME),umShareListener,SPUtils.getString(SPUtils.USER_ID),false);

//                new ShareAction((Activity)this).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA)
//                        .withTitle(UMengInfo.title)
//                        .withText(UMengInfo.getText(SPUtils.getString(SPUtils.USER_NAME)))
//                        .withMedia(new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher))
//                        .withTargetUrl(UMengInfo.getH5Center(SPUtils.getString(SPUtils.USER_ID)))
//                        .setCallback(umShareListener)
//                        .open();
                break;
            case R.id.flash_btn:
                function_bg.setVisibility(View.GONE);
                if(!mTXLivePusher.turnOnFlashLight(!mFlashOn)) {
                    Toast.makeText(getApplicationContext(), "打开闪光灯失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                mFlashOn = !mFlashOn;
                mFlashView.setBackgroundDrawable(mFlashOn ?
                        getResources().getDrawable(R.mipmap.icon_flash_pressed):
                        getResources().getDrawable(R.drawable.icon_flash));
                break;
            case R.id.switch_cam:
                mTXLivePusher.switchCamera();
                function_bg.setVisibility(View.GONE);
                break;
            case R.id.beauty_btn:
//                mLayoutFaceBeauty.setVisibility(View.VISIBLE);
                mBeautyDialogFragment.show(getFragmentManager(), "");
                function_bg.setVisibility(View.GONE);
                break;
            case R.id.btn_close:
                showComfirmDialog(TCConstants.TIPS_MSG_STOP_PUSH, false);
                break;
            case R.id.live_more:
                if (View.VISIBLE == function_bg.getVisibility()){
                    function_bg.setVisibility(View.GONE);
                }else{
                    function_bg.setVisibility(View.VISIBLE);
                }
            default:
//                mLayoutFaceBeauty.setVisibility(View.GONE);
                tool_bar.setVisibility(View.VISIBLE);
                break;
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Toast.makeText(LivePlayerActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable throwable) {
//            Toast.makeText(LivePlayerActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(LivePlayerActivity.this, platform + " 分享取消啦", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 通知后台开始推流
     */
    private void startShow(String room_id, String live_code){
        LiveRequestParams params = new LiveRequestParams();
        params.put("room_id", room_id);
        params.put("live_code", live_code);

        roomProtocol.startPush(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog","推流成功通知后台" + s);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    /**
     * 拉取用户头像列表
     */
    public void getGroupMembersList() {

        mTCChatRoomMgr.getGroupMembers(new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Log.d("livelog", "GETGroupMembers profile failed");
                getGroupMembersList();
            }

            @Override
            public void onSuccess(List<TIMUserProfile> result) {
                Log.d("livelog", "GETGroupMembers profile success");
                for (TIMUserProfile timUserProfile : result) {
                    mAvatarListAdapter.addItem(new TCSimpleUserInfo(timUserProfile.getIdentifier(),
                            timUserProfile.getNickName(), timUserProfile.getFaceUrl()));
                }
                mCurrentMemberCount = result.size();
                mMemberCount.setText(String.format("%d", mCurrentMemberCount)+ "人");
            }
        });
    }

    /**
     * 拉取IM群组人数
     */
    public void getGroupNumber(){
        mTCChatRoomMgr.getGroupMembers(new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(List<TIMUserProfile> result) {
                mCurrentMemberCount = result.size();
                mMemberCount.setText(String.format("%d", mCurrentMemberCount)+ "人");
            }
        });
    }

    /**
     * 发消息弹出框
     */
    private void inputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();

        lp.width = (int) (display.getWidth()); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }

    //获取权限错误提示框
    private void showErrorAndQuit(String errorMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(errorMsg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                finish();
            }
        });
        if (mTXCloudVideoView != null)
                mTXCloudVideoView.onPause();
        quitRoom();
        stopRecordAnimation();

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    /**
     * 推流状态
     */
    @Override
    public void onPushEvent(int event, Bundle bundle) {
        if (event < 0) {
            if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {//网络断开，弹对话框强提醒，推流过程中直播中断需要显示直播信息后退出
                showComfirmDialog (TCConstants.ERROR_MSG_NET_DISCONNECTED, true);
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {//未获得摄像头权限，弹对话框强提醒，并退出
                showErrorAndQuit(TCConstants.ERROR_MSG_OPEN_CAMERA_FAIL);
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) { //未获得麦克风权限，弹对话框强提醒，并退出
                Toast.makeText(getApplicationContext(), bundle.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
                showErrorAndQuit(TCConstants.ERROR_MSG_OPEN_MIC_FAIL);
            } else {
                //其他错误弹Toast弱提醒，并退出
                Toast.makeText(getApplicationContext(), bundle.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
                if (mTXCloudVideoView != null)
                    mTXCloudVideoView.onPause();
                stopRecordAnimation();
                finish();
            }
        }

        //硬编码启动失败，采用软编码
        if (event == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
            mTXPushConfig.setVideoBitrate(700);
            mTXPushConfig.setHardwareAcceleration(false);
            mTXLivePusher.setConfig(mTXPushConfig);
        }

        //与服务器握手完毕，一切正常，开始推流
        if (event == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {
            //通知后台开始推流
            if (!isPushSuccess) {
                isPushSuccess = true;
                startShow(room_id, live_code);
            }
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    /**
     * 通知消息
     */
    private void notifyMsg(final ChatEntity entity) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mArrayListChatEntity.add(entity);
                if (mChatMsgListAdapter != null){
                    mChatMsgListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 开始发送消息的回掉
     */
    @Override
    public void onTextSend(String msg, boolean danmuOpen) {
        if (msg.length() == 0)
            return;
        try {
            byte[] byte_num = msg.getBytes("utf8");
            if (byte_num.length > 160) {
                Toast.makeText(this, "输入内容过多", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        ChatEntity entity = new ChatEntity();
        entity.setSendName(SPUtils.getString(SPUtils.USER_NAME) + ":");
        entity.setContext(msg);
        entity.setType(TCConstants.TEXT_TYPE);
        notifyMsg(entity);

        //弹幕
        if (danmuOpen) {
            if (mDanmuMgr != null) {
                mDanmuMgr.addDanmu(SPUtils.getString(SPUtils.USER_AVATAR), nickName, msg);
            }
            mTCChatRoomMgr.sendMessage(TCConstants.IMCMD_DANMU, msg);
        } else {
            mTCChatRoomMgr.sendMessage(TCConstants.IMCMD_PAILN_TEXT, msg);
        }
    }

//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        if (seekBar.getId() == R.id.beauty_seekbar) {
//            mBeautyLevel = progress;
//        } else if (seekBar.getId() == R.id.whitening_seekbar) {
//            mWhiteningLevel = progress;
//        }
//
//        if (mTXLivePusher != null) {
//            if (!mTXLivePusher.setBeautyFilter(mBeautyLevel, mWhiteningLevel)) {
//                Toast.makeText(getApplicationContext(), "当前机型的性能无法支持美颜功能", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//    }



    @Override
    public void onSendMsgCallback(int errCode,int msgType, TIMMessage timMessage,String parm) {
        //消息发送成功后回显
        if(errCode == 0) {
            TIMElemType elemType = timMessage.getElement(0).getType();
            if(elemType == TIMElemType.Text) {
                //文本消息显示
                Log.d("livelog", "onSendTextMsgsuccess:" + errCode);

            } else if(elemType == TIMElemType.Custom) {
                //custom消息存在消息回调,此处显示成功失败
                Log.d("livelog", "onSendCustomMsgsuccess:" + errCode);
            }

        } else {
            Log.d("livelog", "onSendMsgfail:" + errCode + " msg:" + errCode);
        }
    }

    @Override
    public void onReceiveTextMsg(String text, String name,String identity,String level, int type) {
        ChatEntity entity = new ChatEntity();
        entity.setSendName(name);
        entity.setContext(text);
        entity.setType(type);
        notifyMsg(entity);
    }

    @Override
    public void onMemberJoin(String userId, String nickname, String faceurl,String level) {
        lTotalMemberCount++;
//        mCurrentMemberCount++;

//        mMemberCount.setText(String.format(Locale.CHINA,"%d",mCurrentMemberCount) + "人");
        //IM人数
        getGroupNumber();

        mAvatarListAdapter.addItem(new TCSimpleUserInfo(userId, nickname, faceurl));

        ChatEntity entity = new ChatEntity();
        entity.setSendName("通知");
        if(nickname.equals(""))
            entity.setContext(userId + "加入直播");
        else
            entity.setContext(nickname + "加入直播");
        entity.setType(TCConstants.MEMBER_ENTER);
        notifyMsg(entity);
    }

    @Override
    public void onMemberQuit(String userId, String nickname) {
//        if(mCurrentMemberCount > 0)
//            mCurrentMemberCount--;
//        else
//            Log.d("livelog", "接受多次退出请求，目前人数为负数");
//        mMemberCount.setText(String.format(Locale.CHINA,"%d",mCurrentMemberCount) + "人");

        //IM人数
        getGroupNumber();

        mAvatarListAdapter.removeItem(userId);

//        ChatEntity entity = new ChatEntity();
//        entity.setSendName("通知");
//        if(nickname.equals(""))
//            entity.setContext(userId + "退出直播");
//        else
//            entity.setContext(nickname + "退出直播");
//        entity.setType(TCConstants.MEMBER_EXIT);
//        notifyMsg(entity);
    }

    @Override
    public void onReceiveDanmu(String text, String nickname, String faceurl,String identity,String level) {
        ChatEntity entity = new ChatEntity();
        entity.setSendName(nickname);
        entity.setContext(text);
        entity.setType(TCConstants.TEXT_TYPE);
        notifyMsg(entity);

        if (mDanmuMgr != null) {
            mDanmuMgr.addDanmu(faceurl,nickname,text);
        }
    }

    @Override
    public void onGroupDelete(String groupId) {

    }

    @Override
    public void onPraise(String userId, String nickname,String level) {
        ChatEntity entity = new ChatEntity();
        entity.setSendName("通知");
        if(nickname.equals(""))
            entity.setContext(userId + "点了个赞");
        else
            entity.setContext(nickname + "点了个赞");

        if (mHeartLayout != null) {
            mHeartLayout.addFavor();
        }
        lHeartCount++;

        //todo：修改显示类型
        entity.setType(TCConstants.PRAISE);
        notifyMsg(entity);
    }

    @Override
    public void onGiftSend(String giftMsg,String id,String name,String url,String level) {
        ArrayList<Map<String, String>> giftList = JsonUtil.getListMapByJson(giftMsg);
        if (giftList.size()>0){
            Map<String, String> map = giftList.get(0);
            if (map.containsKey("B")){
                String b = map.get("B");
                if (!TextUtils.isEmpty(b)){
                    tv_coin_num.setText(b);
                }
            }
            map.put("user_id",id);
            map.put("user_name",name);
            map.put("user_avatar",url);
            if ("1".equals(map.get("continue"))){
                gift_animation_view.sendGift(map);
            }else{
//                UIUtils.showToast("送了一个" + map.get("gift_name"));
                gift_animation_view.sendBigGift(map);

            }


            ChatEntity entity = new ChatEntity();
            entity.setSendName("通知");
            String nickname = map.get("user_name");
            String userId = map.get("user_id");
            if (nickname.equals(""))
                entity.setContext(userId + "送了一个"+map.get("gift_name"));
            else
                entity.setContext(nickname + "送了一个"+map.get("gift_name"));

            entity.setType(TCConstants.TEXT_TYPE);
            notifyMsg(entity);
        }
    }

    @Override
    public void onAddRobot(Robot robot) {

    }

    @Override
    public void onSendRobotCallBack(int i, int cmd, Object o, Robot robot) {

    }

    @Override
    public void onRobotAll(String robotsMsg,String size, String identifier, String nickname, String faceUrl) {

    }

    @Override
    public void onRobotPraise(Robot robot) {

    }

    @Override
    public void onRobotChat(Robot robot) {

    }

    @Override
    public void onFollow(String identifier, String nickname,String level) {

    }

    @Override
    public void onShutUp(String msg) {

    }

    @Override
    public void onShowFlyGift(String str) {

    }

    @Override
    public void onShowShareMsg(String identifier, String nickname, String level, String share) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (function_bg != null && View.VISIBLE == function_bg.getVisibility()){
            if (ev.getRawX() > function_bg.getLeft() && ev.getRawX() < function_bg.getRight()
                    && ev.getRawY() > function_bg.getTop()
                    ){

            }else{
                function_bg.setVisibility(View.GONE);
            }
        }
//        else{
//            function_bg.setVisibility(View.VISIBLE);
//        }


        if(mBeautyDialogFragment != null && !mBeautyDialogFragment.isVisible()) {
            //保证recyclerView与父View滑动不冲突
            if(ev.getRawY() > mScreenHeight * 0.17){
                if (mTCSwipeAnimationController != null)
                    mTCSwipeAnimationController.processEvent(ev);
            }
        }
//        else {
//            //显示情况下，点击外部退出
//            if(ev.getRawY() < mLayoutFaceBeauty.getTop()) {
//                mLayoutFaceBeauty.setVisibility(View.GONE);
//            }
//        }
        return super.dispatchTouchEvent(ev);
    }

    private Map<String ,String> giftMap;
    private boolean isFinish = false;
    public Handler giftHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case selectGift://选了礼物
                    Object obj = message.obj;
                    giftMap = (Map<String, String>) obj;
                    giftMap.put("user_id", SPUtils.getString(SPUtils.USER_ID));
                    giftMap.put("user_name",SPUtils.getString(SPUtils.USER_NAME));
                    giftMap.put("user_avatar",SPUtils.getString(SPUtils.USER_AVATAR));
                    break;
                case cancleGift://取消礼物
                    giftMap = null;
                    break;

                case sendGift://发送礼物
                    break;

                case sendGiftContinue://连送礼物
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
