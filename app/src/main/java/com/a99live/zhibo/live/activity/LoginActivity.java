package com.a99live.zhibo.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.Tools;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.ShowWeb;
import com.a99live.zhibo.live.view.weight.MyProgressDialog;
import com.tencent.android.tpush.XGPushManager;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 登陆界面
 * Created by fuyk on 2016/8/24.
 */
public class LoginActivity extends BaseActivity {

    private UserProtocol userProtocol;
    private TimeCount mCodeTime;

    @Bind(R.id.layout_back)
    RelativeLayout layout_back;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.edit_phoneNumber)
    EditText et_phone;

    @Bind(R.id.edit_pwd)
    EditText et_pwd;

    @Bind(R.id.tv_get_code)
    TextView tv_get_code;

    @Bind(R.id.layout_login)
    LinearLayout layout_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        layout_login.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        initView();
        initData();
    }

    public static void goLoginActivity(Context context, String url, String title, String desc,boolean isShowWeb,String is_login,String uri){
        Intent intent = new Intent(context,LoginActivity.class);
        if (isShowWeb) {
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("desc", desc);
            intent.putExtra("is_login", is_login);
            intent.putExtra("uri", uri);
        }
        context.startActivity(intent);
    }

    private void initData() {
        userProtocol = new UserProtocol();
        mCodeTime = new TimeCount(60000,1000);
    }

    private void initView() {
        layout_back.setVisibility(View.GONE);
        tv_title.setText(R.string.phone_login);
    }


    @OnClick({R.id.tv_get_code, R.id.tv_login})
    void onClick(View view){
        switch (view.getId()){
            case R.id.tv_get_code:
                onClicksecurity();
                break;
            case R.id.tv_login:
                onClickLogin();
                break;
        }
    }

    /**
     * 点击登录
     */
    private void onClickLogin() {
        String phoneNumber = et_phone.getText().toString().trim();
        String verifySms = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this,R.string.nophonenumber,Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(verifySms)){
            Toast.makeText(this,R.string.no_verifysms,Toast.LENGTH_SHORT).show();
        }else {
            initProgressDialog();
            login(phoneNumber, verifySms);
        }
    }

    private MyProgressDialog dialog;
    private void initProgressDialog() {
        dialog = new MyProgressDialog(this,"登录中...");
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 用户登录接口
     */
    private void login(String phone, String code) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("mobile", phone);
        params.put("sms", code);

        userProtocol.getUserLogin(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i("login====", s);

                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");

                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                if (dataList.size() > 0){
                                    Map<String, String> dataMap = dataList.get(0);
                                    if ("1".equals(dataMap.get("type"))){//新用户
                                        //昵称设置
                                        goSetNamePage(dataMap);
                                    }else if ("0".equals(dataMap.get("type"))){//老用户
                                        //去首页
                                        goHomePage(dataMap);
                                    }
                                }
                            }else if ("11111112".equals(map.get("code"))){
                                UIUtils.showToast(R.string.code_error);
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{
                            UIUtils.showToast(s);
                        }
                        if (dialog != null)
                            dialog.dismiss();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
                        if (dialog != null)
                            dialog.dismiss();
                    }
                });
    }


    /**
     * 去设置昵称页
     */
    private void goSetNamePage(Map<String, String> dataMap) {
        //保存用户信息
        SPUtils.putString(SPUtils.USER_CODE, dataMap.get("ucode"));
        SPUtils.putString(SPUtils.USER_UAUTH, dataMap.get("uauth"));

        //绑定账号注册
        XGPushManager.registerPush(this, dataMap.get("ucode"));

        //设置标签
        ArrayList<Map<String, String>> tags = JsonUtil.getListMapByJson(dataMap.get("tags"));

        if (dataMap.get("tags") != null && !"".equals(dataMap.get("tags"))) {
            for (int i = 0;i<tags.size();i++){
                XGPushManager.setTag(this, ""+ tags.get(i) );
            }
        }

        Intent intent = new Intent(this, SetNameActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tran_right_in, R.anim.scale_small_out);
        finish();
    }

    private void goHomePage(Map<String, String> dataMap) {
        //保存用户信息
        SPUtils.putString(SPUtils.USER_CODE, dataMap.get("ucode"));
        SPUtils.putString(SPUtils.USER_UAUTH, dataMap.get("uauth"));

        //绑定账号注册
        XGPushManager.registerPush(this, dataMap.get("ucode"));

        //设置标签
        ArrayList<Map<String, String>> tags = JsonUtil.getListMapByJson(dataMap.get("tags"));

        if (dataMap.get("tags") != null && !"".equals(dataMap.get("tags"))) {
            for (int i = 0;i<tags.size();i++){
                XGPushManager.setTag(this, ""+ tags.get(i) );
            }
        }

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tran_right_in, R.anim.scale_small_out);
        finish();
    }

    /**
     * 点击获取验证码
     */
    private void onClicksecurity() {
        String phoneNumber = et_phone.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this,R.string.nophonenumber,Toast.LENGTH_SHORT).show();
        }else if (!Tools.isMobile(phoneNumber)){
            Toast.makeText(this,R.string.putrightphnum, Toast.LENGTH_SHORT).show();
        }else {
            getCode(phoneNumber);

        }
    }

    /**
     * 发送短信验证码
     * @param phone
     */
    private void getCode(String phone) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("phone", phone);

        userProtocol.getVerifySms(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String,String>> list = JsonUtil.getListMapByJson(s);
                        if (list.size()>0){
                            Map<String,String> map = list.get(0);
                            String code = map.get("code");
                            if ("0".equals(code)){
                                mCodeTime.start();
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast("请检查网络设置");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        overridePendingTransition(R.anim.scale_big_in, R.anim.tran_right_out);
        finish();
    }

    /**
     * 倒计时
     */
    class TimeCount extends CountDownTimer {

        //总时长和计时的时间间隔
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程显示
        @Override
        public void onTick(long millisUntilFinished) {
            tv_get_code.setText(getResources().getString(R.string.getvercode_new)
                    + "(" + millisUntilFinished / 1000 + ")");
            tv_get_code.setTextColor(getResources().getColor(R.color.gray));
            tv_get_code.setBackgroundResource(R.drawable.shape_get_code_press);
            tv_get_code.setClickable(false);
            tv_get_code.setEnabled(false);
        }

        //计时完毕时触发
        @Override
        public void onFinish() {
            tv_get_code.setText(R.string.getvercode);
            tv_get_code.setTextColor(getResources().getColor(R.color.text_5a5b));
            tv_get_code.setEnabled(true);
            tv_get_code.setClickable(true);
            tv_get_code.setBackgroundResource(R.drawable.shape_get_code);
        }
    }
}
