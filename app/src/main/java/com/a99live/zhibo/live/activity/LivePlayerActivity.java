package com.a99live.zhibo.live.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.user.AccountActivity;
import com.a99live.zhibo.live.activity.user.FansContribution;
import com.a99live.zhibo.live.adapter.TCChatMsgListAdapter;
import com.a99live.zhibo.live.adapter.TCUserAvatarListAdapter;
import com.a99live.zhibo.live.event.FollowPlayerEvent;
import com.a99live.zhibo.live.gift.ComeInAnimation;
import com.a99live.zhibo.live.gift.FlyGift;
import com.a99live.zhibo.live.gift.GiftAnimationView;
import com.a99live.zhibo.live.model.ChatEntity;
import com.a99live.zhibo.live.model.Robot;
import com.a99live.zhibo.live.model.TCSimpleUserInfo;
import com.a99live.zhibo.live.model.UMengInfo;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.presenter.LoginHelper;
import com.a99live.zhibo.live.presenter.TCChatRoomMgr;
import com.a99live.zhibo.live.presenter.TCDanmuMgr;
import com.a99live.zhibo.live.protocol.GiftProtocol;
import com.a99live.zhibo.live.protocol.ImProtocol;
import com.a99live.zhibo.live.protocol.RoomProtocol;
import com.a99live.zhibo.live.protocol.ShareProtocol;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.LoginManager;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.SharedPreferencesUtil;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.TCSwipeAnimationController;
import com.a99live.zhibo.live.utils.TCUtils;
import com.a99live.zhibo.live.utils.Tools;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.utils.YMClick;
import com.a99live.zhibo.live.view.GuideView;
import com.a99live.zhibo.live.view.MyDialog;
import com.a99live.zhibo.live.view.TCInputTextMsgDialog;
import com.a99live.zhibo.live.view.customviews.TCHeartLayout;
import com.a99live.zhibo.live.view.weight.SendGift;
import com.tencent.TIMElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import master.flame.danmaku.controller.IDanmakuView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/9/26.
 */
