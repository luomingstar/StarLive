package com.a99live.zhibo.live.activity.setting;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import butterknife.OnTextChanged;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 反馈界面
 * Created by fuyk on 2016/8/30.
 */
public class FeedBackActivity extends BaseActivity {
    private SettingProtocol feedBackProtocol;

    private static final int LENGTH_FEEDBACK = 400;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.et_feedback)
    EditText et_feedback;

    @Bind(R.id.tv_num)
    TextView tv_num;

    @Bind(R.id.ll_feedback)
    LinearLayout ll_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        ll_feedback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        initData();
        initView();
    }

    private void initData() {
        feedBackProtocol = new SettingProtocol();
    }

    private void initView() {
        tv_title.setText(R.string.feed_back);
        et_feedback.setFilters(new InputFilter[]{new InputFilter.LengthFilter(LENGTH_FEEDBACK)});
    }

    /**
     * 联系我们接口
     */
    private void getFeedBack(String feedbackText){
        LiveRequestParams params = new LiveRequestParams();
        params.put("suggest", feedbackText);

        feedBackProtocol.feedback(params)
                .compose(this.<String>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog_feedback", "about" + s);
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                UIUtils.showToast("提交成功");
                                finish();
                            }else {
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else {
                            UIUtils.showToast("提交失败");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast("提交失败");
                    }
                });
    }

    @OnClick({R.id.layout_back, R.id.tv_feed_back_finish})
    void onClick(View view) {
        String feedbackText = et_feedback.getText().toString().trim();
        switch (view.getId()){
            case R.id.layout_back:
                back();
                break;
            case R.id.tv_feed_back_finish:
                if (TextUtils.isEmpty(feedbackText)){
                    UIUtils.showToast(R.string.feed_back_empty);
                    return;
                }
                getFeedBack(feedbackText);
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

    @OnTextChanged(R.id.et_feedback)
    void onFeedbackTextChange(CharSequence s) {
        tv_num.setText(String.valueOf(s.toString().trim().length()) + getString(R.string.feedback_length));
    }
}
