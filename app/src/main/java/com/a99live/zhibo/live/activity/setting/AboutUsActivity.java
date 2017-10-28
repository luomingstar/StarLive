package com.a99live.zhibo.live.activity.setting;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
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
 * 关于我们
 * Created by fuyk on 2016/8/30.
 */
public class AboutUsActivity extends BaseActivity {
    private SettingProtocol aboutProtocol;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_about_detail)
    TextView tv_about_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        aboutProtocol = new SettingProtocol();
        getAbout();
    }

    private void initView() {
        tv_title.setText(R.string.about_live);
    }

    /**
     * 关于99接口
     */
    private void getAbout(){
        LiveRequestParams params = new LiveRequestParams();

        aboutProtocol.about(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog", "about" + s);
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");
                                initPage(data);
                            }else {
                                UIUtils.showToast("网络异常");
                            }
                        }else {
                            UIUtils.showToast("网络异常");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast("网络异常");
                    }
                });
    }

    private void initPage(String data) {
        tv_about_detail.setText(data);
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