package com.a99live.zhibo.live.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.model.UMengInfo;
import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveHttpUrl;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.AppProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.TCUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.okhttp.Response;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.onlineconfig.OnlineConfigAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 欢迎界面
 */
public class WelcomeActivity extends BaseActivity {

//    private String h5Domain;
//    private ImProtocol imProtocol;
//    private LoginHelper loginPresenter;
    @Bind(R.id.ad_img)
    ImageView mAdImg;

    @Bind(R.id.ad_frame)
    FrameLayout mAdFrame;

    @Bind(R.id.skip)
    TextView mSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //判断是否是第一次开启应用
//        boolean isFirstOpen = SharedPreferencesUtil.getBoolean(this, SharedPreferencesUtil.FIRST_OPEN, true);
//        // 如果是第一次启动，则先进入功能引导页
//        if (isFirstOpen) {
//            Intent intent = new Intent(this, WelcomeGuideActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }

        // 如果不是第一次启动app，则正常显示启动屏
        setContentView(R.layout.acticity_splash);
        ButterKnife.bind(this);
        mAdFrame.setVisibility(View.GONE);
//        loginPresenter = LoginHelper.getInstance();

        Context context = getApplicationContext();
        XGPushManager.registerPush(context, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int i) {
                Log.d("livelogxinge", "注册信鸽成功，设备token为：" + data);
            }

