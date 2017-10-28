package com.a99live.zhibo.live.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.utils.SPUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fuyk on 2016/12/13.
 */

public class WithdrawSuccessActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_withdraw_number)
    TextView tv_withdraw_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_success);
        ButterKnife.bind(this);
        initView();
    }

    public static void goWithdrawSuccessActivity(Context context){
        Intent intent = new Intent(context, WithdrawSuccessActivity.class);
        context.startActivity(intent);
    }

    private void initView() {
        tv_title.setText("申请成功");
        tv_withdraw_number.setText("本次提现：" + SPUtils.getString(SPUtils.JIU_RMB) + "元");
    }

    @OnClick({R.id.layout_back, R.id.tv_withdraw_back_home})
    void onClick(View view){
        switch (view.getId()){
            case R.id.layout_back:
                back();
                break;
            case R.id.tv_withdraw_back_home:
                back();
                break;
        }
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
