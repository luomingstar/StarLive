package com.a99live.zhibo.live.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.user.Approve;
import com.a99live.zhibo.live.activity.user.FansContribution;
import com.a99live.zhibo.live.adapter.TCChatMsgListAdapter;
import com.a99live.zhibo.live.adapter.TCUserAvatarListAdapter;
import com.a99live.zhibo.live.event.ShowHomeEvent;
import com.a99live.zhibo.live.event.ShutUpEvent;
import com.a99live.zhibo.live.event.TagEvent;
import com.a99live.zhibo.live.gift.ComeInAnimation;
import com.a99live.zhibo.live.gift.FlyGift;
import com.a99live.zhibo.live.gift.GiftAnimationView;
import com.a99live.zhibo.live.model.ChatEntity;
import com.a99live.zhibo.live.model.Robot;
import com.a99live.zhibo.live.model.TCSimpleUserInfo;
import com.a99live.zhibo.live.model.UMengInfo;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.presenter.LocationHelper;
import com.a99live.zhibo.live.presenter.LoginHelper;
import com.a99live.zhibo.live.presenter.TCChatRoomMgr;
import com.a99live.zhibo.live.presenter.TCDanmuMgr;
import com.a99live.zhibo.live.presenter.UploadHelper;
import com.a99live.zhibo.live.protocol.ImProtocol;
import com.a99live.zhibo.live.protocol.NewRoomProtocol;
import com.a99live.zhibo.live.protocol.RobotProtocol;
import com.a99live.zhibo.live.protocol.RoomProtocol;
import com.a99live.zhibo.live.protocol.ShareProtocol;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.TCSwipeAnimationController;
import com.a99live.zhibo.live.utils.TCUtils;
import com.a99live.zhibo.live.utils.Tools;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.MyDialog;
import com.a99live.zhibo.live.view.TCInputTextMsgDialog;
import com.a99live.zhibo.live.view.customviews.BeautyDialogFragment;
import com.a99live.zhibo.live.view.customviews.RoundCornerImageView;
import com.a99live.zhibo.live.view.customviews.TCHeartLayout;
import com.a99live.zhibo.live.view.weight.MyProgressDialog;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
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
import com.tencent.upload.task.data.FileInfo;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
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
 * Created by fuyk on 2016/11/7.
 */

