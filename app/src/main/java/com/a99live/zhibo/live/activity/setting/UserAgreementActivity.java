package com.a99live.zhibo.live.activity.setting;

import android.os.Bundle;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.SettingProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 用户协议界面
 * Created by fuyk on 2016/8/30.
 */
public class UserAgreementActivity extends BaseActivity {
    private SettingProtocol aboutProtocol;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_user_agreement_detail)
    TextView tv_user_agreement_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        aboutProtocol = new SettingProtocol();
        getUserAgreement();
    }

    private void initView() {
        tv_title.setText(R.string.user_agreement);
    }

    /**
     * 用户协议接口
     */
    private void getUserAgreement(){
        LiveRequestParams params = new LiveRequestParams();

        aboutProtocol.agreement(params)
                .compose(this.<String>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");
                                initPage(data);
                            }else {
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else {
                            UIUtils.showToast(s);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
                    }
                });
    }

    private void initPage(String data) {
        tv_user_agreement_detail.setText(data);
    }

    @OnClick(R.id.layout_back)
    void back() {
        finish();
        overridePendingTransition(R.anim.scale_big_in, R.anim.tran_right_out);
    }

    @Override
    public void onBackPressed() {
        back();
    }
}
