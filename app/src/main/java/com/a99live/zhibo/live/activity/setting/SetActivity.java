package com.a99live.zhibo.live.activity.setting;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.activity.LoginActivity;
import com.a99live.zhibo.live.activity.imchatc2c.FriendshipEvent;
import com.a99live.zhibo.live.activity.imchatc2c.MessageEvent;
import com.a99live.zhibo.live.activity.imchatc2c.RefreshEvent;
import com.a99live.zhibo.live.model.FriendshipInfo;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.presenter.LoginHelper;
import com.a99live.zhibo.live.protocol.AppProtocol;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.token.PhoneManager;
import com.a99live.zhibo.live.utils.FileUtils;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.utils.UpdateManager;
import com.a99live.zhibo.live.view.MyDialog;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 设置界面
 * Created by fuyk on 2016/8/27.
 */
public class SetActivity extends BaseActivity {

    private AppProtocol versionProtocol;
    private UserProtocol userProtocol;

    private UpdateManager updateManager;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_cache)
    TextView tv_cache;

    @Bind(R.id.tv_version)
    TextView tv_version;

    private static Activity mActivity;

    public static void goSetActivity(Activity activity){
        activity.startActivity(new Intent(activity, SetActivity.class));
        mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        versionProtocol = new AppProtocol();
        userProtocol = new UserProtocol();
    }

    private void initView() {
        tv_title.setText(R.string.set);

        tv_cache.setText(FileUtils.getCacheSize());

        String clientVersion = PhoneManager.getVersionName(this);
        tv_version.setText("V."+ clientVersion);
    }

    @OnClick({R.id.layout_back, R.id.layout_tel_me, R.id.layout_feed_back, R.id.layout_clear, R.id.layout_about, R.id.layout_check, R.id.layout_user_agreement, R.id.exits})
    void onCick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                back();
                break;
            case R.id.layout_tel_me:
                goTelMe();
                break;
            case R.id.layout_feed_back:
                goFeedback();
                break;
            case R.id.layout_clear:
                FileUtils.clearCache();
                SPUtils.putString(SPUtils.VIDEO_LIKE,"");
                tv_cache.setText(FileUtils.getCacheSize());
                break;
            case R.id.layout_about:
                goAboutPage();
                break;
            case R.id.layout_check:
                UpdateManager.getInstance().getUpdate(this,true);
                break;
            case R.id.layout_user_agreement:
                goUserAgreement();
                break;
            case R.id.exits:
                new MyDialog(this)
                        .setMessage(R.string.exits)
                        .setLeftButtonState(R.string.cancel, null)
                        .setRightButtonState(R.string.enter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                EventBus.getDefault().post(new ShowHomeEvent());
                                loginOut();
//                                goLoginPage();
                            }
                        }).show();
                break;
            default:
                break;
        }
    }



    /**
     * 用户登出接口
     */
    private void loginOut(){
        LiveRequestParams params = new LiveRequestParams();

        userProtocol.getUserLoginOut(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> dataMap = listMapByJson.get(0);
                            if ("0".equals(dataMap.get("code"))){
                                goLoginPage();
                            }else {
                                UIUtils.showToast(dataMap.get("msg"));
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

    /**
     * 跳转到登陆页
     */
    private void goLoginPage() {
        SPUtils.putString(SPUtils.USER_CODE, "");
        SPUtils.putString(SPUtils.USER_UAUTH, "");
        SPUtils.putString(SPUtils.IMSIG, "");
        SPUtils.putString(SPUtils.IMUID, "");

        //注册消息监听
        MessageEvent.getInstance().deleteObservers();
        //注册刷新监听
        RefreshEvent.getInstance().deleteObservers();
//        //注册好友关系链监听
        FriendshipEvent.getInstance().deleteObservers();
//        //注册群关系监听
//        GroupEvent.getInstance().deleteObservers();
        //登出im防止登录其他号之后im还是以前的人；
        LoginHelper.getInstance().imLoginOut();
        MessageEvent.getInstance().clear();
        FriendshipInfo.getInstance().clear();
//        TlsBusiness.logout(UserInfo.getInstance().getId());
//        UserInfo.getInstance().setId(null);
//        GroupInfo.getInstance().clear();
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.tran_right_in, R.anim.scale_small_out);
        finish();
        if (mActivity != null){
            mActivity.finish();
            mActivity = null;
        }
    }

    /**
     * 跳转到联系我们页
     */
    private void goTelMe() {
        Intent intent = new Intent();
        intent.setClass(this, TelMeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tran_right_in, R.anim.scale_small_out);
    }

    /**
     * 跳转到反馈页
     */
    private void goFeedback() {
        Intent intent = new Intent();
        intent.setClass(this, FeedBackActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tran_right_in, R.anim.scale_small_out);
    }

    /**
     * 跳转到关于99页
     */
    private void goAboutPage() {
        Intent intent = new Intent();
        intent.setClass(this, AboutUsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tran_right_in, R.anim.scale_small_out);
    }

    /**
     * 跳转到用户协议页
     */
    private void goUserAgreement() {
        Intent intent = new Intent();
        intent.setClass(this, UserAgreementActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tran_right_in, R.anim.scale_small_out);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        overridePendingTransition(R.anim.scale_big_in, R.anim.tran_right_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }
}