public class LivePublisherNewActivity extends BaseActivity implements ITXLivePushListener,
        BeautyDialogFragment.SeekBarCallback, TCInputTextMsgDialog.OnTextSendListener,
        TCChatRoomMgr.TCChatRoomListener, LoginHelper.IMLoginCallback, LocationHelper.OnLocationListener,
        UploadHelper.OnUploadListener , TakePhoto.TakeResultListener, InvokeListener {

    //被踢下线广播监听
    private LocalBroadcastManager mLocalBroadcatManager;
    private BroadcastReceiver mExitBroadcastReceiver;

    private ImProtocol imProtocol;
    private RoomProtocol roomProtocol;
    private NewRoomProtocol newRoomProtocol;

    private float mScreenHeight;

    private String roomId = "";
//    private String room_id = "";
    private String live_code = "";
    private String group_id = "";
    /**推流地址*/
    private String mPushUrl = "";

    /**权限*/
    private boolean mPermission = false;

    /**房间总数*/
    private long lTotalMemberCount = 0;

    /**送心数*/
    private long lHeartCount = 0;//以后可能有用，现在不删

    /**计时*/
    private long mSecond = 0;
    private Timer mBroadcastTimer;
    private BroadcastTimerTask mBroadcastTimerTask;

    /**聊天室人数*/
    private long mCurrentMemberCount = 0;

    /**美颜和美白*/
    private int mBeautyLevel = 100;
    private int mWhiteningLevel = 50;

    /**闪光灯*/
    private boolean mFlashOn = false;

    /**滑动动画控制类*/
    private TCSwipeAnimationController mTCSwipeAnimationController;

    /**IM类*/
    private TCChatRoomMgr chatRoom;
    private LoginHelper loginPresenter;

    /**发送消息框*/
    private TCInputTextMsgDialog mInputTextMsgDialog;
    /**IM消息列表*/
    private ArrayList<ChatEntity> mArrayListChatEntity = new ArrayList<>();
    private TCChatMsgListAdapter mChatMsgListAdapter;

    /**头像列表*/
    private TCUserAvatarListAdapter mAvatarListAdapter;

    /**美颜美白*/
    private BeautyDialogFragment mBeautyDialogFragment;

    /**点赞动画*/
    private TCHeartLayout mHeartLayout;

    /**弹幕*/
    private TCDanmuMgr  mDanmuMgr;

    private Handler mHandler = new Handler();

    @Bind(R.id.rl_publish)
    RelativeLayout rl_publish;

    @Bind(R.id.iv_gps)
    ImageView iv_gps;

    /**所在地*/
    @Bind(R.id.tv_local)
    TextView tv_local;

    @Bind(R.id.iv_exit)
    ImageView iv_exit;

    //分享的按钮
    @Bind(R.id.layout_share)
    LinearLayout layout_share;

    @Bind(R.id.iv_circle_Friends)
    ImageView circle_friends;

    @Bind(R.id.iv_wexin_friend)
    ImageView weixin_friend;

    @Bind(R.id.iv_qq)
    ImageView qq;

    @Bind(R.id.iv_weibo)
    ImageView weibo;

    @Bind(R.id.tv_begin_live)
    TextView tv_begin_live;

    /**标题*/
    @Bind(R.id.et_live_title)
    EditText et_live_title;

    @Bind(R.id.ll_live_agreement)
    LinearLayout live_agreement;

    /**主界面*/
    @Bind(R.id.rl_controllLayer)
    RelativeLayout mControllLayer;
    /**头像*/
    @Bind(R.id.iv_head_icon)
    ImageView mHeadIcon;
    /**小红点*/
    @Bind(R.id.iv_record_ball)
    ImageView mRecordBall;
    /**直播时间*/
    @Bind(R.id.tv_broadcasting_time)
    TextView mBroadcastTime;
    /**房间人数*/
    @Bind(R.id.tv_member_counts)
    TextView mMemberCount;

    @Bind(R.id.tv_identity)
    TextView tv_identity;

    /**头像列表*/
    @Bind(R.id.rv_user_avatar)
    RecyclerView mUserAvatarList;
    /**消息列表*/
    @Bind(R.id.im_msg_listview)
    ListView mListViewMsg;

    /**功能*/
    @Bind(R.id.function_bg)
    LinearLayout function_bg;

    /**功能按键*/
    @Bind(R.id.tool_bar)
    LinearLayout tool_bar;

    /**闪光灯*/
//    @Bind(R.id.flash_btn)
//    LinearLayout mFlashView;

    /**礼物*/
    @Bind(R.id.gift_animation_view)
    GiftAnimationView gift_animation_view;

    @Bind(R.id.tv_coin_num)
    TextView tv_coin_num;

    @Bind(R.id.check_box)
    CheckBox checkBox;

    //倒计时动画
//    @Bind(R.id.action_live)
//    TextView action_live;
    //倒计时动画
    @Bind(R.id.ready)
    ImageView ready;

    @Bind(R.id.live_bg)
    RoundCornerImageView mLiveBg;

    @Bind(R.id.tv_tag)
    TextView tv_tag;

    @Bind(R.id.flygift)
    FlyGift flyGift;

    @Bind(R.id.comein_animation)
    ComeInAnimation comein_animation;

    private String coin;

    /**推流对象*/
    private TXLivePusher mTXLivePusher;
    /**推流自定义*/
    private TXLivePushConfig mTXPushConfig = new TXLivePushConfig();
    /**预览界面*/
    private TXCloudVideoView mTXCloudVideoView;

    private ImageView[] shares;
    /**主播退出时的弹窗*/
    private LinearLayout mDetailDialog;
    /**直播开启成功*/
    private boolean isPushSuccess = false;
    private RelativeLayout open_live;
    private String city = "";

    //上传封面图
    private static final String TAG = TakePhotoActivity.class.getName();
    private TakePhoto takePhoto;
    private Dialog mPicChsDialog;
    private static final int CAPTURE_IMAGE_CAMERA = 100;
    private static final int IMAGE_STORE = 200;
    private static final int CROP_CHOOSE = 10;
    private Uri fileUri, cropUri;
    private UploadHelper uploadHelper;
    private InvokeParam invokeParam;
    private String mLiveBgFileId;//背景图id

    private static final int LIVE_TAG = 999;

    private List<String> shutUpGroupIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_new_publish);
        ButterKnife.bind(this);
        /**初始化IM消息处理类*/
        chatRoom = TCChatRoomMgr.getInstance();
        chatRoom.setMessageListener(this);
        loginPresenter = LoginHelper.getInstance();
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        initView();
        initData();
        //打开摄像头进行画面预览
        startPublish();
        //定位
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (LocationHelper.checkLocationPermission(LivePublisherNewActivity.this)) {
                    if (!LocationHelper.getMyLocation(LivePublisherNewActivity.this, LivePublisherNewActivity.this)) {
                        tv_local.setText(getString(R.string.text_live_lbs_fail));
                        Toast.makeText(getApplicationContext(), "定位失败，请查看是否打开GPS", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 1000);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    /**获取99币*/
    private void getCoin(){
        LiveRequestParams params = new LiveRequestParams();
        UserProtocol userProtocol = new UserProtocol();
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

                    }
                });
    }

    private int getAVPathCount = 0;
    /**获取推流地址*/
    private void getAVPath() {
        LiveRequestParams params = new LiveRequestParams();
        getAVPathCount++;
        newRoomProtocol.getStream(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))) {
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(map.get("data"));
                                if (dataList.size() > 0) {
                                    Map<String, String> dataMap = dataList.get(0);
                                    mPushUrl = dataMap.get("avpath");
                                    live_code = dataMap.get("live_code");
                                    getIM();
                                }
                            }else {
                                if (getAVPathCount<3) {
                                    getAVPath();
                                }else{
                                    UIUtils.showToast(R.string.net_error);
                                    finish();
                                }
                            }
                        }else {
                            if (getAVPathCount<3) {
                                getAVPath();
                            }else{
                                UIUtils.showToast(R.string.net_error);
                                finish();
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (getAVPathCount<3) {
                            getAVPath();
                        }else{
                            UIUtils.showToast(R.string.net_error);
                            finish();
                        }
                    }
                });
    }

    private void initData() {
        imProtocol = new ImProtocol();
        roomProtocol = new RoomProtocol();
        newRoomProtocol = new NewRoomProtocol();

        //检测推流权限
        mPermission = checkPublishPermission();
        sendRobots = new ArrayList<>();
        uploadHelper = new UploadHelper(this, this);
        initPhotoDialog();
    }

    private void initView() {
//        circle_friends.setSelected(true);//现在不要默认分享
        mControllLayer.setVisibility(View.GONE);
        open_live = (RelativeLayout) findViewById(R.id.open_live);
        open_live.setVisibility(View.VISIBLE);
        open_live.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        //动画滑动
        mTCSwipeAnimationController = new TCSwipeAnimationController(this);
        mTCSwipeAnimationController.setAnimationView(mControllLayer);
        //获取头像列表
        mAvatarListAdapter = new TCUserAvatarListAdapter(this, "",giftHandler);
        mUserAvatarList.setAdapter(mAvatarListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserAvatarList.setLayoutManager(linearLayoutManager);
        //美颜和美白
        mBeautyDialogFragment = new BeautyDialogFragment();
        //发消息文本框
        mInputTextMsgDialog = new TCInputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);
        //直播时长
        mBroadcastTime.setText(String.format(Locale.US,"%s","00:00:00"));
        //头像
        showHeadIcon(mHeadIcon, SPUtils.getString(SPUtils.USER_AVATAR));
        //直播背景图
        showLiveBgIcon(mLiveBg, SPUtils.getString(SPUtils.USER_AVATAR));
        //自定义点赞
        mHeartLayout = (TCHeartLayout) findViewById(R.id.heart_layout);

        //消息adapter
        mChatMsgListAdapter = new TCChatMsgListAdapter(this, mListViewMsg, mArrayListChatEntity,giftHandler);
        mListViewMsg.setAdapter(mChatMsgListAdapter);

        //弹幕
        IDanmakuView danmakuView = (IDanmakuView) findViewById(R.id.danmakuView);
        mDanmuMgr = new TCDanmuMgr(this);
        mDanmuMgr.setDanmakuView(danmakuView);

        //礼物
        gift_animation_view.setHandler(giftHandler);

        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        //下线广播
        mLocalBroadcatManager = LocalBroadcastManager.getInstance(this);
        mExitBroadcastReceiver = new ExitBroadcastRecevier();
        mLocalBroadcatManager.registerReceiver(mExitBroadcastReceiver, new IntentFilter(TCConstants.EXIT_APP));

        //集合装起分享的按钮方便点选操作
        shares = new ImageView[]{circle_friends, weixin_friend, qq, weibo};
        for (int i=0;i< shares.length;i++){
            final int finalI = i;
            shares[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j=0;j<shares.length;j++){
                        if (finalI ==j){
                            if (!shares[j].isSelected()) {
                                shares[j].setSelected(true);
                            }else{
                                shares[j].setSelected(false);
                            }
                        }else{
                            shares[j].setSelected(false);
                        }
                    }
                }
            });
        }
    }

    /**
     * 图片选择对话框
     */
    private void initPhotoDialog() {
        mPicChsDialog = new Dialog(this, R.style.floag_dialog);
        mPicChsDialog.setContentView(R.layout.dialog_pic_choose);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = mPicChsDialog.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.BOTTOM);
        lp.width = (int) (display.getWidth()); //设置宽度

        mPicChsDialog.getWindow().setAttributes(lp);

        TextView camera = (TextView) mPicChsDialog.findViewById(R.id.chos_camera);
        camera.setVisibility(View.GONE);
        TextView picLib = (TextView) mPicChsDialog.findViewById(R.id.pic_lib);
        TextView cancel = (TextView) mPicChsDialog.findViewById(R.id.btn_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(CAPTURE_IMAGE_CAMERA);
                mPicChsDialog.dismiss();
            }
        });

        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(IMAGE_STORE);
                mPicChsDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicChsDialog.dismiss();
            }
        });
    }

    /**
     * 获取图片资源
     *
     * @param type 类型（本地IMAGE_STORE/拍照CAPTURE_IMAGE_CAMERA）
     */
    private void getPicFrom(int type) {
        switch (type) {
            //拍照
            case CAPTURE_IMAGE_CAMERA:
                fileUri = createCoverUri("");
                getTakePhoto();
                //是否压缩
                takePhoto.onEnableCompress(null,false);
                //拍照
                takePhoto.onPickFromCaptureWithCrop(fileUri,getCropOptions());
                break;
            //本地
            case IMAGE_STORE:
                fileUri = createCoverUri("_select");
                getTakePhoto();
                //是否压缩
                takePhoto.onEnableCompress(null,false);
                //相册
                takePhoto.onPickFromGalleryWithCrop(fileUri,getCropOptions());

                break;

        }
    }

    /**加载主播头像*/
    private void showHeadIcon(ImageView view, String avatar) {
        TCUtils.showPicWithUrl(this, view, avatar, R.mipmap.head);
    }
    /**加载背景图*/
    private void showLiveBgIcon(ImageView view, String avatar) {
        TCUtils.showPicWithUrlCircleView(this, view, avatar, R.mipmap.head);
    }

    /**检测权限*/
    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[permissions.size()]),
                        TCConstants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type=PermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        PermissionManager.handlePermissionsResult(this,type,invokeParam,this);
        switch (requestCode) {
            case TCConstants.LOCATION_PERMISSION_REQ_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!LocationHelper.getMyLocation(this, this)) {
                        tv_local.setText(getString(R.string.text_live_lbs_fail));
                    }
                }
                break;
            case TCConstants.WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                mPermission = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void onUploadResult(int code, Object obj) {
        if (0 == code) {
            FileInfo fileInfo = (FileInfo) obj;
            String fileId = fileInfo.fileId;
//            SPUtils.putString(SPUtils.USER_AVATAR_ID,fileId);
            Toast.makeText(this, getString(R.string.publish_upload_success), Toast.LENGTH_SHORT).show();
//            setUser(fileId);
            mLiveBgFileId = fileId;
        } else {
            Toast.makeText(this, "上传封面失败", Toast.LENGTH_SHORT).show();
            Log.d("livelog","错误码+"+code);
            mLiveBg.setImageResource( R.mipmap.head);
        }
        if (dialog != null){
            dialog.dismiss();
        }
    }

    @Override
    public void takeSuccess(TResult result) {
        ArrayList<TImage> images = result.getImages();
        if (images.size()>0){
            TImage tImage = images.get(0);
            String path = tImage.getOriginalPath();
            if (TextUtils.isEmpty(path)){
                return;
            }
            mLiveBg.setImageBitmap(null);
            mLiveBg.setImageURI(Uri.parse(path));

            initProgressDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                        UIUtils.showToast("上传失败");
                    }
                }
            },10000);

            uploadHelper.uploadCover(path);
        }
    }

    private MyProgressDialog dialog;
    private void initProgressDialog() {
        dialog = new MyProgressDialog(this,"封面上传中");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type=PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type)){
            this.invokeParam=invokeParam;
        }
        return type;
    }

    /**自定义退出广播*/
    public class ExitBroadcastRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TCConstants.EXIT_APP)) {
                TXLog.d("livelog", "退出广播");
                //在被踢下线的情况下，执行退出前的处理操作：停止推流、关闭群组
                stopRecordAnimation();
                if(mTXCloudVideoView != null)
                    mTXCloudVideoView.onPause();
                stopPublish();
                quitRoom();
            }
        }
    }

    private boolean isIM = false;
    /**判断IM是否登录*/
    private void getIM(){
        String oldRoomId = SPUtils.getString(SPUtils.LIVE_ROOM_ID);
        if (!TextUtils.isEmpty(oldRoomId)){
            chatRoom.quitGroupOldRoomId(oldRoomId);
        }
        if (TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())){
//        if (TIMManager.getInstance().getLoginUser()==null || "".equals(TIMManager.getInstance().getLoginUser())){
            isIM = true;
            getImSig();
        }else {
            chatRoom.setMessageListener(this);
//            TIMManager.getInstance().init(this);
            chatRoom.createGroup();
        }
    }

    /**获取IM签名*/
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
                        UIUtils.showToast("网络异常，请重试或反馈给我们");
                    }
                });
    }

    /**IM登录*/
    private void goLoginIM(Map<String, String> dataMap){
        loginPresenter.imLogin(dataMap.get("uid"), dataMap.get("sig"));
        Log.d("livelog","登录IM");
    }

    /**IM登录成功去创建聊天室*/
    @Override
    public void onIMLoginSuccess() {
        if (chatRoom != null && isIM) {
            isIM = false;
            String oldRoomId = SPUtils.getString(SPUtils.LIVE_ROOM_ID);
            if (!TextUtils.isEmpty(oldRoomId)){
                chatRoom.quitGroupOldRoomId(oldRoomId);
            }

            chatRoom.setMessageListener(this);
//            TIMManager.getInstance().init(this);
            chatRoom.createGroup();
        }
        Log.d("livelog","IM登录成功");
    }

    /**IM登录失败继续登录*/
    @Override
    public void onIMLoginError(int code, String msg) {
        Log.d("livelog","IM登录失败，code="+code);
        getImSig();
    }

    /**
     * 创建聊天室的回调
     * @param code 错误码，成功时返回0，失败时返回相应错误码
     * @param msg  返回信息，成功时返回群组Id，失败时返回相应错误信息
     */
    @Override
    public void onJoinGroupCallback(int code, String msg) {
        if (code == 0){
            group_id = msg;
            mTXLivePusher.startPusher(mPushUrl);
            doAfterIM(msg);
        }else if (code == 6013 || code == 6014){
            getImSig();
        }else if (code == 6012) {
            Log.d("livelog",code+"请求超时，网络恢复后重试");
            UIUtils.showToast("网络连接超时，创建直播失败");
            finish();
        }else {
            Log.d("livelog",code+"");
            UIUtils.showToast("网络连接超时，创建直播失败");
            finish();
        }
    }

    /**
     * 解散聊天室的回调
     * @param code 错误码，成功时返回0，失败时返回相应错误码
     * @param msg  返回信息，成功时返回群组Id，失败时返回相应错误信息
     */
    @Override
    public void onQuitGroupCallback(int code, String msg) {
        if(0 == code) {
            TXLog.d("livelog", "删除聊天室成功");
        } else {
            TXLog.d("livelog", "删除聊天室失败");
        }
    }

    /**
     * 在IM登录后进行操作
     * @param group_id
     */
    private void doAfterIM(String group_id){
        //获取主播的99币
        getCoin();
        //99号
        tv_identity.setText(SPUtils.getString(SPUtils.USER_IDENTITY));
        //获取IM聊天室内信息
        getGroupMembersList();
        //获取系统消息
        getImMessage(group_id);
    }

    /**拉取用户头像列表*/
    public void getGroupMembersList() {

        chatRoom.getGroupMembers(new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Log.d("livelog", "拉取用户头像失败");
                getGroupMembersList();
            }

            @Override
            public void onSuccess(List<TIMUserProfile> result) {
                Log.d("livelog", "拉取用户头像成功");
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
        chatRoom.getGroupMembers(new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(List<TIMUserProfile> result) {
                mCurrentMemberCount = result.size();
//                mCurrentMemberCount += sendRobots.size();
                mCurrentMemberCount += sendRobotsNum;
                mMemberCount.setText(String.format("%d", mCurrentMemberCount)+ "人");
            }
        });
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
     * 打开摄像头初始化
     */
    private void startPublish() {
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
            mTXPushConfig.setTouchFocus(false);
            //切后台推流图片
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
        //打开摄像头渲染画面
        mTXLivePusher.startCameraPreview(mTXCloudVideoView);
    }

    //点击直播按钮后进行设置
    private void startBeauty(){
//        if (mTXLivePusher != null) {
//            //带宽自适应
//            mTXPushConfig.setAutoAdjustBitrate(false);
//            //视频分辨率
//            mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
//            //视频比特率
//            mTXPushConfig.setVideoBitrate(1000);
//            mTXPushConfig.setHardwareAcceleration(true);
//            mTXPushConfig.setTouchFocus(false);
//        }
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
                                for (int i = 0; i < data.size(); i++) {
                                    sysMessage = data.get(i).get("");
                                    getSysTemMessage(sysMessage);
                                }
                            }else{

                            }
                        }else {
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("livelog","IM获取系统消息失败");
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

    @Override
    public void onLocationChanged(final int code, double lat1, double long1, final String location) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (0 == code) {
                    tv_local.setText(location);
                    city = location;
                } else {
                    tv_local.setText(getString(R.string.text_live_lbs_fail));
                }
            }
        });
    }

    /**美颜和美白效果调整*/
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

    //是否进入后台
    private boolean mPasuing = false;
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
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onPause();
        }
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
        EventBus.getDefault().unregister(this);
        if (gift_animation_view != null){
            gift_animation_view.destroyed();
        }
        if (comein_animation != null){
            comein_animation.destroy();
        }
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
        if (chatRoom != null)
            chatRoom.removeMsgListener();
        quitRoom();
    }

    @OnClick({R.id.iv_gps, R.id.iv_exit, R.id.layout_tag, R.id.tv_begin_live, R.id.btn_message_input,
            R.id.tv_share, R.id.flash_btn, R.id.switch_cam, R.id.beauty_btn,
            R.id.btn_close, R.id.live_more, R.id.jiujiub, R.id.checktext, R.id.live_bg})
    void onClick(View v){
        switch (v.getId()){
            case R.id.iv_gps:
                tv_local.setText(R.string.text_live_location);
                if (LocationHelper.checkLocationPermission(this)) {
                    if (!LocationHelper.getMyLocation(this, this)) {
                        tv_local.setText(getString(R.string.text_live_lbs_fail));
                        Toast.makeText(getApplicationContext(), "定位失败，请查看是否打开定位服务", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.iv_exit:
                back();
                break;
            case R.id.layout_tag:
                startActivity(new Intent(this, LiveTagActivity.class));
                overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
                break;
            case R.id.tv_begin_live:
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                String isDefaultAvatar = SPUtils.getString(SPUtils.USER_IS_DEFAULT_AVATAR);
                if (TextUtils.isEmpty(tagId)){
//                    UIUtils.showToast("请选择标签后再进行直播");
                    startActivity(new Intent(this, LiveTagActivity.class));
                    overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
                    break;
                }
                if (!"N".equals(isDefaultAvatar)){
                    UIUtils.showToast("请到个人中心设置头像再进行直播");
                    break;
                }else{

                }
                if (!checkBox.isChecked()) {
                    UIUtils.showToast("请遵守《99直播管理条例》");
                    break;
                }
                if (mPermission){
//                    iv_gps.setVisibility(View.GONE);
//                    tv_local.setVisibility(View.GONE);
//                    iv_exit.setVisibility(View.GONE);
//                    et_live_title.setVisibility(View.GONE);
//                    tv_begin_live.setVisibility(View.GONE);
//                    layout_share.setVisibility(View.GONE);
//                    live_agreement.setVisibility(View.GONE);
                    open_live.setVisibility(View.GONE);
                    //显示布局
                    mControllLayer.setVisibility(View.VISIBLE);
//                    action_live.setVisibility(View.VISIBLE);
//                    action_live.setText("准备中...");
                    ready.setImageResource(R.drawable.ready);
                    ready.setVisibility(View.VISIBLE);
                    startBeauty();
//                  String title = et_live_title.getText().toString().trim();
                    getAVPath();
                }else {
                    UIUtils.showToast("权限未允许");
                }
                break;
            case R.id.btn_message_input:
                inputMsgDialog();
                break;
            case R.id.tv_share:
                UMengInfo.getUrlTool(this,SPUtils.getString(SPUtils.USER_NAME),umShareListener,SPUtils.getString(SPUtils.USER_ID),false,
                        et_live_title.getText().toString().trim(),SPUtils.getString(SPUtils.USER_AVATAR),null);


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
                    Toast.makeText(getApplicationContext(), "闪光灯打开失败，请切换后置摄像头", Toast.LENGTH_SHORT).show();
                    return;
                }
                mFlashOn = !mFlashOn;
//                mFlashView.setBackgroundDrawable(mFlashOn ?
//                        getResources().getDrawable(R.mipmap.icon_flash_pressed):
//                        getResources().getDrawable(R.drawable.icon_flash));
                break;
            case R.id.switch_cam:
                mTXLivePusher.switchCamera();
                function_bg.setVisibility(View.GONE);
                break;
            case R.id.beauty_btn:
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
                break;
            case R.id.jiujiub:
                FansContribution.goFansContributionByUser(LivePublisherNewActivity.this,SPUtils.getString(SPUtils.USER_CODE));
                break;

            case R.id.checktext:
                checkBox.setChecked(!checkBox.isChecked());
                break;

            case R.id.live_bg:
                mPicChsDialog.show();
                break;

            default:
                tool_bar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDetailDialog != null){
            if (mDetailDialog.isShown()){
                return;
            }
        }

        if (mControllLayer.getVisibility()== View.VISIBLE){
            showComfirmDialog(TCConstants.TIPS_MSG_STOP_PUSH, false);
        }else{
            finish();
        }
    }

    void back(){
        finish();
    }

    private String tagId;

    /**创建房间*/
    private void goCreatRoom(final String title, final String group_id, final String live_code, final String av_path , final boolean isGetRobot) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("title",title);
        params.put("group_id", group_id);
        params.put("live_code", live_code);
        params.put("av_path", av_path);
        params.put("city", city);
        params.put("tag_id", tagId);
        if (!TextUtils.isEmpty(mLiveBgFileId)){
            params.put("background_image",mLiveBgFileId);
        }

        newRoomProtocol.creatRoom(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> dataMap = listMapByJson.get(0);
                            if ("0".equals(dataMap.get("code"))){
                                String data = dataMap.get("data");
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                if (dataList.size()>0){
                                    Map<String, String> map = dataList.get(0);
                                    roomId = map.get("room_id");
                                    getPing( title, group_id,  live_code, av_path);
//                                    if (isGetRobot) {
//                                        for (int i = 0; i < shares.length; i++) {
//                                            if (shares[i].isSelected()) {
//                                                shares[i].setSelected(false);
//                                                goShare(i, roomId);
//                                                break;
//                                            }
//                                        }
//                                    }
//                                    if (isGetRobot) {
//                                        getRobotList(roomId, false);
//                                    }
                                }else{
                                    UIUtils.showToast(R.string.net_error);
                                    finish();
                                }

                            }else{
                                UIUtils.showToast(dataMap.get("msg"));
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
                        UIUtils.showToast(R.string.net_error);
                        finish();
                    }
                });
    }

    private int pingSize=0;
    private void getPing(final String title, final String group_id, final String live_code, final String av_path){
        if (pingSize >= 3){
            return;
        }
        LiveRequestParams params = new LiveRequestParams();
        params.put("live_code",live_code);
        newRoomProtocol.ping(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                if (dataList.size()>0){
                                    Map<String, String> map1 = dataList.get(0);
                                    if (map1.containsKey("result")){
                                        String result = map1.get("result");
                                        Log.d("livelog",result);
                                        if (!"OK".equals(result)){
                                            goCreatRoom(title,group_id,live_code,av_path,false);
                                        }else{
//                                            if (isGetRobot) {
                                            getRobotList(roomId, false);
//                                            }
                                            for (int i = 0; i < shares.length; i++) {
                                                if (shares[i].isSelected()) {
                                                    shares[i].setSelected(false);
                                                    goShare(i, roomId);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }else{
                                pingSize++;
                                getPing( title, group_id,  live_code, av_path);
                            }
                        }else{
                            pingSize++;
                            getPing( title, group_id,  live_code, av_path);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        pingSize++;
                        getPing( title, group_id,  live_code, av_path);
                    }
                });
    }

    private int maxGetRobot=0;
    /**
     * 获取机器人列表
     * @param roomId
     */
    private void getRobotList(final String roomId, final boolean isGetMore){
        RobotProtocol robotProtocol = new RobotProtocol();
        LiveRequestParams params = new LiveRequestParams();
        params.put("room_id",roomId);
        if (isGetMore){
            params.put("robot_list",mRobotIdString);
        }
        robotProtocol.getRobotList(params).observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        /**
                         * scale    一个真实用户带几个机器人
                         threshold   阈值
                         base_count   基数
                         pre_real   预加载多少个用户的机器人
                         base_count + (scale * pre_real)  =  总数
                         rand_range  总机器人
                         */
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                if (dataList.size()>0){
                                    Map<String, String> dataMap = dataList.get(0);
                                    if (dataMap.containsKey("robotList")){
                                        //获取robot——list  id
                                        String robot_id_string = dataMap.get("robot_id_string");
                                        //阈值
                                        String threshold = dataMap.get("threshold");
                                        //获取基数
                                        String base_count = dataMap.get("base_count");
                                        //获取增数
                                        String scale = dataMap.get("scale");
                                        //预加载多少个用户的机器人
                                        String pre_real = dataMap.get("pre_real");
                                        String robotList = dataMap.get("robotList");
                                        //加载机器人的总数
                                        String rand_range = dataMap.get("rand_range");
                                        ArrayList<Map<String, String>> robots = JsonUtil.getListMapByJson(robotList);
                                        if (robots.size()>0){
                                            if (isGetMore){
                                                doAddRobotScale(robots,robot_id_string,rand_range);
                                            }else {
                                                doSendRobot(robots, base_count , scale , pre_real, robot_id_string, threshold , rand_range);
                                            }
                                        }
                                    }
                                }
                            }else{
                                if (!isGetMore && maxGetRobot < 3){
                                    maxGetRobot++;
                                    getRobotList(roomId,false);
                                }
                            }
                        }else{
                            if (!isGetMore && maxGetRobot < 3){
                                maxGetRobot++;
                                getRobotList(roomId,false);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (!isGetMore && maxGetRobot < 3){
                            maxGetRobot++;
                            getRobotList(roomId,false);
                        }
                    }
                });
    }

    /**
     * 发送机器人
     * @param mRobots
     * base_count + (scale * pre_real)  =  总数
     */
    /**已发送给所有人的机器人的集合,此集合用于当有真用户进入的时候发送给大家*/
    private List<Map<String,String>> sendRobots ;
    private int sendRobotsNum = 0;
    private int robotScale = 3;//每进一个真人时添加的机器人的数量
    private List<Robot> scaleRobot;
    private String mRobotIdString = "";//所有机器人的id和总和
    private int mThreshold = 1;
    private boolean isGetMore;//是否获取更多的机器人（当机器人用完了之后，再向服务器获取）
    private int robotMax = 0;//机器人的最大值
    private void doSendRobot(final ArrayList<Map<String, String>> mRobots,String base,String scale
                                ,String pre_real ,String robot_id_string,String threshold,String rand_range){
        int size = mRobots.size();
        int baseR = Integer.parseInt(base);
        int scaleR = Integer.parseInt(scale);
//        int pre = Integer.parseInt(pre_real);
        robotMax = Integer.parseInt(rand_range);
        mRobotIdString = robot_id_string;
        mThreshold = Integer.parseInt(threshold) * scaleR;
        robotScale = scaleR;
        //对数据进行整理
        if (size > baseR){
            scaleRobot = new ArrayList<>();
            for (int i=baseR;i<size;i++){
                Map<String, String> mapp = mRobots.get(i);
                Robot robot = new Robot();
                robot.setFull_head_image(mapp.get(TCConstants.ROB0T_FULLHEADIMG));
                robot.setNickname(mapp.get(TCConstants.ROBOT_NAME));
                robot.setUser_id(mapp.get(TCConstants.ROBOT_USER_ID));
                robot.setIdentity(mapp.get(TCConstants.ROBOT_IDENTITY));
                robot.setAction(mapp.get(TCConstants.ROBOT_ACTION));
                robot.setLanguage(mapp.get(TCConstants.ROBOT_CHAT));
                robot.setLevel("1");
                scaleRobot.add(robot);
            }
        }else if (size < baseR){
            baseR = size;
        }else{

        }
        final int finalMSize = baseR;
        final int finalMSizeban = baseR/2;
        final int laoding = baseR<=10 ? 100 : 2000;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i< finalMSize; i++){
                    try {
                        if (i == finalMSizeban){
                            Thread.sleep(laoding);
                        }else {
                            Thread.sleep(100);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Map<String, String> map = mRobots.get(i);
                    final Robot robot = new Robot();
                    robot.setFull_head_image(map.get(TCConstants.ROB0T_FULLHEADIMG));
                    robot.setNickname(map.get(TCConstants.ROBOT_NAME));
                    robot.setIdentity(map.get(TCConstants.ROBOT_IDENTITY));
                    robot.setUser_id(map.get(TCConstants.ROBOT_USER_ID));
                    robot.setAction(map.get(TCConstants.ROBOT_ACTION));
                    robot.setLanguage(map.get(TCConstants.ROBOT_CHAT));
                    robot.setLevel("1");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
                            chatRoom.sendRobotMessage(TCConstants.IMCMD_ROBOT,robot);
//                        }
//                    });
                }
            }
        }).start();
    }

    private void addRobotWithPeopleCome(){

        if (scaleRobot != null && scaleRobot.size() > 0){
            if (scaleRobot.size() <= mThreshold && !isGetMore){
                isGetMore = true;
                getRobotList(roomId,true);
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i< robotScale; i++){
                        if (scaleRobot.size()<=0){
                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (scaleRobot.size()>0) {
                            final Robot robot = scaleRobot.get(0);
                            scaleRobot.remove(0);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
                                    chatRoom.sendRobotMessage(TCConstants.IMCMD_ROBOT, robot);
//                                }
//                            });
                        }
                    }
                }
            }).start();
        }else{
            if (!isGetMore) {
                isGetMore = true;
                getRobotList(roomId, true);
            }
        }
    }

    private void doAddRobotScale(ArrayList<Map<String, String>> robots, String robot_id_string,String rand_range){
        if (scaleRobot == null)
                scaleRobot = new ArrayList<>();
        mRobotIdString = mRobotIdString +","+robot_id_string;
        for (int i=0;i<robots.size();i++){
            Map<String, String> mapp = robots.get(i);
            Robot robot = new Robot();
            robot.setFull_head_image(mapp.get(TCConstants.ROB0T_FULLHEADIMG));
            robot.setNickname(mapp.get(TCConstants.ROBOT_NAME));
            robot.setUser_id(mapp.get(TCConstants.ROBOT_USER_ID));
            robot.setIdentity(mapp.get(TCConstants.ROBOT_IDENTITY));
            robot.setAction(mapp.get(TCConstants.ROBOT_ACTION));
            robot.setLanguage(mapp.get(TCConstants.ROBOT_CHAT));
            robot.setLevel("1");
            scaleRobot.add(robot);
        }
        isGetMore = false;
        if (robotMax == 0){
            robotMax = Integer.parseInt(rand_range);
            addRobotWithPeopleCome();
        }
//        addRobotWithPeopleCome();
    }

    private void startLive(){
        //开始计时
//        action_live.setVisibility(View.VISIBLE);
//        action_live.setText("3");
        ready.setImageResource(R.drawable.ready_three);
        ready.setVisibility(View.VISIBLE);
        startAction();
    }
    private int mReady = 0;
    private void startAction(){
        mReady ++;
        PropertyValuesHolder anim4 = PropertyValuesHolder.ofFloat("scaleX",
                0.8f,1.5f);
        PropertyValuesHolder anim5 = PropertyValuesHolder.ofFloat("scaleY",
                0.8f,1.5f);
        PropertyValuesHolder anim6 = PropertyValuesHolder.ofFloat("alpha",
                1.0f, 0.5f);
        final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(ready, anim4, anim5, anim6).setDuration(1000);
//        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                switch (mReady){
                    case 1:
                        ready.setImageResource(R.drawable.ready_two);
                        startAction();
                        break;
                    case 2:
                        ready.setImageResource(R.drawable.ready_one);
                        startAction();
                        break;
                    case 3:
                        //创建直播
                        goCreatRoom(et_live_title.getText().toString().trim(), group_id, live_code, mPushUrl,true);
                        ready.setVisibility(View.GONE);
                        startRecordAnimation();
                        break;
                }
