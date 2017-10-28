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
import com.a99live.zhibo.live.utils.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 修改昵称界面
 * Created by fuyk on 2016/8/26.
 */
public class NickNameActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_finish)
    TextView tv_finish;

    @Bind(R.id.et_pet_name)
    EditText et_pet_name;

    @Bind(R.id.ll_pet_name)
    LinearLayout ll_pet_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_name);
        ButterKnife.bind(this);
        ll_pet_name.setOnTouchListener(new View.OnTouchListener() {
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
        tv_title.setText(R.string.nickName);
        et_pet_name.setText(getIntent().getStringExtra("pet_name"));
        tv_finish.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick({R.id.layout_back, R.id.tv_finish})
    void OnClick(View view) {
        String pet_name = et_pet_name.getText().toString().trim();
        switch (view.getId()) {
            case R.id.layout_back:
                back();
                break;
            case R.id.tv_finish:
                if (TextUtils.isEmpty(pet_name)) {
                    Toast.makeText(NickNameActivity.this, R.string.input_pet_name_empty, Toast.LENGTH_SHORT).show();
                    return;
                }else if (pet_name.length() > 12) {
                    UIUtils.showToast("昵称不要超过12位哦~");
                    return;
                }
                goSubmitPetName(pet_name);
                break;
        }
    }

    /**
     * 提交昵称
     *
     * @param pet_name
     */
    private void goSubmitPetName(String pet_name) {
        Intent intent = new Intent(NickNameActivity.this, UserInfoActivity.class);
        intent.putExtra("petName", pet_name);
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
