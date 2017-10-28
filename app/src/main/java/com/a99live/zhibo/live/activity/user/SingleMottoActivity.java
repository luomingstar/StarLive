package com.a99live.zhibo.live.activity.user;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.utils.SystemBarTintManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人签名界面
 * Created by fuyk on 2016/8/26.
 */
public class SingleMottoActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_finish)
    TextView tv_finish;

    @Bind(R.id.et_single_motto)
    EditText et_single_motto;

    @Bind(R.id.ll_single_motto)
    LinearLayout ll_single_motto;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_motto);
        ButterKnife.bind(this);
        ll_single_motto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        initView();
        initData();
    }

    private void initData() {
    }

    private void initView() {
        et_single_motto.setText(getIntent().getStringExtra("single_motto"));
        tv_title.setText(R.string.motto);
        tv_finish.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.layout_back, R.id.tv_finish})
    void OnClick(View view) {
        String single_motto = et_single_motto.getText().toString().trim();
        switch (view.getId()) {
            case R.id.layout_back:
                back();
                break;
            case R.id.tv_finish:
                if (TextUtils.isEmpty(et_single_motto.getText().toString())) {
                    Toast.makeText(SingleMottoActivity.this, R.string.input_pet_name_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                goSubmitSingleMotto(single_motto);
                break;
        }
    }

    /**
     * 提交个人签名
     *
     * @param single_motto
     */
    private void goSubmitSingleMotto(String single_motto) {
        Intent intent = new Intent(SingleMottoActivity.this, UserInfoActivity.class);
        intent.putExtra("single_motto", single_motto);
        setResult(RESULT_OK, intent);
        finish();
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