//                int ac = Integer.parseInt(action_live.getText().toString());
//                if (ac > 1){
//                    ac--;
//                    action_live.setText(ac+"");
//                    startAction();
//                }else{
//                    //创建直播
//                    goCreatRoom(et_live_title.getText().toString().trim(), group_id, live_code, mPushUrl);
//                    action_live.setVisibility(View.GONE);
//                    startRecordAnimation();
//                }
            }
        });
        animator.start();
    }

    private void goShare(int i,String room_id){
        switch (i){
            case 0:
                UMengInfo.getUserShare(SPUtils.getString(SPUtils.USER_ID), room_id, true ,this ,
                        umShareListener,SHARE_MEDIA.WEIXIN_CIRCLE,SPUtils.getString(SPUtils.USER_NAME),
                        et_live_title.getText().toString().trim(),SPUtils.getString(SPUtils.USER_AVATAR));

                break;
            case 1:
                UMengInfo.getUserShare(SPUtils.getString(SPUtils.USER_ID), room_id, true ,this ,
                        umShareListener,SHARE_MEDIA.WEIXIN,SPUtils.getString(SPUtils.USER_NAME),
                        et_live_title.getText().toString().trim(),SPUtils.getString(SPUtils.USER_AVATAR));

                break;
            case 2:
                UMengInfo.getUserShare(SPUtils.getString(SPUtils.USER_ID), room_id, true ,this ,
                        umShareListener,SHARE_MEDIA.QQ,SPUtils.getString(SPUtils.USER_NAME),
                        et_live_title.getText().toString().trim(),SPUtils.getString(SPUtils.USER_AVATAR));

                break;
            case 3:
                UMengInfo.getUserShare(SPUtils.getString(SPUtils.USER_ID), room_id, true ,this ,
                        umShareListener,SHARE_MEDIA.QZONE,SPUtils.getString(SPUtils.USER_NAME),
                        et_live_title.getText().toString().trim(),SPUtils.getString(SPUtils.USER_AVATAR));

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

//        ChatEntity entity = new ChatEntity();
//        entity.setSendName(SPUtils.getString(SPUtils.USER_NAME) + ":");
//        entity.setContext(msg);
//        entity.setIdentity(SPUtils.getString(SPUtils.USER_IDENTITY));
//        entity.setType(TCConstants.TEXT_TYPE);
//        notifyMsg(entity);

        //弹幕
        if (danmuOpen) {
//            if (mDanmuMgr != null) {
//                mDanmuMgr.addDanmu(SPUtils.getString(SPUtils.USER_AVATAR), SPUtils.getString(SPUtils.USER_NAME), msg);
//            }
//            chatRoom.sendMessage(TCConstants.IMCMD_DANMU, msg);
            chatRoom.sendMessage(TCConstants.IMCMD_DANMU,msg);
        } else {
            chatRoom.sendMessage(TCConstants.IMCMD_PAILN_TEXT, msg);
        }
    }

    /**停止推流*/
    private void stopPublish() {
        if (mTXLivePusher != null) {
            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.setPushListener(null);
            mTXLivePusher.stopPusher();
        }
    }

    /**
     * 退出房间
     * 包含后台退出与IMSDK房间退出操作
     */
    public void quitRoom() {
        //主播端退出前删除直播群组
        if (chatRoom != null && !TextUtils.isEmpty(group_id))
            chatRoom.deleteGroup();
    }

    /**关闭直播接口*/
    private void closeLive(String code) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("code", code);

        roomProtocol.getCloseLive(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        //停止推流
                        stopPublish();
                        //退出房间
                        quitRoom();
                        //停止计时动画
                        stopRecordAnimation();
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){

                                ArrayList<Map<String, String>> datalist = JsonUtil.getListMapByJson(map.get("data"));
                                if (datalist.size() > 0){
                                    Map<String, String> dataMap = datalist.get(0);
                                    String awardCoin = dataMap.get("reward_gold");
                                    String totalCoin = dataMap.get("get_gold");
                                    String user_auth = dataMap.get("user_auth");
                                    showDetailDialog(awardCoin,totalCoin,user_auth);
                                }
                            }else {
                                showDetailDialog("0","0","1");
                            }
                        }else {
                            showDetailDialog("0","0","1");
                        }

                        Log.d("livelog", "关闭直播成功");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d("livelog", "关闭直播失败");
                        //停止推流
                        stopPublish();
                        //退出房间
                        quitRoom();
                        //停止计时动画
                        stopRecordAnimation();
                        showDetailDialog("0","0","1");
                    }
                });
    }

    /**
     * 退出后提示框
     */
    public void showDetailDialog(String awardCoin,String totalCoin,String user_auth) {
        mDetailDialog = (LinearLayout) findViewById(R.id.finish_dialog);
//        mDetailDialog = new Dialog(this, R.style.dialog);
//        mDetailDialog.setContentView(R.layout.dialog_new_publish_detail);
        RelativeLayout rl_back = (RelativeLayout) mDetailDialog.findViewById(R.id.layout_back);
        RelativeLayout award_relative = (RelativeLayout) mDetailDialog.findViewById(R.id.award_relative);
        RelativeLayout go_approve = (RelativeLayout) mDetailDialog.findViewById(R.id.go_approve);
        if ("0".equals(user_auth) || "3".equals(user_auth)){
            if ("0".equals(awardCoin)){
                award_relative.setVisibility(View.GONE);
                go_approve.setVisibility(View.VISIBLE);
                go_approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Approve.goApprove(LivePublisherNewActivity.this);
                        finish();
                    }
                });
            }else{
                award_relative.setVisibility(View.VISIBLE);
                go_approve.setVisibility(View.GONE);
            }
        }else{

            award_relative.setVisibility(View.VISIBLE);
            go_approve.setVisibility(View.GONE);

        }

        rl_back.setVisibility(View.GONE);
        TextView title = (TextView) mDetailDialog.findViewById(R.id.tv_title);
        title.setText(R.string.live_finish);
        TextView mDetailTime = (TextView) mDetailDialog.findViewById(R.id.tv_time);