public class LivePlayerActivity extends BaseActivity implements ITXLivePlayListener, LoginHelper.IMLoginCallback, TCChatRoomMgr.TCChatRoomListener,
        TCInputTextMsgDialog.OnTextSendListener ,SendGift.HideListener{

    /**后台接口*/
    private ImProtocol imProtocol;
    private RoomProtocol roomProtocol;
    private UserProtocol userProtocol;
    private GiftProtocol giftProtocol;

    private LoginHelper loginPresenter;

    private float mScreenHeight;

    private String anchor_id;
    private String is_follow;
    private String number = null;

    //拉流地址
    String mPlayUrl = null;
    private String mGroupId;
    private String mRoomId;

    //被踢下线广播监听
    private LocalBroadcastManager mLocalBroadcatManager;
    private BroadcastReceiver mExitBroadcastReceiver;

    private TXCloudVideoView mTXCloudVideoView;
    //发消息文本框
    private TCInputTextMsgDialog mInputTextMsgDialog;

    //消息列表
    private ArrayList<ChatEntity> mArrayListChatEntity = new ArrayList<>();
    private TCChatMsgListAdapter mChatMsgListAdapter;

    //直播对象
    private TXLivePlayer mTXLivePlayer;
    private TXLivePlayConfig mTXPlayConfig = new TXLivePlayConfig();

    //头像列表
    private TCUserAvatarListAdapter mAvatarListAdapter;

    private Handler mHandler = new Handler();

    private long mCurrentMemberCount = 0;
    private long mLastedPraisedTime = 0;

    private boolean mPausing = false;

    private TCChatRoomMgr chatRoom;

    //点赞动画
    private TCHeartLayout mHeartLayout;

    //弹幕
    private TCDanmuMgr mDanmuMgr;

    //手势动画
    private TCSwipeAnimationController mTCSwipeAnimationController;

    private boolean isAttetion = false;

    @Bind(R.id.rl_controllLayer)
    RelativeLayout mControllLayer;

    @Bind(R.id.background)
    ImageView mBgImageView;

    @Bind(R.id.head_icon)
    ImageView head_icon;

    @Bind(R.id.tv_user_name)
    TextView user_name;

    @Bind(R.id.iv_attention)
    ImageView iv_attention;

    @Bind(R.id.tv_look_num)
    TextView mMemberCount;

    @Bind(R.id.rv_user_avatar)
    RecyclerView mUserAvatarList;

    @Bind(R.id.tv_coin_num)
    TextView tv_coin_num;

    @Bind(R.id.tv_identity)
    TextView tv_identity;

    @Bind(R.id.im_msg_listview)
    ListView mListViewMsg;

    @Bind(R.id.sendgift_view)
    SendGift sendGiftView;

    @Bind(R.id.gift_animation_view)
    GiftAnimationView gift_animation_view;

    @Bind(R.id.tool_bar)
    RelativeLayout tool_bar;

    @Bind(R.id.wait)
    TextView wait;


    @Bind(R.id.iv_message_input)
    ImageView iv_message_input;

    @Bind(R.id.iv_send_gift)
    ImageView iv_send_gift;

    @Bind(R.id.flygift)
    FlyGift flyGift;

    @Bind(R.id.comein_animation)
    ComeInAnimation comein_animation;

    private int myGoldNum;
    private Map<String, String> dataMap;
    private String liveCode = "";
    private String coin = "";
    private int mRobotSize = 0;

    private GuideView guideViewMessage;
    private GuideView guideViewGift;
    private GuideView guideViewEmpty;
    private GuideView guideViewClear;
    private String room_title;
    String bg_img = "";
    private String shareMsg = "";

    public interface ShareCallBack{
        void shareCall(String shareMsg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        boolean isFirstLive = SharedPreferencesUtil.getBoolean(this , SharedPreferencesUtil.FIRST_OPEN_LIVE, true);
        if (isFirstLive){
            setGuideView();
        }

        mControllLayer.setVisibility(View.INVISIBLE);
        //初始化消息回调，当前存在：获取文本消息、用户加入/退出消息、群组解散消息、点赞消息、弹幕消息回调
        chatRoom = TCChatRoomMgr.getInstance();
        chatRoom.setMessageListener(this);
        getRoomId();

        //通知下线广播
        mLocalBroadcatManager = LocalBroadcastManager.getInstance(this);
        mExitBroadcastReceiver = new ExitBroadcastRecevier();
        mLocalBroadcatManager.registerReceiver(mExitBroadcastReceiver, new IntentFilter(TCConstants.EXIT_APP));

    }

    public static void goLivePlayerActivity(Context context,String roomId,String ucode){
        Intent intent = new Intent(context, LivePlayerActivity.class);
        intent.putExtra("room_id", roomId);
        intent.putExtra("ucode", ucode);
        context.startActivity(intent);
    }

    private void setGuideView(){
        final ImageView messageImage = new ImageView(this);
        messageImage.setImageResource(R.drawable.message_live);
        RelativeLayout.LayoutParams editParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        messageImage.setLayoutParams(editParams);

        final ImageView giftImage = new ImageView(this);
        giftImage.setImageResource(R.drawable.gift_live);
        RelativeLayout.LayoutParams earnParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        giftImage.setLayoutParams(earnParams);

        final ImageView emptyImage = new ImageView(this);
        emptyImage.setImageResource(R.drawable.empty_live);
        RelativeLayout.LayoutParams accountParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        emptyImage.setLayoutParams(accountParams);

        final ImageView clearImage = new ImageView(this);
        clearImage.setImageResource(R.drawable.clear_live);
        RelativeLayout.LayoutParams clearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clearImage.setLayoutParams(clearParams);

        guideViewMessage = GuideView.Builder
                .newInstance(this)
                .setTargetView(iv_message_input)
                .setCustomGuideView(messageImage)
                .setDirction(GuideView.Direction.RIGHT_TOP)
                .setShape(GuideView.MyShape.CIRCULAR)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideViewMessage.hide();
                        guideViewGift.show();
                    }
                })
                .build();

        guideViewGift = GuideView.Builder
                .newInstance(this)
                .setTargetView(iv_send_gift)
                .setCustomGuideView(giftImage)
                .setDirction(GuideView.Direction.TOP)
                .setShape(GuideView.MyShape.CIRCULAR)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideViewGift.hide();
                        guideViewEmpty.show();
                    }
                })
                .build();

        guideViewEmpty = GuideView.Builder
                .newInstance(this)//必须
                .setTargetView(wait)//必须
                .setCustomGuideView(emptyImage)//必须
                .setDirction(GuideView.Direction.BOTTOM)
                .setRadius(0)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideViewEmpty.hide();
                        guideViewClear.show();
                    }
                })
                .build();

        guideViewClear = GuideView.Builder
                .newInstance(this)
                .setTargetView(wait)
                .setCustomGuideView(clearImage)
                .setDirction(GuideView.Direction.TOP)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideViewClear.hide();
                    }
                })
                .build();

        guideViewMessage.show();
        SharedPreferencesUtil.putBoolean(this , SharedPreferencesUtil.FIRST_OPEN_LIVE, false);
    }

    /**进入直播间*/
    private void getRoomId(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            if (bundle.containsKey("room_id")){
                String room_id = bundle.getString("room_id");
                liveCode = bundle.getString("ucode");

                RoomProtocol roomProtocol = new RoomProtocol();

                LiveRequestParams params = new LiveRequestParams();
                params.put("roomId", room_id);

                roomProtocol.enterLive(params)
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(this.<String>bindToLifecycle())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                                /**解析成功*/
                                if (listMapByJson.size()>0){
                                    Map<String, String> map = listMapByJson.get(0);
                                    int code = Integer.parseInt(map.get("code"));
                                    /**请求成功*/
                                    if (code == 0){
                                        String data = map.get("data");
                                        ArrayList<Map<String, String>> listMapByJson1 = JsonUtil.getListMapByJson(data);
                                        if (listMapByJson1.size() >0 ){
                                            Map<String, String> map1 = listMapByJson1.get(0);
                                            initLiver(map1);
                                        }

                                    }else if (code == 20000006) {
                                        Toast.makeText(LiveZhiBoApplication.getApp(), "直播已结束", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else if (code == 111111110 || code == 11111113) {
                                        LoginManager.clearUser();
                                        LoginManager.goLoginActivity(LivePlayerActivity.this);
                                        finish();
                                    } else if (code == 11111122) {
                                        UIUtils.showToast("在别处登录，请重新登录");
                                        LoginManager.clearUser();
                                        LoginManager.goLoginActivity(LivePlayerActivity.this);
                                        finish();
                                    }else{
                                        UIUtils.showToast(map.get("msg"));
                                        finish();
                                    }
                                }else{
                                    UIUtils.showToast(s);
                                    finish();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.d("livelog","进入直播间失败");
                                UIUtils.showToast(R.string.net_error);
                                finish();

                            }
                        });
            }
        }else{
            UIUtils.showToast("房间不存在");
            finish();
        }
    }

    String sysMessage = TCConstants.SYSTEM_MESSAGE;
    //获取IM系统消息
    private void getImMessage(String gid){
        if (imProtocol == null)
            imProtocol = new ImProtocol();
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
                                for (int i = 0; i < data.size(); i++){
                                    sysMessage = data.get(i).get("");
                                    getSysTemMessage(sysMessage);
                                }
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else {

                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getImMessage(mGroupId);
                        Log.e("livelog","获取IM系统消息失败");
                    }
                });
    }

    private void getSysTemMessage(String msg){
        ChatEntity entity = new ChatEntity();
        entity.setSendName("系统消息:");
        entity.setContext(msg);
        entity.setIdentity("-1");
        entity.setType(TCConstants.SYSTEM_TYPE);
        notifyMsg(entity);
    }

    private void initData() {
        imProtocol = new ImProtocol();
        loginPresenter = LoginHelper.getInstance();
        roomProtocol = new RoomProtocol();
        userProtocol = new UserProtocol();
        giftProtocol = new GiftProtocol();

        if (loginPresenter != null)
            loginPresenter.setIMLoginCallback(this);
    }

    private void initLiver(Map<String, String> map1){
        user_name.setText(map1.get("nick_name"));
        number = map1.get("number99");
        mGroupId = map1.get("group_id");
        mRoomId = map1.get("room_id");
        anchor_id = map1.get("anchor_id");
        is_follow = map1.get("is_follow");
        room_title = map1.get("room_title");


        bg_img = map1.get("bg_img_url");
        //背景图
        TCUtils.blurBgPic(this, mBgImageView, bg_img, R.drawable.beijingtu);

        String playList = map1.get("playList");
        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(playList);
        Map<String, String> map = listMapByJson.get(0);

        mPlayUrl = map.get("flv");
        initData();
        initView();
        joinRoom();
        wait.setVisibility(View.GONE);
        mControllLayer.setVisibility(View.VISIBLE);
    }

    private boolean isIM = false;
    /**观众加入房间操作*/
    public void joinRoom() {
        chatRoom.setMessageListener(this);
        String oldRoomId = SPUtils.getString(SPUtils.LIVE_ROOM_ID);
        if (!TextUtils.isEmpty(oldRoomId) && !mGroupId.equals(oldRoomId)){
            chatRoom.quitGroupOldRoomId(oldRoomId);
        }
        //仅当直播时才进行执行加入直播间逻辑
        if (TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())){
            isIM = true;
            getImSig();
            Log.d("livelog","IM未登陆，现在去登陆");
        }else{
            chatRoom.joinGroup(mGroupId);
        }
        //开始拉流
        if (giftHandler != null) {
            giftHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startPlay();
                }
            }, 1000);
        }else{
            startPlay();
        }

    }

    @Override
    public void onJoinGroupCallback(int code, String msg) {
        if(code == 0){
            chatRoom.setMessageListener(this);
            getCoin();
            getImMessage(mGroupId);
            getGroupMembersList();
            SPUtils.putString(SPUtils.LIVE_ROOM_ID,mGroupId);
            Log.d("livelog", "加入房间成果" + msg);
        } else if (code == 6013 || code == 6014){
            //用户未成功登录,重新登录
            getImSig();
        } else if (code == 6012){
            showErrorAndQuit(TCConstants.ERROR_MSG_JOIN_GROUP_FAILED);
        }else {
            Log.d("livelog", "加入房间失败" + msg);
            //加入房间失败
            showErrorAndQuit(TCConstants.ERROR_MSG_JOIN_GROUP_FAILED);
        }
    }

    /**获取主播99币*/
    private void getCoin(){
        if (userProtocol == null){
            return;
        }
        LiveRequestParams params = new LiveRequestParams();
        params.put("ucode", liveCode);

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
                        getCoin();
                    }
                });
    }

    /**
     * 拉取用户信息
     */
    public void getGroupMembersList() {
        chatRoom.setMessageListener(this);
        chatRoom.getGroupMembers(new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Log.d("livelog", "拉取IM用户信息失败");
                getGroupMembersList();
            }

            @Override
            public void onSuccess(List<TIMUserProfile> result) {
                Log.d("livelog", "拉取IM用户信息成功");
                if (mAvatarListAdapter != null)
                for (TIMUserProfile timUserProfile : result) {
                    mAvatarListAdapter.addItem(new TCSimpleUserInfo(timUserProfile.getIdentifier(),
                            timUserProfile.getNickName(), timUserProfile.getFaceUrl()));
                    /**获取头像*/
                    if (number.equals(timUserProfile.getIdentifier())){
                        showHeadIcon(head_icon, timUserProfile.getFaceUrl());
                    }
                }
                /**获取当前人数*/
                mCurrentMemberCount = result.size();
//                mMemberCount.setText(String.format("%d", mCurrentMemberCount)+ "人");
                mMemberCount.setText("");
            }
        });
    }

    /**
     * 拉取IM群组人数
     */
    public void getGroupNumber(){
        chatRoom.getGroupMembers(new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(List<TIMUserProfile> result) {
                mCurrentMemberCount = result.size();
                mCurrentMemberCount += mRobotSize;
                mMemberCount.setText(String.format("%d", mCurrentMemberCount)+ "人");
            }
        });
    }

    /**
     * 初始化观看直播界面
     */
    private void initView() {
        mTCSwipeAnimationController = new TCSwipeAnimationController(this);
        mTCSwipeAnimationController.setAnimationView(mControllLayer);

        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mListViewMsg.setVisibility(View.VISIBLE);

        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        //消息输入框
        mInputTextMsgDialog = new TCInputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);
        //消息
        mChatMsgListAdapter = new TCChatMsgListAdapter(this, mListViewMsg, mArrayListChatEntity,giftHandler);
        mListViewMsg.setAdapter(mChatMsgListAdapter);
        //99号
        tv_identity.setText(number);

        head_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (number != null && !TextUtils.isEmpty(number)){
                    UserCardActivity.goCardActivity(LivePlayerActivity.this,number,true,"",false,false);
                    overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
                }
            }
        });

        //关注
        if (is_follow.equals("N")){
            iv_attention.setImageResource(R.drawable.attention);
            isAttetion = false;
        }else if (is_follow.equals("Y")){
            iv_attention.setImageResource(R.drawable.unattention);
            isAttetion = true;
        }
        //初始化观众头像列表
        mUserAvatarList.setVisibility(View.VISIBLE);
        mAvatarListAdapter = new TCUserAvatarListAdapter(this, "",giftHandler);
        mUserAvatarList.setAdapter(mAvatarListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserAvatarList.setLayoutManager(linearLayoutManager);

        //点赞
        mHeartLayout = (TCHeartLayout) findViewById(R.id.heart_layout);

        //弹幕
        IDanmakuView danmakuView = (IDanmakuView) findViewById(R.id.danmakuView);
        danmakuView.setVisibility(View.VISIBLE);
        mDanmuMgr = new TCDanmuMgr(this);
        mDanmuMgr.setDanmakuView(danmakuView);

        gift_animation_view.setHandler(giftHandler);
        getGiftList(false);//获取礼物的列表，这里只获取一次不用担心，如果获取失败，点击礼物按钮的时候会再次获取
        sendGiftView.setHideListener(LivePlayerActivity.this);

    }

    private void showHeadIcon(ImageView view, String avatar) {
        TCUtils.showPicWithUrl(this, view, avatar, R.mipmap.head);
    }

