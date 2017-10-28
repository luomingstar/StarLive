package com.a99live.zhibo.live.activity.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 我的收益页面
 * Created by fuyk on 2016/11/25.
 */

public class EarnActivity extends BaseActivity {

    private UserProtocol userProtocol;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_finish)
    TextView tv_finish;

    @Bind(R.id.tv_coin)
    TextView tv_coin;

    @Bind(R.id.tv_rmb)
    TextView tv_rmb;

    @Bind(R.id.tv_account)
    TextView tv_account;

    @Bind(R.id.tv_replace)
    TextView tv_replace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earning);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        tv_title.setText(R.string.my_earning);
        tv_finish.setVisibility(View.VISIBLE);
        tv_finish.setText("提现记录");
        tv_finish.setTextColor(getResources().getColor(R.color.note));
        tv_coin.setText(TextUtils.isEmpty(SPUtils.getString(SPUtils.JIU_BI)) ? "0" : SPUtils.getString(SPUtils.JIU_BI));
        tv_rmb.setText("￥" + (TextUtils.isEmpty(SPUtils.getString(SPUtils.JIU_RMB))?"0":SPUtils.getString(SPUtils.JIU_RMB)));

        if (!TextUtils.isEmpty(TCUtils.StringChange(SPUtils.getString(SPUtils.ACCOUNT)))){
            tv_account.setText("支付宝(" + TCUtils.StringChange(SPUtils.getString(SPUtils.ACCOUNT)) + ")");
            tv_replace.setText("更换");
        }else {
            tv_replace.setText("绑定");
        }


    }

    private void initData() {
        userProtocol = new UserProtocol();
    }

    @Subscribe
    public void bindingAccount(BindingAccountEvent bindingAccountEvent){
        String accout = TCUtils.StringChange(bindingAccountEvent.getAlipayAccount());
        tv_account.setText("支付宝(" + accout + ")");
    }

    @OnClick({R.id.layout_back, R.id.tv_get_rmb, R.id.rl_layout_binding,R.id.tv_finish})
    void onClick(View view){
        switch (view.getId()) {
            case R.id.layout_back:
                back();
                break;
            case R.id.tv_get_rmb:
                String r = SPUtils.getString(SPUtils.JIU_RMB);
                String mix = SPUtils.getString(SPUtils.WITHDRAW_LINE);
                float rmb = TextUtils.isEmpty(r) ? 0 : Float.parseFloat(r);
                float mixRmb = TextUtils.isEmpty(mix) ? 50 : Float.parseFloat(mix);
                if (rmb >= mixRmb){
                    getMoney();
                }else {
                    UIUtils.showToast("未达到最小提现金额");
                }
                break;
            case R.id.rl_layout_binding:
                BindingAccountActivity.goBindingAccountActivity(EarnActivity.this);
                break;
            case R.id.tv_finish:
                WithDrawalRecordActivity.goWithDrawlRecordActivity(EarnActivity.this);
                break;
        }
    }

    private void getMoney(){
        LiveRequestParams params = new LiveRequestParams();

        userProtocol.getWithdraw(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                back();
                                WithdrawSuccessActivity.goWithdrawSuccessActivity(EarnActivity.this);
                            }else {
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else {
                            UIUtils.showToast(R.string.net_error);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
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
}
