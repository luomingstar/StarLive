package com.a99live.zhibo.live.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.event.BindingAccountEvent;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.MyProgressDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/12/13.
 */

public class BindingAccountActivity extends BaseActivity {

    private UserProtocol userProtocol;
    private TimeCount mCodeTime;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.et_alipay_account)
    EditText et_alipay_account;

    @Bind(R.id.et_real_name)
    EditText et_real_name;

    @Bind(R.id.tv_check_phone)
    TextView tv_check_phone;

    @Bind(R.id.tv_get_code)
    TextView tv_get_code;

    @Bind(R.id.et_check_code)
    EditText et_check_code;

    @Bind(R.id.tv_binding_alipay_hint)
    TextView tv_binding_alipay_hint;

    @Bind(R.id.tv_now_binding)
    TextView tv_now_binding;

    @Bind(R.id.ll_layout_binding)
    LinearLayout ll_layout_binding;

    private String account;
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_account);
        ButterKnife.bind(this);
        ll_layout_binding.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        initView();
        initData();
    }

    public static void goBindingAccountActivity(Context context){
        Intent intent = new Intent(context, BindingAccountActivity.class);
        context.startActivity(intent);
    }

    private void initData() {
        userProtocol = new UserProtocol();
        mCodeTime = new TimeCount(60000,1000);
    }

    private void initView() {
        tv_title.setText(R.string.binging_account);
        mobile = SPUtils.getString(SPUtils.USER_MOBILE);

        tv_check_phone.setText(TCUtils.StringChange(mobile));

        String hint = "<font color='#999999'>注：请仔细确认支付宝账户和姓名</font><font color='#FF5A5B'>[是否正确]</font>" +
                "</font><font color='#999999'>否则绑定后的所有</font></font><font color='#FF5A5B'>[提现操作都将失败]</font>";
        tv_binding_alipay_hint.setText(Html.fromHtml(hint));

        et_check_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(et_alipay_account.getText().toString().trim()) &&
                        !TextUtils.isEmpty(et_real_name.getText().toString().trim()) &&
                        !TextUtils.isEmpty(tv_check_phone.getText().toString().trim())) {

                    if (!TextUtils.isEmpty(et_check_code.getText().toString())) {
                        tv_now_binding.setBackgroundResource(R.drawable.shape_earning);
                    }else {
                        tv_now_binding.setBackgroundResource(R.drawable.shape_binding);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @OnClick({R.id.layout_back, R.id.tv_now_binding, R.id.tv_get_code})
    void onClick(View view){
        switch (view.getId()){
            case R.id.layout_back:
                back();
                break;
            case R.id.tv_get_code:
                getCode(mobile);
                break;
            case R.id.tv_now_binding:
                String sms = et_check_code.getText().toString().trim();
                account = et_alipay_account.getText().toString().trim();
                String name = et_real_name.getText().toString().trim();
                if (TextUtils.isEmpty(sms)){
                    UIUtils.showToast(R.string.no_verifysms);
                } else if (TextUtils.isEmpty(account)){
                    UIUtils.showToast(R.string.no_account);
                } else if (TextUtils.isEmpty(name)){
                    UIUtils.showToast(R.string.no_name);
                }else {
                    initProgressDialog();
                    getBindAccount(mobile, sms, account, name);
                }
                break;
        }
    }

    /**发送短信验证码*/
    private void getCode(String phone) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("phone", phone);

        userProtocol.getVerifySms(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i("VerifySms------",s);
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
                        UIUtils.showToast(R.string.net_error);
                    }
                });
    }


    private MyProgressDialog dialog;
    private void initProgressDialog() {
        dialog = new MyProgressDialog(this,"绑定中...");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void getBindAccount(String mobile, String sms, final String account, String name){
        LiveRequestParams params = new LiveRequestParams();
        params.put("mobile", mobile);
        params.put("sms", sms);
        params.put("account", account);
        params.put("name", name);
        params.put("type", "alipay");

        userProtocol.bindingAccount(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog", "返回" + s );
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                EventBus.getDefault().post(new BindingAccountEvent(account));
                                UIUtils.showToast("绑定成功");
                                finish();
                            }else {
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else {
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

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        finish();
        overridePendingTransition(R.anim.scale_big_in, R.anim.tran_right_out);
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