//    @Override
//    public void startActivity(Intent intent) {
//        super.startActivity(intent);
//
//    }

    /**开始拉流*/
    private void startPlay() {
        if (mTXLivePlayer == null) {
            mTXLivePlayer = new TXLivePlayer(this);
        }

        mTXLivePlayer.setPlayerView(mTXCloudVideoView);
        mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mTXLivePlayer.setPlayListener(this);
        mTXLivePlayer.setConfig(mTXPlayConfig);
//        mTXLivePlayer.setLogLevel(TXLiveConstants.LOG_LEVEL_DEBUG);
        TXLiveBase.setLogLevel(TXLiveConstants.LOG_LEVEL_DEBUG);

        int result = 0;

        result = mTXLivePlayer.startPlay(mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV);

        if (0 != result) {

//            Intent rstData = new Intent();

            if (-1 == result) {
                Log.d("livelog", TCConstants.ERROR_MSG_NOT_QCLOUD_LINK);
                showErrorAndQuit(TCConstants.ERROR_RTMP_PLAY_FAILED);
            } else {
                Log.d("livelog", TCConstants.ERROR_RTMP_PLAY_FAILED);
                showErrorAndQuit(TCConstants.ERROR_MSG_NOT_QCLOUD_LINK);
            }
            if (mTXCloudVideoView != null)
                mTXCloudVideoView.onPause();
            stopPlay(true);
            finish();
        }
    }

    /**停止拉流*/
    private void stopPlay(boolean clearLastFrame) {
        if (mTXLivePlayer != null) {
            mTXLivePlayer.setPlayListener(null);
            mTXLivePlayer.stopPlay(clearLastFrame);
        }
    }

    /**发消息弹出框*/
    private void inputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();

        lp.width = (display.getWidth()); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }

    /**错误弹框*/
    private void showErrorAndQuit(String errorMsg) {
        if (mTXCloudVideoView != null)
            mTXCloudVideoView.onPause();
//        chatRoom.removeMsgListener();

        stopPlay(true);
        if (!isDestroyed()) {
            new MyDialog(this).setMessage(errorMsg).setRightButtonState("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).setCanceledOnTouchOutside(false).setCancelable(false).show();
        }
    }

    public class ExitBroadcastRecevier extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TCConstants.EXIT_APP)) {
                TXLog.d("livelog", "player broadcastReceiver receive exit app msg");
                //在被踢下线的情况下，执行退出前的处理操作：关闭rtmp连接、退出群组
                mTXCloudVideoView.onPause();
                stopPlay(true);

//                chatRoom.removeMsgListener();
                stopPlay(true);
            }
        }
    }

    @OnClick({R.id.iv_attention, R.id.iv_message_input, R.id.iv_like, R.id.iv_share, R.id.btn_back,
            R.id.iv_send_gift,R.id.rl_controllLayer,R.id.jiujiub})
    void onClick(View view){
        switch (view.getId()){
            case R.id.iv_attention:
                iv_attention.setClickable(false);
                if (!isAttetion){
                    follow(anchor_id);
                }else {
                    unFollow(anchor_id);
                }
                break;
            case R.id.iv_message_input:
                inputMsgDialog();
                break;
            case R.id.iv_like:
                if (mHeartLayout != null) {
                    mHeartLayout.addFavor();
                }
                //点赞发送请求限制
                long currentTime = System.currentTimeMillis();
                if(mLastedPraisedTime == 0 || currentTime - mLastedPraisedTime > 1000) {
                    //向ChatRoom发送点赞消息
                    if (chatRoom != null){
                        chatRoom.sendMessage(TCConstants.IMCMD_PRAISE, "");
                        mLastedPraisedTime = currentTime;
                    }
                }
                break;
            case R.id.iv_share:
                UMengInfo.getUrlTool(this, user_name.getText().toString(),
                        umShareListener, mRoomId, true,room_title,bg_img,new ShareCallBack(){

                            @Override
                            public void shareCall(String shareMsg) {
                                LivePlayerActivity.this.shareMsg = shareMsg;
//                                showShareMsg();
                            }
                        });
                YMClick.onEvent(LivePlayerActivity.this,"live_share","1");
                break;
            case R.id.btn_back:
                back();
                break;
            case R.id.iv_send_gift:
                if (sendGiftView != null){
                    if (!sendGiftView.isDataSuccess){
                        getGiftList(false);
                    }
                    sendGiftView.show();
                    tool_bar.setVisibility(View.INVISIBLE);
                }
                YMClick.onEvent(LivePlayerActivity.this,"live_gift","1");
                break;
            case R.id.rl_controllLayer:
                if (mHeartLayout != null) {
                    mHeartLayout.addFavor();
                }
                //点赞发送请求限制
                long currentTime1 = System.currentTimeMillis();
                if(mLastedPraisedTime == 0 || currentTime1 - mLastedPraisedTime > 1000) {
                    //向ChatRoom发送点赞消息
                    if (chatRoom != null){
                        chatRoom.sendMessage(TCConstants.IMCMD_PRAISE, "");
                        mLastedPraisedTime = currentTime1;
                        onPraise(SPUtils.getString(SPUtils.USER_IDENTITY),SPUtils.getString(SPUtils.USER_NAME),SPUtils.getString(SPUtils.USER_LEVEL));
                    }
                }
                break;
            case R.id.jiujiub:
                FansContribution.goFansContributionByUser(LivePlayerActivity.this,liveCode);
                break;
            default:
                break;
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Toast.makeText(LivePlayerActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            shareCallBack(SPUtils.getString(SPUtils.USER_ID));
            showShareMsg();
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

    private void showShareMsg(){
        if (!TextUtils.isEmpty(shareMsg)){
//            ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(shareMsg);
//            Random random = new Random();
//            int i = random.nextInt(listMapByJson.size());
//            Map<String, String> map = listMapByJson.get(i);
//            String share = map.get("");
            if (chatRoom != null)
                chatRoom.sendMessage(TCConstants.IMCMD_SHARE_MSG,shareMsg);

            //左下角显示用户加入消息
            ChatEntity entity = new ChatEntity();
            entity.setSendName("直播消息");
//            if (nickname.equals(""))
            entity.setContext(SPUtils.getString(SPUtils.USER_NAME) + "分享了主播，"+ shareMsg);
//            else
//                entity.setContext(nickname + "关注了主播");
            entity.setLevel(SPUtils.getString(SPUtils.USER_LEVEL));
            entity.setIdentity(SPUtils.getString(SPUtils.USER_IDENTITY));
            entity.setType(TCConstants.MEMBER_ENTER);
            notifyMsg(entity);
        }
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
                                    sendGiftView.setHideListener(LivePlayerActivity.this);
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
                                    String send = Tools.map2Json(giftMap);
                                    chatRoom.sendMessage(TCConstants.IMCMD_GIFT,send);
                                }

                            }
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

    /**关注接口*/
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
                                chatRoom.sendMessage(TCConstants.IMCMD_FOLLOW,"");
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
                    }
                });
    }

    /**取消关注接口*/
    private void unFollow(String ucode){
        LiveRequestParams params = new LiveRequestParams();
        params.put("fid", ucode);

        userProtocol.getUnFollow(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {

                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                Log.d("livelog", "取消关注成功");
                                isAttetion = false;
                                iv_attention.setImageResource(R.drawable.attention);
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
                        Log.d("livelog", "取消关注失败");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        lessNum(mRoomId);
        finish();
    }

    /**退出房间操作*/
    private void lessNum(String mRoomId){
        LiveRequestParams params = new LiveRequestParams();
        params.put("roomId", mRoomId);
        if (roomProtocol != null){

        roomProtocol.lessOnline(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog", "退出成功");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d("livelog", "退出失败");
                    }
                });
        }
    }

    /**请求IM签名*/
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
                                    dataMap = dataList.get(0);
                                    goLoginIM(dataMap);
                                }
                            }
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("livelog","请求IM签名失败");
                    }
                });
    }

    /**登录IM*/
    private void goLoginIM(Map<String, String> dataMap) {
        if (dataMap != null)
            loginPresenter.imLogin(dataMap.get("uid"), dataMap.get("sig"));
        Log.d("livelog","IM 登陆");
    }

    @Override
    public void onIMLoginSuccess() {
        Log.d("livelog", "IM登录成功");
        if (null != mGroupId && !"".equals(mGroupId) && isIM) {
            isIM = false;
            if (chatRoom != null ){
                String oldRoomId = SPUtils.getString(SPUtils.LIVE_ROOM_ID);
                if (!TextUtils.isEmpty(oldRoomId) && !mGroupId.equals(oldRoomId)){
                    chatRoom.quitGroupOldRoomId(oldRoomId);
                }

                chatRoom.setMessageListener(this);
                chatRoom.joinGroup(mGroupId);
            }
            Log.d("livelog", "加入群聊");
        }
    }

    @Override
    public void onIMLoginError(int code, String msg) {
        Log.d("livelog", "IM登录失败");
        if (dataMap != null)
            goLoginIM(dataMap);
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loginPresenter != null)
            loginPresenter.setIMLoginCallback(this);
        if (mTXCloudVideoView != null)
            mTXCloudVideoView.onResume();
        if (mDanmuMgr != null) {
            mDanmuMgr.resume();
        }
        if (mPausing) {
            mPausing = false;

//            if (Build.VERSION.SDK_INT >= 23) {
//                startPlay();
//            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTXCloudVideoView != null) {
//            mTXCloudVideoView.onPause();
        }
        if (mDanmuMgr != null) {
            mDanmuMgr.pause();
        }

        mPausing = true;