            @Override
            public void onFail(Object o, int errCode, String msg) {
                Log.d("livelogxinge", "注册信鸽失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
//        Intent service = new Intent(context, XGPushService.class);
//        context.startService(service);

        getOnlineConfig();
        getPremiss();
//        getToken();
//        initTask();//判断是去登录还是去mainActivity，已放在下方getToken
    }

    private void getAD(){
        AppProtocol appProtocol = new AppProtocol();
        LiveRequestParams params = new LiveRequestParams();
        params.put("type","start");
        appProtocol.getBanner(params)
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
                                String append = dataMap.get("append");
                                initAD(data,append);
                                return;
                            }else {

                            }
                        }else {

                        }
                        initTask();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        initTask();
                    }
                });
    }

    /**
     * "id":"8",
     "img_path":"9bf683abfb480ac59a9ea5b2602c7828",
     "type":"2",
     "address":"http://www.baidu.com",
     "desc":"asffadsfas",
     "title":"asdfasd",
     "sort_key":"1",
     "start_time":"2016-12-03 08:10:15",
     "end_time":"2016-12-30 08:10:15",
     "full_image_path":"http://99live-10063116.image.myqcloud.com/9bf683abfb480ac59a9ea5b2602c7828"
     * @param data
     */
    private void initAD(String data,String append){
        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(data);
        if (listMapByJson.size()>0){
            mAdFrame.setVisibility(View.VISIBLE);
            Map<String, String> map = listMapByJson.get(0);
            ArrayList<Map<String, String>> listMapByJson1 = JsonUtil.getListMapByJson(append);
            Map<String, String> map1 = listMapByJson1.get(0);
            final String uri = map1.get("uri");

            Glide.with(this)
                    .load(map.get("full_image_path"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
//                    .placeholder(R.drawable.welcome)
                    .dontAnimate()
                    .into(mAdImg);

            ObjectAnimator animator = ObjectAnimator.ofFloat(mAdImg,"Alpha",0.5f,1.0f);
            animator.setDuration(1000);
            animator.start();
            final String url = map.get("address");
            final String title = map.get("title");
            final String desc = map.get("desc");
            final String is_login = map.get("is_login");

            final CountDownTimer timer = new CountDownTimer(6000,500) {
                @Override
                public void onTick(long l) {
                    long time =  l/1000;
                    mSkip.setText("跳过("+time+")s");
                }

                @Override
                public void onFinish() {
//                    mSkip.setText("跳过("+0+")S");
                    if (!isLoad)
                        initIndex(url,title,desc,false,is_login,uri);
                }
            };

            mAdFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isLoad) {
                        timer.cancel();
                        initIndex(url, title, desc,true,is_login,uri);
                    }
                }
            });

            mSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isLoad){
                        timer.cancel();
                        initIndex("","","",false,"","");
                    }
                }
            });
            timer.start();
        }else {
            initTask();
        }

    }

    /**
     * 跳转网页
     * @param url
     * @param title
     * @param desc
     */
    private boolean isLoad;
    private void initIndex(String url, String title, String desc,boolean isShowWeb,String is_login,String uri){
        isLoad = true;
        if (!TextUtils.isEmpty(SPUtils.getString(SPUtils.USER_CODE)) && !TextUtils.isEmpty(SPUtils.getString(SPUtils.USER_UAUTH))){
            HomeActivity.goHomeActivity(WelcomeActivity.this,url, UMengInfo.title,desc,isShowWeb,is_login,uri);
            //测试需要
//            startActivity(new Intent(WelcomeActivity.this, InterestActivity.class));
//            overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
        }else {
            //没有登陆过，去登录界面
            LoginActivity.goLoginActivity(WelcomeActivity.this,url, UMengInfo.title,desc,isShowWeb,is_login,uri);
        }
        WelcomeActivity.this.finish();
    }

    private void getOnlineConfig(){
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
                OnlineConfigAgent.getInstance().updateOnlineConfig(getApplication());

                String value = OnlineConfigAgent.getInstance().getConfigParams(UIUtils.getContext(), TCConstants.YOUMENGKEY);
                String level = OnlineConfigAgent.getInstance().getConfigParams(LiveZhiBoApplication.getContext(),TCConstants.YOUMENGLEVELKEY);
                Log.d("livelog", value);
                Log.d("livelog",level);
                ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(value);
                if (listMapByJson.size() >0){
                    Map<String, String> map = listMapByJson.get(0);
                    String api = map.get("api");
                    String version = map.get("version");
                    String h5 = "";
                    if(map.containsKey("h5")){
                        h5 = map.get("h5");
                    }
                    ArrayList<Map<String, String>> h5List = JsonUtil.getListMapByJson(h5);

                    ArrayList<Map<String, String>> apiList = JsonUtil.getListMapByJson(api);
                    if (apiList.size()>0){
                        Map<String, String> map1 = apiList.get(0);
                        String domain = map1.get("domain");
                        String port = map1.get("port");
                        String h5Domain = "";
                        String h5Port = "";
                        if (h5List.size()>0){
                            Map<String, String> map2 = h5List.get(0);
                            h5Domain =  map2.get("domain");
                            h5Port = map2.get("port");
                            LiveHttpUrl.setH5DomainPort(domain,port);
                        }
                        //内存保存
                        LiveHttpUrl.setDomainPort(domain,port);
                        String apiVersion = SPUtils.getString(SPUtils.API_VERSION);
                        if (apiVersion != null && !"".equals(apiVersion)){
                            if (Integer.parseInt(version) > Integer.parseInt(apiVersion)){
                                SPUtils.putString(SPUtils.API_DOMAIN,domain);
                                SPUtils.putString(SPUtils.API_PORT,port);
                                SPUtils.putString(SPUtils.API_VERSION,version);
                                SPUtils.putString(SPUtils.H5_DOMAIN,h5Domain);
                                SPUtils.putString(SPUtils.H5_PORT,h5Port);
                            }
                        }else{
                            SPUtils.putString(SPUtils.API_DOMAIN,domain);
                            SPUtils.putString(SPUtils.API_PORT,port);
                            SPUtils.putString(SPUtils.API_VERSION,version);
                            SPUtils.putString(SPUtils.H5_DOMAIN,h5Domain);
                            SPUtils.putString(SPUtils.H5_PORT,h5Port);
                        }
                    }
                }

        ArrayList<Map<String, String>> levelList = JsonUtil.getListMapByJson(level);
        if (levelList.size()>0){
            Map<String, String> levelMap = levelList.get(0);
            String user = levelMap.get("user");
            String version = levelMap.get("version");
            String levelVersion = SPUtils.getString(SPUtils.LEVEL_VERSION);
            if (TextUtils.isEmpty(levelVersion)){
                ArrayList<Map<String, String>> userList = JsonUtil.getListMapByJson(user);
                if (userList.size()>0){
                    Map<String, String> userMap = userList.get(0);
                    String special = userMap.get("special");
                    SPUtils.putString(SPUtils.LEVEL_SPECIAL,special);
                    SPUtils.putString(SPUtils.LEVEL_VERSION,version);
                }
            }else{
                if (Integer.parseInt(version) > Integer.parseInt(levelVersion)){
                    ArrayList<Map<String, String>> userList = JsonUtil.getListMapByJson(user);
                    if (userList.size()>0){
                        Map<String, String> userMap = userList.get(0);
                        String special = userMap.get("special");
                        SPUtils.putString(SPUtils.LEVEL_SPECIAL,special);
                        SPUtils.putString(SPUtils.LEVEL_VERSION,version);
                    }
                }
            }
        }
//            }
//        }.run();
    }

    private void getPremiss(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    100);
        }else{
            getToken();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getToken();
                } else {
                    finish();
                }
                return;
            }
        }
    }

    private void getToken() {
        //判断网络是否可用
        if (!TCUtils.isNetworkAvailable(this)){
            initTask();
            return;
        }


        new Thread() {
            @Override
            public void run() {
            try {
                Response response = LiveHttpClient.getClient().execute(LiveHttpClient.getClient().getTokenRequest());
                if(response.code()>=200 && response.code()<300) {
                    String result = response.body().string();
                    Log.d("livelog_result","result" + result);

                    ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(result);
                    if (listMapByJson.size()>0){
                        Map<String, String> map = listMapByJson.get(0);
                        if ("0".equals(map.get("code"))){
                            String data = map.get("data");
                            ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                            if (dataList.size() > 0){
                                Map<String, String> dataMap = dataList.get(0);
                                String key = dataMap.get("key");
                                String global = dataMap.get("global");
                                String msg_exp_time = "";
                                ArrayList<Map<String, String>> globalList = JsonUtil.getListMapByJson(global);
                                if (globalList.size()>0){
                                    Map<String, String> globaMap = globalList.get(0);
                                    msg_exp_time = globaMap.get("msg_exp_time");
                                    SPUtils.putString(SPUtils.WITHDRAW_LINE, globaMap.get("withdraw_line"));

                                }

                                SPUtils.putString(SPUtils.TAG_TOKEN, key);
                                SPUtils.putString(SPUtils.TAG_TIME, msg_exp_time);

                            }
                        }
                    }

//                    getImSig();//由于纯在im消息串流的情况，现在每次进直播开直播都会重新登录im，这里的登录就多了
                    getAD();
                }else{
                    Log.d("livelog" , "获取Token失败");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initTask();
                        }
                    });
                }

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initTask();
                    }
                });
            }
            }
        }.start();
    }

    /**
     * 请求IM签名
     */