//        mDetailAdmires = (TextView) mDetailDialog.findViewById(R.id.tv_admires);//点赞
        TextView mDetailWatchCount = (TextView) mDetailDialog.findViewById(R.id.tv_looker);
        TextView mDetailCoin = (TextView) mDetailDialog.findViewById(R.id.tv_coin);//收获99币
        TextView mDetailAward = (TextView) mDetailDialog.findViewById(R.id.tv_award);//奖励99币
        TextView share_at_finish = (TextView) mDetailDialog.findViewById(R.id.share_at_finish);
        share_at_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UMengInfo.getUrlTool(LivePublisherNewActivity.this,SPUtils.getString(SPUtils.USER_NAME),umShareListener,
                        SPUtils.getString(SPUtils.USER_ID),false,
                        et_live_title.getText().toString().trim(),SPUtils.getString(SPUtils.USER_AVATAR),null);
            }
        });

//        mDetailDialog.setCancelable(false);

        //返回首页
        TextView tvCancel = (TextView) mDetailDialog.findViewById(R.id.tv_back_home);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                mDetailDialog.dismiss();
                EventBus.getDefault().post(new ShowHomeEvent());
            }
        });
        //确认则显示观看detail
        stopRecordAnimation();
        //直播时间
        mDetailTime.setText(TCUtils.formattedTime(mSecond));