//        if (Build.VERSION.SDK_INT >= 23) {
//            stopPlay(false);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (gift_animation_view != null){
            gift_animation_view.destroyed();
        }
        if (comein_animation != null){
            comein_animation.destroy();
        }
        if (loginPresenter != null)
            loginPresenter.removeIMLoginCallback();
        isFinish = true;
        if (giftHandler != null) {
            giftHandler.removeCallbacksAndMessages(null);
        }
        giftHandler = null;
        if (mDanmuMgr != null) {
            mDanmuMgr.destroy();
            mDanmuMgr = null;
        }

        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
        }
        quitRoom();
        if (mLocalBroadcatManager != null)
            mLocalBroadcatManager.unregisterReceiver(mExitBroadcastReceiver);
    }

    public void quitRoom() {
        if (chatRoom != null ){
            if (!TextUtils.isEmpty(mGroupId)) {
                chatRoom.quitGroup(mGroupId);
                chatRoom.removeMsgListener();
            }
        }
        stopPlay(true);
    }

    private void notifyMsg(final ChatEntity entity) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                if(entity.getType() == TCConstants.PRAISE) {
//                    if(mArrayListChatEntity.contains(entity))
//                        return;
//                }
                mArrayListChatEntity.add(entity);
                if (mChatMsgListAdapter != null)
                    mChatMsgListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
        if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {

        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {

            showErrorAndQuit(TCConstants.ERROR_MSG_NET_DISCONNECTED);

        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {

        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    /**
     * TextInputDialog发送回调
     * @param msg 文本信息
     * @param danmuOpen 是否打开弹幕
     */
    @Override
    public void onTextSend(String msg, boolean danmuOpen) {
        if (msg.length() == 0)
            return;
        try {
            byte[] byte_num = msg.getBytes("utf8");
            if (byte_num.length > 160) {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        //消息回显,###由于添加了敏感词过滤所以现在调到发送成功的地方去显示了
//        ChatEntity entity = new ChatEntity();
//        entity.setSendName(SPUtils.getString(SPUtils.USER_NAME) + ":");
//        entity.setContext(msg);
//        entity.setIdentity(SPUtils.getString(SPUtils.USER_IDENTITY));
//        entity.setType(TCConstants.TEXT_TYPE);
//        notifyMsg(entity);
//        Log.d("livelog", msg);

        if (danmuOpen) {
//            if (mDanmuMgr != null) {
//                mDanmuMgr.addDanmu(SPUtils.getString(SPUtils.USER_AVATAR), SPUtils.getString(SPUtils.USER_NAME), msg);
//            }
            chatRoom.sendMessage(TCConstants.IMCMD_DANMU, msg);
        } else {
            chatRoom.sendMessage(TCConstants.IMCMD_PAILN_TEXT, msg);
        }
    }

    @Override
    public void onQuitGroupCallback(int code, String msg) {
        if(code == 0){
            Log.d("livelog", "onquit group success id :" + msg);
            //将退出群组操作移动至ondestory操作中
        } else {
            Log.d("livelog", "onquit group failed" + msg);
            //showErrorAndQuit("退出群组失败");
        }
    }

    @Override
    public void onSendMsgCallback(int code,int msgType, TIMMessage timMessage,String parm) {
        //消息发送成功后回显
        if(code == 0) {
            TIMElemType elemType =  timMessage.getElement(0).getType();
            if(elemType == TIMElemType.Text) {
                Log.d("livelog", "onSendTextMsgsuccess:" + code);
                switch (msgType){
                    case TCConstants.IMCMD_PAILN_TEXT:
                        ChatEntity entity1 = new ChatEntity();
                        entity1.setSendName(SPUtils.getString(SPUtils.USER_NAME) + ":");
                        entity1.setContext(parm);
                        entity1.setIdentity(SPUtils.getString(SPUtils.USER_IDENTITY));
                        entity1.setLevel(SPUtils.getString(SPUtils.USER_LEVEL));
                        entity1.setType(TCConstants.TEXT_TYPE);
                        notifyMsg(entity1);
                        Log.d("livelog", parm);
                        break;
                }

            } else if(elemType == TIMElemType.Custom) {//todo
                //custom消息存在消息回调,此处显示成功失败
                Log.d("livelog", "onSendCustomMsgsuccess:" + code);
                switch (msgType){
                    case TCConstants.IMCMD_GIFT:
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(parm);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if (map != null){
                                if ("1".equals(map.get("continue"))){
                                    if (!LivePlayerActivity.this.isDestroyed()) {
                                        gift_animation_view.sendGift(map);
                                    }
                                }else{
                                    if (!LivePlayerActivity.this.isDestroyed()) {
                                        gift_animation_view.sendBigGift(map);
                                    }
                                }

                                ChatEntity entity = new ChatEntity();
                                String nickname = map.get("user_name");
                                String userId = map.get("user_id");
                                if (nickname.equals("")){
                                    entity.setSendName(userId + ":");
                                }else {
                                    entity.setSendName(nickname+ ":");
                                }
                                entity.setContext("送了一个" + map.get("gift_name"));
                                entity.setIdentity(SPUtils.getString(SPUtils.USER_IDENTITY));
                                entity.setLevel(SPUtils.getString(SPUtils.USER_LEVEL));
                                entity.setType(TCConstants.SEND_GIFT);
                                notifyMsg(entity);
                            }
                        }

                        break;

                    case TCConstants.IMCMD_FOLLOW:
                            //左下角显示用户加入消息
                            ChatEntity entity = new ChatEntity();
                        entity.setSendName("直播消息");
//                        if (nickname.equals(""))
//                            entity.setContext(identifier + "关注了直播");
//                        else
//                            entity.setContext(nickname + "关注了直播");
//                        entity.setIdentity(identifier);
                        entity.setContext(SPUtils.getString(SPUtils.USER_NAME )+ "关注了主播");
                        entity.setIdentity(SPUtils.getString(SPUtils.USER_IDENTITY));
                        entity.setType(TCConstants.MEMBER_ENTER);
                        entity.setLevel(SPUtils.getString(SPUtils.USER_LEVEL));
                        notifyMsg(entity);
                        break;


                    case TCConstants.IMCMD_DANMU:
                        ChatEntity entity2 = new ChatEntity();
                        entity2.setSendName(SPUtils.getString(SPUtils.USER_NAME) + ":");
                        entity2.setContext(parm);
                        entity2.setIdentity(SPUtils.getString(SPUtils.USER_IDENTITY));
                        entity2.setLevel(SPUtils.getString(SPUtils.USER_LEVEL));
                        entity2.setType(TCConstants.TEXT_TYPE);
                        notifyMsg(entity2);
                        if (mDanmuMgr != null) {
                            mDanmuMgr.addDanmu(SPUtils.getString(SPUtils.USER_AVATAR), SPUtils.getString(SPUtils.USER_NAME), parm);
                        }
                        break;
                }

            }
        } else {
            //code = 10017被禁言
            Log.d("livelog", "onSendMsgfail:" + code);
//            TIMElemType elemType =  timMessage.getElement(0).getType();
            switch (msgType) {
                case TCConstants.IMCMD_PAILN_TEXT:
                    if (code==80001){
                        UIUtils.showToast("输入内容含有敏感词");
                    }else if(code == 10017){
                        UIUtils.showToast("您已被主播禁言");
                    }
                    break;

                case TCConstants.IMCMD_DANMU:
                    if (code==80001){
                        UIUtils.showToast("输入内容含有敏感词");
                    }else if(code == 10017){
                        UIUtils.showToast("您已被主播禁言");
                    }
                    break;
                case TCConstants.IMCMD_GIFT:
                    ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(parm);
                    if (listMapByJson.size()>0){
                        Map<String, String> map = listMapByJson.get(0);
                        if (map != null){
                            if ("1".equals(map.get("continue"))){
                                if (!LivePlayerActivity.this.isDestroyed()) {
                                    gift_animation_view.sendGift(map);
                                }
                            }else{
                                if (!LivePlayerActivity.this.isDestroyed()) {
                                    gift_animation_view.sendBigGift(map);
                                }
                            }

                            ChatEntity entity = new ChatEntity();
                            String nickname = map.get("user_name");
                            String userId = map.get("user_id");
                            if (nickname.equals("")){
                                entity.setSendName(userId + ":");
                            }else {
                                entity.setSendName(nickname+ ":");
                            }
                            entity.setContext("送了一个" + map.get("gift_name"));
                            entity.setIdentity(SPUtils.getString(SPUtils.USER_IDENTITY));
                            entity.setLevel(SPUtils.getString(SPUtils.USER_LEVEL));
                            entity.setType(TCConstants.SEND_GIFT);
                            notifyMsg(entity);
                        }
                    }
                    break;
                default:
                    if (code==80001){
                        UIUtils.showToast("输入内容含有敏感词");
                    }else if(code == 10017){
                        UIUtils.showToast("您已被主播禁言");
                    }
                    break;
            }
        }
    }

    @Override
    public void onReceiveTextMsg(String text, String name,String identity,String level, int type) {
        ChatEntity entity = new ChatEntity();
        entity.setSendName(name + ":");
        entity.setContext(text);
        entity.setIdentity(identity);
        entity.setType(type);
        entity.setLevel(level);

        notifyMsg(entity);
    }

    @Override
    public void onMemberJoin(String userId, String nickname, String faceurl,String level) {

        //更新头像列表 返回false表明已存在相同用户，将不会更新数据
        if (mAvatarListAdapter == null) {
                return;
        }
        if (!mAvatarListAdapter.addItem(new TCSimpleUserInfo(userId, nickname, faceurl)))
            return;

//        mCurrentMemberCount++;
//        mMemberCount.setText(String.format(Locale.CHINA,"%d",mCurrentMemberCount) + "人");
        userAvatarListScrollto();
        //IM人数
        getGroupNumber();
//        int itemCount = mAvatarListAdapter.getItemCount();
//        mCurrentMemberCount = itemCount;23423423423423
//        mMemberCount.setText(String.format("%d", mCurrentMemberCount)+ "人");

        //左下角显示用户加入消息
        ChatEntity entity = new ChatEntity();
        entity.setSendName("直播消息");
        if (nickname.equals(""))
            entity.setContext(userId + "加入直播");
        else
            entity.setContext(nickname + "加入直播");
        entity.setIdentity(userId);
        entity.setLevel(level);
        entity.setType(TCConstants.MEMBER_ENTER);
        notifyMsg(entity);

        comein_animation.showLevel(userId,level,faceurl,nickname);
    }

    @Override
    public void onMemberQuit(String userId, String nickname) {
//        if(mCurrentMemberCount > 0)
//            mCurrentMemberCount--;
//        else
//            Log.d("livelog", "接受多次退出请求，目前人数为负数");
//
//        mMemberCount.setText(String.format(Locale.CHINA,"%d",mCurrentMemberCount) + "人");
        if (mAvatarListAdapter==null){
            return;
        }
        mAvatarListAdapter.removeItem(userId);

        //IM人数
        getGroupNumber();

//        int itemCount = mAvatarListAdapter.getItemCount();
//        mCurrentMemberCount = itemCount;234234234234
//        mMemberCount.setText(String.format("%d", mCurrentMemberCount)+ "人");


//        ChatEntity entity = new ChatEntity();
//        entity.setSendName("通知");
//        if (nickname.equals(""))
//            entity.setContext(userId + "退出直播");
//        else
//            entity.setContext(nickname + "退出直播");
//        entity.setType(TCConstants.MEMBER_EXIT);
//        notifyMsg(entity);
    }

    @Override
    public void onReceiveDanmu(String text, String nickname, String faceurl,String identity,String level) {
        ChatEntity entity = new ChatEntity();
        entity.setSendName(nickname+":");
        entity.setContext(text);
        entity.setIdentity(identity);
        entity.setLevel(level);
        entity.setType(TCConstants.TEXT_TYPE);

        notifyMsg(entity);
        if (mDanmuMgr != null) {
            mDanmuMgr.addDanmu(faceurl, nickname,text);
        }
    }

    @Override
    public void onGroupDelete(String groupId) {
        if (!TextUtils.isEmpty(mGroupId) && mGroupId.equals(groupId)) {
            showErrorAndQuit(TCConstants.ERROR_MSG_LIVE_STOPPED);
        }
    }

    @Override
    public void onPraise(String userId, String nickname,String level) {
        ChatEntity entity = new ChatEntity();


        if (nickname.equals(""))
            entity.setSendName(userId + ":");
        else
            entity.setSendName(nickname + ":");

        int unicode = 0x1F497;
        String emojiString = getEmojiStringByUnicode(unicode);
        entity.setContext("点亮了" + emojiString);
        if (mHeartLayout != null) {
            mHeartLayout.addFavor();
        }
        entity.setIdentity(userId);
        entity.setLevel(level);
        entity.setType(TCConstants.PRAISE);
        notifyMsg(entity);
    }

    private String getEmojiStringByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    /**
     * 收到礼物消息回调
     */
    @Override
    public void onGiftSend(String giftMsg, String id, String name, String url,String level) {
        ArrayList<Map<String, String>> giftList = JsonUtil.getListMapByJson(giftMsg);
        if (giftList.size()>0){
            Map<String, String> map = giftList.get(0);
            if (map.containsKey("B")){
                String b = map.get("B");
                if (!TextUtils.isEmpty(b)){
                    coin = b;
                    tv_coin_num.setText(coin + "  >");
                }
            }
            map.put("user_id",id);
            map.put("user_name",name);
            map.put("user_avatar",url);
            if ("1".equals(map.get("continue"))){
                if (!LivePlayerActivity.this.isDestroyed()) {
                    gift_animation_view.sendGift(map);
                }
            }else{
//                UIUtils.showToast("送了一个" + map.get("gift_name"));
                if (!LivePlayerActivity.this.isDestroyed()) {
                    gift_animation_view.sendBigGift(map);
                }
            }


            ChatEntity entity = new ChatEntity();

            String nickname = map.get("user_name");
            String userId = map.get("user_id");
            if (nickname.equals(""))
                entity.setSendName(userId + ":");
            else
                entity.setSendName(nickname + ":");

            entity.setContext("送了一个"+map.get("gift_name"));
            entity.setIdentity(id);
            entity.setLevel(level);
            entity.setType(TCConstants.SEND_GIFT);
            notifyMsg(entity);
        }
    }

    @Override
    public void onAddRobot(Robot robot){
        String nikeName = robot.getNickname();
        String bg_img_url = robot.getFull_head_image();
        String user_id = robot.getUser_id();
        String identity = robot.getIdentity();
        onRobotJoin(identity,user_id,nikeName,bg_img_url,true);
//        mRobotSize ++;
//        getGroupNumber();
//        int itemCount = mAvatarListAdapter.getItemCount();
//        mCurrentMemberCount = itemCount;234234234
//        mMemberCount.setText(String.format("%d", mCurrentMemberCount)+ "人");
    }


    @Override
    public void onSendRobotCallBack(int code, int cmd, Object o, Robot robot) {

    }

    @Override
    public void onRobotAll(String robotsMsg,String size, String identifier, String nickname, String faceUrl) {
        mRobotSize = Integer.parseInt(size);
        getGroupNumber();
        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(robotsMsg);
        if (listMapByJson.size()>0){
            for (int i=0;i<listMapByJson.size();i++){
                Map<String, String> map = listMapByJson.get(i);
                String nickName = map.get(TCConstants.ROBOT_NAME);
                String img_url = map.get(TCConstants.ROB0T_FULLHEADIMG);
                String user_id = map.get(TCConstants.ROBOT_USER_ID);
                String identity = map.get(TCConstants.ROBOT_IDENTITY);
                onRobotJoin(identity,user_id,nickName,img_url,false);
            }
        }
    }

    @Override
    public void onRobotPraise(Robot robot) {
        onPraise(robot.getIdentity(),robot.getNickname(),"1");
    }

    @Override
    public void onRobotChat(Robot robot) {
        onReceiveTextMsg(robot.getLanguage(), robot.getNickname(),robot.getIdentity(),"1", TCConstants.TEXT_TYPE);
    }

    @Override
    public void onFollow(String identifier, String nickname,String level) {
        //左下角显示用户加入消息
        ChatEntity entity = new ChatEntity();
        entity.setSendName("直播消息");
        if (nickname.equals(""))
            entity.setContext(identifier + "关注了主播");
        else
            entity.setContext(nickname + "关注了主播");
        entity.setIdentity(identifier);
        entity.setType(TCConstants.MEMBER_ENTER);
        entity.setLevel(level);
        notifyMsg(entity);
    }

    @Override
    public void onShutUp(String msg) {
        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(msg);
        if (listMapByJson.size()>0){
            Map<String, String> map = listMapByJson.get(0);
            String identity = map.get("shutup_id");
            String name = map.get("shutup_name");
            //左下角显示用户加入消息
            ChatEntity entity = new ChatEntity();
            entity.setSendName("直播消息");
//            if (nickname.equals(""))
                entity.setContext(name + "被禁言了");
//            else
//                entity.setContext(nickname + "关注了主播");
            entity.setIdentity(identity);
            entity.setType(TCConstants.MEMBER_ENTER);
            notifyMsg(entity);
        }
    }

    @Override
    public void onShowFlyGift(String str) {
        flyGift.setFlyGift(str);
    }

    @Override
    public void onShowShareMsg(String identifier, String nickname, String level, String share) {
        //左下角显示用户加入消息
        ChatEntity entity = new ChatEntity();
        entity.setSendName("直播消息");
//            if (nickname.equals(""))
        entity.setContext(nickname + "分享了主播，"+ share);
//            else
//                entity.setContext(nickname + "关注了主播");
        entity.setLevel(level);
        entity.setIdentity(identifier);
        entity.setType(TCConstants.MEMBER_ENTER);
        notifyMsg(entity);
    }

    private void userAvatarListScrollto(){
        mUserAvatarList.scrollToPosition(0);
    }

    /**
     * 机器人加入房间
     * @param userId
     * @param nickname
     * @param faceurl
     */
    public void onRobotJoin(String identity ,String userId, String nickname, String faceurl,boolean isShowJoinMsg) {

        //更新头像列表 返回false表明已存在相同用户，将不会更新数据
        if (mAvatarListAdapter == null) {
            return;
        }
        if (!mAvatarListAdapter.addItem(new TCSimpleUserInfo(identity, nickname, faceurl)))
            return;

//        mCurrentMemberCount++;
//        mMemberCount.setText(String.format(Locale.CHINA,"%d",mCurrentMemberCount) + "人");
        userAvatarListScrollto();
//        int itemCount = mAvatarListAdapter.getItemCount();
//        mCurrentMemberCount = itemCount;
//        mMemberCount.setText(String.format("%d", mCurrentMemberCount)+ "人");

        //左下角显示用户加入消息
        if (isShowJoinMsg) {
            //IM人数
            mRobotSize++;
            getGroupNumber();
            ChatEntity entity = new ChatEntity();
            entity.setSendName("直播消息");
            if (nickname.equals(""))
                entity.setContext(userId + "加入直播");
            else
                entity.setContext(nickname + "加入直播");
            entity.setType(TCConstants.MEMBER_ENTER);
            entity.setIdentity(identity);
            notifyMsg(entity);
        }
    }

    /**
     * 右滑全屏逻辑
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {


        if (View.VISIBLE == sendGiftView.getVisibility()){

        }else{
            //保证recyclerView与父View滑动不冲突
            if(mTCSwipeAnimationController != null && ev.getRawY() > mScreenHeight * 0.17)
                mTCSwipeAnimationController.processEvent(ev);
        }

        return super.dispatchTouchEvent(ev);
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

    private void goToPay(){
        startActivityForResult(new Intent(this, AccountActivity.class),101);
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
                                startActivity(new Intent(LivePlayerActivity.this,AccountActivity.class));
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

    @Override
    public void hide() {
        sendGiftView.setSendEnable(false);
        tool_bar.setVisibility(View.VISIBLE);
        giftMap = null;
        giftNum = 0;
        sendGiftView.setRefresh(-1,-1);
    }

    @Override
    public void onlyHideView() {
        sendGiftView.setSendEnable(false);
        tool_bar.setVisibility(View.VISIBLE);
        giftMap = null;
        giftNum = 0;
//        sendGiftView.setRefresh(-1,-1);
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
//                    YMClick.onEvent(LivePlayerActivity.this,"live_gift",giftMap.get("gift_id"),"1");
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
                                YMClick.onEvent(LivePlayerActivity.this,YMClick.SEND_GIFT,giftMap.get("gift_id"),"1");
                            }else{
                                //发送礼物
                                if (giftNum >0 && giftMap != null){
                                    sendGift(giftMap,giftNum,giftMap.get("gift_id"),mRoomId,anchor_id);
                                    YMClick.onEvent(LivePlayerActivity.this,YMClick.SEND_GIFT,giftMap.get("gift_id"),"1");
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
                           YMClick.onEvent(LivePlayerActivity.this, YMClick.SEND_GIFT, giftMap.get("gift_id"), "1");
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
                    YMClick.onEvent(LivePlayerActivity.this,"live_deposit","1");
                    break;

                case TCConstants.OPEN_USER_BY_IDENTITY:
                    Object objs = message.obj;
                    String identity = (String) objs;
                    Log.d("livelog",identity);
//                    Intent intent = new Intent(LivePlayerActivity.this, UserCardActivity.class);
//                    intent.putExtra(TCConstants.UCODE, identity);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
                    if (number != null && number.equals(identity)){
                        UserCardActivity.goCardActivity(LivePlayerActivity.this,identity,true,"",false,false);
                    }else{
                        UserCardActivity.goCardActivity(LivePlayerActivity.this,identity,false,"",false,false);
                    }
                    overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
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

    @Subscribe
    public void IsFollowPlayer(FollowPlayerEvent event){
        if (event.isFollow()){
            iv_attention.setImageResource(R.drawable.unattention);
            isAttetion = true;
            chatRoom.sendMessage(TCConstants.IMCMD_FOLLOW,"");
        }else {
            iv_attention.setImageResource(R.drawable.attention);
            isAttetion = false;
        }
    }
}