//    private void getImSig(){
//        LiveRequestParams params = new LiveRequestParams();
//
//        imProtocol = new ImProtocol();
//        imProtocol.getIMSign(params)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String s) {
//
//                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
//                        if (listMapByJson.size()>0){
//                            Map<String, String> map = listMapByJson.get(0);
//                            if ("0".equals(map.get("code"))){
//                                String data = map.get("data");
//                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
//                                if (dataList.size()>0){
//                                    Map<String, String> dataMap = dataList.get(0);
//                                    goLoginIM(dataMap);
//                                }
//
//                            }else{
//                                Log.d("liveLog",map.get("msg"));
//                            }
//                        }else{
//
//                        }
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        Log.e("livelog","error-");
//                    }
//                });
//    }

//    private void goLoginIM(Map<String,String> iMsigInfo) {
//        loginPresenter.imLogin(iMsigInfo.get("uid"), iMsigInfo.get("sig"));
//    }

//    @Override
//    public void onIMLoginSuccess() {
//        Log.d("livelog_wl", "登录IM成功");
//    }
//
//    @Override
//    public void onIMLoginError(int code, String msg) {
//        Log.d("livelog_wl", "登录IM失败");
//    }

    private void initTask(){
        /**是否登录过*/
        if (!TextUtils.isEmpty(SPUtils.getString(SPUtils.USER_CODE)) && !TextUtils.isEmpty(SPUtils.getString(SPUtils.USER_UAUTH))){
            goHomeActivity();
        }else {
            //没有登陆过，去登录界面
            goLoginPage();
        }
    }

    private void goHomeActivity() {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                        finish();
                        //测试需要
//                        startActivity(new Intent(WelcomeActivity.this, InterestActivity.class));
//                        overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
                    }
                });
    }

    public void goLoginPage() {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 信鸽的数据
     */
//    @Override
//    protected void onStart() {
//        super.onStart();
//        XGPushClickedResult xgPushClickedResult = XGPushManager.onActivityStarted(this);
//        if (xgPushClickedResult != null){
//
//        String customContent = xgPushClickedResult.getCustomContent();
//        UIUtils.showToast(customContent);
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**屏蔽物理返回按钮*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
