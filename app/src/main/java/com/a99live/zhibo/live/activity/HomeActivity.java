package com.a99live.zhibo.live.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.imchatc2c.FriendshipEvent;
import com.a99live.zhibo.live.activity.imchatc2c.MessageEvent;
import com.a99live.zhibo.live.activity.imchatc2c.RefreshEvent;
import com.a99live.zhibo.live.event.ShowLiveDialogEvent;
import com.a99live.zhibo.live.fragment.HomeFragment;
import com.a99live.zhibo.live.model.FriendshipInfo;
import com.a99live.zhibo.live.model.UserInfo;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.presenter.LoginHelper;
import com.a99live.zhibo.live.protocol.ImProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.utils.UpdateManager;
import com.a99live.zhibo.live.view.MyDialog;
import com.a99live.zhibo.live.view.ShowWeb;
import com.tencent.TIMManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/8/24.
 */
public class HomeActivity extends BaseActivity implements LoginHelper.IMLoginCallback {

    private Fragment mContentFragment;
    private UpdateManager instance;
    private Timer timer;

    private LoginHelper loginPresenter;
    //    @Bind(R.id.updata)
//    RelativeLayout updata;
//
//    @Bind(R.id.progress)
//    TextView progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (bundle.containsKey("url")){
                String url = bundle.getString("url");
                String title = bundle.getString("title");
                String desc = bundle.getString("desc");
                String is_login = bundle.getString("is_login");
                String uri = bundle.getString("uri");
                ShowWeb.goShowWeb(this,url,title,desc,is_login,uri);
            }
        }
        EventBus.getDefault().register(this);
        LiveZhiBoApplication.mHomeAct = this;
        initFragment();
        instance = UpdateManager.getInstance();
        instance.getUpdate(this,false);
        loginPresenter = LoginHelper.getInstance();

        //登录之前要初始化群和好友关系链缓存
        FriendshipEvent.getInstance().init();
//        GroupEvent.getInstance().init();
        //初始化TLS
        //初始化imsdk
        TIMManager.getInstance().init(LiveZhiBoApplication.getApp());
        //设置刷新监听
        RefreshEvent.getInstance();

//        if (Build.VERSION.SDK_INT >= 23) {
//            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    ) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        1);
//            }else{
//
//            }
//
//            if ( PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        2);
//            }else{
//
//            }
//
//        }else{
//            UpdateManager.getInstance().getUpdate(this,false);
//        }
    }

    public static void goHomeActivity(Context context,String url, String title, String desc,boolean isShowWeb,String is_login,String uri){
        Intent intent = new Intent(context,HomeActivity.class);
        if (isShowWeb) {
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("desc", desc);
            intent.putExtra("is_login", is_login);
            intent.putExtra("uri", uri);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
        loginPresenter.removeIMLoginCallback();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
        if (SPUtils.getString(SPUtils.USER_CODE) == "" && "".equals(SPUtils.getString(SPUtils.USER_UAUTH))){
            finish();
        }
        loginPresenter.setIMLoginCallback(this);
        //用户未登录IM 进行IM登录
        if (TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())){
            goLoginIm();
        }else{
            String uid = SPUtils.getString(SPUtils.IMUID);
            String sig = SPUtils.getString(SPUtils.IMSIG);
            if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(sig)){
                getImSig();
            }
        }
    }

    private void goLoginIm(){
        String uid = SPUtils.getString(SPUtils.IMUID);
        String sig = SPUtils.getString(SPUtils.IMSIG);
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(sig)){
            getImSig();
        }else{
            UserInfo.getInstance().setId(uid);
            goLoginIM(uid,sig);
        }
    }

    @Subscribe
    public void onShowLiveDialogEvent(ShowLiveDialogEvent event){
        final String roomId = event.getRoomId();
        final String ucode = event.getUcode();
        String content = event.getContent();
        new MyDialog(this).setMessage(content).setLeftButtonState("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setRightButtonState("立即查看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(HomeActivity.this,LivePlayerActivity.class);
                intent.putExtra("room_id",roomId);
                intent.putExtra("ucode",ucode);
                startActivity(intent);
            }
        }).setCanceledOnTouchOutside(false).show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 加载fragment
     */
    private void initFragment() {
            mContentFragment = new HomeFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_content, mContentFragment, mContentFragment.getClass().getName())
                    .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    this.finish();
                }
                return;
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    this.finish();
                }
                return;
            case 1:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    instance.showDownloadDialog();
                }
                break;
            case 2:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    instance.installAPK();
                }
                break;
        }
    }


    private boolean quit = false;   //设置退出标识

    @Override
    public void onBackPressed() {
        if (quit == false) {        //询问退出程序
            UIUtils.showToast("再按一次退出程序");
            if (timer == null){
                timer = new Timer(true);
            }
            timer.schedule(new TimerTask() {      //启动定时任务
                @Override
                public void run() {
                    timer.cancel();
                    timer = null;
                    quit = false;
                    //重置退出标识
                }
            }, 2000);               //2秒后运行run()方法
            quit = true;
        } else {                    //确认退出程序
            super.onBackPressed();
            finish();
        }
    }

    int number = 0;

    private void goLoginIM(String uid,String sig) {
        loginPresenter.imLogin(uid, sig);
        number = number++;
    }


    /**
     * 请求IM签名
     */
    ImProtocol imProtocol;
    private void getImSig(){
        LiveRequestParams params = new LiveRequestParams();
        if (imProtocol == null) {
            imProtocol = new ImProtocol();
        }
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
                                String data = map.get("data");
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                if (dataList.size()>0){
                                    Map<String, String> dataMap = dataList.get(0);
                                    String uid = dataMap.get("uid");
                                    String sig = dataMap.get("sig");
                                    SPUtils.putString(SPUtils.IMUID,uid);
                                    SPUtils.putString(SPUtils.IMSIG,sig);
                                    UserInfo.getInstance().setId(uid);
                                    goLoginIM(uid,sig);
                                }

                            }else{
                                Log.d("liveLog",map.get("msg"));
                            }
                        }else{

                        }


                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("livelog","error-");
                    }
                });
    }

    @Override
    public void onIMLoginSuccess() {
        Log.d("livelog","IM登录成功了");
        //初始化消息监听
        MessageEvent.getInstance();
        FriendshipInfo.getInstance().getFriends();
        if (mContentFragment != null) {
            ((HomeFragment) mContentFragment).setConversation();
        }
    }

    @Override
    public void onIMLoginError(int code, String msg) {
        Log.d("livelog","IM登录失败了");
        if (number <= 2) {
            goLoginIm();
        }else{


        }
    }
}