//        mDetailAdmires.setText(String.format(Locale.CHINA, "%d", lHeartCount));
        //观看人数
        mDetailWatchCount.setText(String.format(Locale.CHINA,"%d",lTotalMemberCount));
        //获得99币
        mDetailCoin.setText(totalCoin);
        //奖励99币
        mDetailAward.setText(awardCoin);
        mDetailDialog.setVisibility(View.VISIBLE);
    }

    /**退出提示框*/
    public void showComfirmDialog(String msg, Boolean isError) {

        MyDialog dialog = new MyDialog(this).setMessage(msg);
        dialog.setRightButtonState("确定",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mTXCloudVideoView != null)
                    mTXCloudVideoView.onPause();
//                //停止推流
//                stopPublish();
//                //退出房间
//                quitRoom();
//                //停止计时动画
//                stopRecordAnimation();
                //关闭直播间
                closeLive(live_code);
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

    /**错误提示框*/
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


    @Override
    public void onPushEvent(int event, Bundle bundle) {
        if (event < 0) {
            if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {//网络断开，弹对话框强提醒，推流过程中直播中断需要显示直播信息后退出
                showComfirmDialog (TCConstants.ERROR_MSG_NET_DISCONNECTED, true);
            } else if (event == TXLiveConstants.PUSH_WARNING_NET_BUSY){//网络质量很差，观众端出现卡顿
                showComfirmDialog (TCConstants.ERROR_MSG_NET_BUSY, true);
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
//            startShow(room_id, live_code);
            //开始倒计时，计时结束后创建直播房间进行直播
            if (!isPushSuccess){
                isPushSuccess = true;
                startLive();
            }
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    @Override
    public void onSendMsgCallback(int errCode, int msgType, TIMMessage timMessage, String parm) {
        //消息发送成功后回显
        if(errCode == 0) {
            TIMElemType elemType = timMessage.getElement(0).getType();
            if(elemType == TIMElemType.Text) {
                //文本消息显示
                Log.d("livelog", "文本消息发送成功:" + errCode);
                switch (msgType) {
                    case TCConstants.IMCMD_PAILN_TEXT:
                        ChatEntity entity1 = new ChatEntity();
                        entity1.setSendName(SPUtils.getString(SPUtils.USER_NAME) + ":");
                        entity1.setContext(parm);
                        entity1.setIdentity(SPUtils.getString(SPUtils.USER_IDENTITY));
                        entity1.setType(TCConstants.TEXT_TYPE);
                        entity1.setLevel(SPUtils.getString(SPUtils.USER_LEVEL));
                        notifyMsg(entity1);
                        Log.d("livelog", parm);
                        break;
                }

            } else if(elemType == TIMElemType.Custom) {
                //custom消息存在消息回调,此处显示成功失败
                Log.d("livelog", "自定义消息发送失败:" + errCode);
                switch (msgType){
//                    case TCConstants.IMCMD_PAILN_TEXT:
//                        ChatEntity entity1 = new ChatEntity();
//                        entity1.setSendName(SPUtils.getString(SPUtils.USER_NAME) + ":");
//                        entity1.setContext(parm);
//                        entity1.setIdentity(SPUtils.getString(SPUtils.USER_IDENTITY));
//                        entity1.setType(TCConstants.TEXT_TYPE);
//                        notifyMsg(entity1);
//                        Log.d("livelog", parm);
//                        break;

                    case TCConstants.IMCMD_DANMU:
                        ChatEntity entity2 = new ChatEntity();
                        entity2.setSendName(SPUtils.getString(SPUtils.USER_NAME) + ":");
                        entity2.setContext(parm);
                        entity2.setIdentity(SPUtils.getString(SPUtils.USER_IDENTITY));
                        entity2.setType(TCConstants.TEXT_TYPE);
                        entity2.setLevel(SPUtils.getString(SPUtils.USER_LEVEL));
                        notifyMsg(entity2);
                        if (mDanmuMgr != null) {
                            mDanmuMgr.addDanmu(SPUtils.getString(SPUtils.USER_AVATAR), SPUtils.getString(SPUtils.USER_NAME), parm);
                        }
                        break;
                }
            }



        } else {
            Log.d("livelog", "消息发送失败:" + errCode + " msg:" + errCode);

            switch (msgType) {
                case TCConstants.IMCMD_PAILN_TEXT:
                    if (errCode==80001){
                        UIUtils.showToast("输入内容含有敏感词");
                    }
                    break;

                case TCConstants.IMCMD_DANMU:
                    if (errCode==80001){
                        UIUtils.showToast("输入内容含有敏感词");
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
        entity.setLevel(level);
        entity.setType(type);
        notifyMsg(entity);
    }

    private int allRobotSendCount = 0;
    @Override
    public void onMemberJoin(String userId, String nickname, String faceurl,String level) {
        Log.d("sendRobotNum",sendRobots.size() + "and " + sendRobotsNum);
        if (sendRobots.size()>0) {
            int size = sendRobots.size()>= 10 ? 10 : sendRobots.size();
            List<Map<String ,String>> robots = new ArrayList<>();
            for (int i=0;i< size ;i++){
                robots.add(sendRobots.get(i));
            }
            JSONArray robotsJson = Tools.list2JsonArray(robots);
//            chatRoom.sendRobotAll(TCConstants.IMCMD_ROBOT_ALL,robotsJson,sendRobots.size());
            chatRoom.sendRobotAll(TCConstants.IMCMD_ROBOT_ALL,robotsJson,sendRobotsNum);
        }

        if (!mAvatarListAdapter.addItem(new TCSimpleUserInfo(userId, nickname, faceurl))){
            return;
        }

        //IM人数
        getGroupNumber();
        userAvatarListScrollto();
        lTotalMemberCount++;
//        int itemCount = mAvatarListAdapter.getItemCount();
//        mCurrentMemberCount = itemCount;
//        mMemberCount.setText(String.format("%d", mCurrentMemberCount)+ "人");

        ChatEntity entity = new ChatEntity();
        entity.setSendName("直播消息");
        if(nickname.equals(""))
            entity.setContext(userId + "加入直播");
        else
            entity.setContext(nickname + "加入直播");
        entity.setType(TCConstants.MEMBER_ENTER);
        entity.setIdentity(userId);
        entity.setLevel(level);
        notifyMsg(entity);

        comein_animation.showLevel(userId,level,faceurl,nickname);

        //进来一个人开始加机器人
//        if (sendRobots.size() < robotMax){
//            addRobotWithPeopleCome();
//        }
        if (sendRobotsNum < robotMax){
            addRobotWithPeopleCome();
        }else if(sendRobotsNum==0 && robotMax == 0){
            addRobotWithPeopleCome();
        }
    }

    @Override
    public void onMemberQuit(String userId, String nickname) {

        mAvatarListAdapter.removeItem(userId);
        //IM人数
        getGroupNumber();
//        int itemCount = mAvatarListAdapter.getItemCount();
//        mCurrentMemberCount = itemCount;
//        mMemberCount.setText(String.format("%d", mCurrentMemberCount)+ "人");

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
        entity.setSendName(nickname+":");
        entity.setContext(text);
        entity.setIdentity(identity);
        entity.setType(TCConstants.TEXT_TYPE);
        entity.setLevel(level);
        notifyMsg(entity);

        if (mDanmuMgr != null) {
            mDanmuMgr.addDanmu(faceurl,nickname,text);
        }
    }

    @Override
    public void onGroupDelete(String group_id) {

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
        entity.setIdentity(userId);
        entity.setLevel(level);

        if (mHeartLayout != null) {
            mHeartLayout.addFavor();
        }
        lHeartCount++;

        //todo：修改显示类型
        entity.setType(TCConstants.PRAISE);
        notifyMsg(entity);
    }

    private String getEmojiStringByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    @Override
    public void onGiftSend(String giftMsg, String id, String name, String url,String level) {
        ArrayList<Map<String, String>> giftList = JsonUtil.getListMapByJson(giftMsg);
        if (giftList.size()>0){
            Map<String, String> map = giftList.get(0);
            if (map.containsKey("B")){
                String b = map.get("B");
                if (!TextUtils.isEmpty(b)){
                    tv_coin_num.setText(b + "  >");
                }
            }
            map.put("user_id",id);
            map.put("user_name",name);
            map.put("user_avatar",url);
            if ("1".equals(map.get("continue"))){
                gift_animation_view.sendGift(map);
            }else{
                gift_animation_view.sendBigGift(map);
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
    public void onAddRobot(Robot robot) {

    }

    @Override
    public void onSendRobotCallBack(int code, int cmd, Object o, Robot robot) {
        if (code == 0){
            switch (cmd){
                case TCConstants.IMCMD_ROBOT:
                    if (robot != null){
                        Map<String,String> robotMap = new HashMap<>();
                        robotMap.put(TCConstants.ROBOT_NAME,robot.getNickname());
                        robotMap.put(TCConstants.ROB0T_FULLHEADIMG,robot.getFull_head_image());
                        robotMap.put(TCConstants.ROBOT_USER_ID,robot.getUser_id());
                        robotMap.put(TCConstants.ROBOT_IDENTITY,robot.getIdentity());
                        robotMap.put(TCConstants.ROBOT_LEVEL,robot.getLevel());
                        sendRobots.add(robotMap);
                        sendRobotsNum++;
                        if (sendRobots.size()>50){
                            sendRobots.remove(sendRobots.size()-1);
                        }
                        String name = robot.getNickname();
                        String img_url = robot.getFull_head_image();
                        String user_id = robot.getUser_id();
                        String identity = robot.getIdentity();
                        String action = robot.getAction();
                        onRobotJoin(robot,user_id,name,img_url,action,identity);
                    }
                    break;
                case TCConstants.IMCMD_ROBOT_PRAISE:
                    onPraise(robot.getIdentity(),robot.getNickname(),robot.getLevel());
                    break;
                case TCConstants.IMCMD_ROBOT_CHAT:
                    onReceiveTextMsg(robot.getLanguage(), robot.getNickname(),robot.getIdentity(),robot.getLevel(), TCConstants.TEXT_TYPE);
                    break;
            }
        }else{
            switch (cmd){
                case TCConstants.IMCMD_ROBOT_ALL:
                    if (allRobotSendCount > 1){
                        break;
                    }
                    allRobotSendCount++;
                    if (sendRobots.size()>0) {
                        int size = sendRobots.size()>= 10 ? 10 : sendRobots.size();
                        List<Map<String ,String>> robots = new ArrayList<>();
                        for (int i=0;i< size ;i++){
                            robots.add(sendRobots.get(i));
                        }
                        JSONArray robotsJson = Tools.list2JsonArray(robots);
                        chatRoom.sendRobotAll(TCConstants.IMCMD_ROBOT_ALL,robotsJson,sendRobots.size());
                    }
                    break;
            }
        }
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
        //左下角显示用户加入消息
        ChatEntity entity = new ChatEntity();
        entity.setSendName("直播消息");
        if (nickname.equals(""))
            entity.setContext(identifier + "关注了您");
        else
            entity.setContext(nickname + "关注了您");
        entity.setIdentity(identifier);
        entity.setType(TCConstants.MEMBER_ENTER);
        entity.setLevel(level);
        notifyMsg(entity);
    }

    @Override
    public void onShutUp(String msg) {

    }

    /**
     * 礼物飘屏
     * @param str
     */
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
        entity.setContext(nickname + "分享了主播，"+share);
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
    public void onRobotJoin(final Robot robot, String userId, String nickname, String faceurl, String action,String identity) {

        if (!mAvatarListAdapter.addItem(new TCSimpleUserInfo(identity, nickname, faceurl))){
            return;
        }
        //IM人数
        getGroupNumber();
        userAvatarListScrollto();
        lTotalMemberCount++;
//        int itemCount = mAvatarListAdapter.getItemCount();
//        mCurrentMemberCount = itemCount;
//        mMemberCount.setText(String.format("%d", mCurrentMemberCount)+ "人");

        if ("L".equals(action)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    robot.setLanguage("点赞");
                    chatRoom.sendRobotMessage(TCConstants.IMCMD_ROBOT_PRAISE,robot);
                }
            },2000);
        }else if ("T".equals(action)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    chatRoom.sendRobotMessage(TCConstants.IMCMD_ROBOT_CHAT,robot);
                }
            },2000);
        }

        ChatEntity entity = new ChatEntity();
        entity.setSendName("直播消息");
        if(nickname.equals(""))
            entity.setContext(userId + "加入直播");
        else
            entity.setContext(nickname + "加入直播");
        entity.setType(TCConstants.MEMBER_ENTER);
        entity.setIdentity(identity);
        entity.setLevel(robot.getLevel());
        notifyMsg(entity);
    }

    @Subscribe
    public void shutUp(ShutUpEvent shutUpEvent){
        String identity = shutUpEvent.getIdentity();
        String name = shutUpEvent.getName();
        if (shutUpGroupIdList != null){
            shutUpGroupIdList.add(identity);
        }else{
            shutUpGroupIdList = new ArrayList<>();
            shutUpGroupIdList.add(identity);
        }

        Map<String,String> map = new HashMap<>();
        map.put("shutup_id",identity);
        map.put("shutup_name",name);
        JSONObject s = Tools.map2JsonNoToString(map);
        chatRoom.sendShutUpMessage(TCConstants.IMCMD_SHUTUP,s);

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


    private Map<String ,String> giftMap;
    private boolean isFinish = false;
    public Handler giftHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case selectGift://选了礼物
                    Object obj = message.obj;
                    giftMap = (Map<String, String>) obj;
                    giftMap.put("user_id", SPUtils.getString(SPUtils.USER_IDENTITY));
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

                case TCConstants.OPEN_USER_BY_IDENTITY://8
                    Object objs = message.obj;
                    String identity = (String) objs;
                    Log.d("livelog",identity);
                    boolean shutUped = isShutUped(identity);
                    UserCardActivity.goCardActivity(LivePublisherNewActivity.this,identity,false,group_id,isOwner(identity),shutUped);
                    overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
//                    Intent intent = new Intent(LivePublisherNewActivity.this, UserCardActivity.class);
//                    intent.putExtra(TCConstants.UCODE, identity);
//                    startActivity(intent);
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

    private boolean isOwner(String identity){
        String string = SPUtils.getString(SPUtils.USER_IDENTITY);
        if (string.equals(identity)){
            return false;
        }
        return true;
    }

    private boolean isShutUped(String identity){
        String string = SPUtils.getString(SPUtils.USER_IDENTITY);
        if (string.equals(identity)){
            return false;
        }

        if (shutUpGroupIdList == null){
            shutUpGroupIdList = new ArrayList<>();
        }
        for (int i=0;i<shutUpGroupIdList.size();i++){
            String id = shutUpGroupIdList.get(i);
            if (id.equals(identity)){
                return true;
            }
        }
        return false;
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


    private Uri createCoverUri(String type) {
        String filename = SPUtils.getString(SPUtils.USER_IDENTITY) + type + ".jpg";
        File outputImage = new File(Environment.getExternalStorageDirectory(), filename);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, TCConstants.WRITE_PERMISSION_REQ_CODE);
            return null;
        }
        if (outputImage.exists()) {
            outputImage.delete();
        }

        return Uri.fromFile(outputImage);
    }

    /**
     *  获取TakePhoto实例
     * @return
     */
    public TakePhoto getTakePhoto(){
        if (takePhoto==null){
            takePhoto= (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
        }
        return takePhoto;
    }


    private CropOptions getCropOptions(){
        //不裁剪
//        if(rgCrop.getCheckedRadioButtonId()!=R.id.rbCropYes)return null;
        int height= 800;//Integer.parseInt(etCropHeight.getText().toString());
        int width= 800;//Integer.parseInt(etCropWidth.getText().toString());
        boolean withWonCrop= false;//rgCropTool.getCheckedRadioButtonId()==R.id.rbCropOwn? true:false;

        CropOptions.Builder builder=new CropOptions.Builder();

//        if(rgCropSize.getCheckedRadioButtonId()==R.id.rbAspect){
//            builder.setAspectX(width).setAspectY(height);
//        }else {
        builder.setOutputX(width).setOutputY(height);
        builder.setAspectX(1).setAspectY(1);
//        }
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }

    @Subscribe
    public void ShowTagEvent(TagEvent tagEvent){
        tv_tag.setText(tagEvent.getName());
        if (!TextUtils.isEmpty(tagEvent.getContent())){
            et_live_title.setText("#" + tagEvent.getContent() + "#");
        }else {
            et_live_title.setText("");
        }

        tagId = tagEvent.getTagId();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }
}
