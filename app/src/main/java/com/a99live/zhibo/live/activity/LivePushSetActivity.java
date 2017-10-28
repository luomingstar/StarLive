package com.a99live.zhibo.live.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.presenter.LocationHelper;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.TCUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.utils.YMClick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 开播进入页
 * Created by fuyk on 2016/8/25.
 */
public class LivePushSetActivity extends BaseActivity implements LocationHelper.OnLocationListener {

    private String city = "";

    private boolean mPermission = false;

    @Bind(R.id.layout_ready)
    RelativeLayout layout_ready;

    @Bind(R.id.tv_local)
    TextView tv_local;

    @Bind(R.id.et_live_title)
    EditText et_live_title;

    @Bind(R.id.iv_cover)
    ImageView iv_cover;

    @Bind(R.id.tv_cover_hint)
    TextView tv_cover_hint;

    @Bind(R.id.tv_begin_live)
    TextView tv_begin_live;

    @Bind(R.id.checktext)
    TextView checkText;

    @Bind(R.id.check_box)
    CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_live);
        ButterKnife.bind(this);
        layout_ready.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        initData();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (LocationHelper.checkLocationPermission(LivePushSetActivity.this)) {
                    if (!LocationHelper.getMyLocation(LivePushSetActivity.this, LivePushSetActivity.this)) {
                        tv_local.setText(getString(R.string.text_live_lbs_fail));
                        Toast.makeText(getApplicationContext(), "定位失败，请查看是否打开GPS", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 500);
    }

    private void initView() {
        Glide.with(this)
                .load(SPUtils.getString(SPUtils.USER_AVATAR))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(R.mipmap.logo_head)
                .crossFade()
                .into(iv_cover);
        //地理位置
        tv_local.setText(R.string.text_live_location);

    }

    private void initData() {
        //检测推流权限
        mPermission = checkPublishPermission();
    }

    @OnClick({R.id.iv_gps, R.id.iv_exit, R.id.iv_cover, R.id.iv_circle_Friends, R.id.iv_wexin_friend,
            R.id.iv_qq, R.id.iv_weibo, R.id.tv_begin_live, R.id.checktext})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_gps:
                tv_local.setText(R.string.text_live_location);
                if (LocationHelper.checkLocationPermission(this)) {
                    if (!LocationHelper.getMyLocation(this, this)) {
                        tv_local.setText(getString(R.string.text_live_lbs_fail));
                        Toast.makeText(getApplicationContext(), "定位失败，请查看是否打开GPS", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.iv_exit:
                back();
                break;
            case R.id.iv_cover:

                break;
            case R.id.iv_circle_Friends:
//                UMengInfo.getUserShare(SPUtils.getString(SPUtils.USER_ID), "", false ,this ,umShareListener,SHARE_MEDIA.WEIXIN_CIRCLE,SPUtils.getString(SPUtils.USER_NAME));

//                ShareAction wei_circle = new ShareAction(this);
//                wei_circle.withText(UMengInfo.getText(SPUtils.getString(SPUtils.USER_NAME)));
//                wei_circle.withMedia(new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher));
//                wei_circle.withTitle(UMengInfo.title);
//                wei_circle.withTargetUrl(UMengInfo.getH5Center(SPUtils.getString(SPUtils.USER_ID)));
//                wei_circle.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener).share();
                break;
            case R.id.iv_wexin_friend:
//                UMengInfo.getUserShare(SPUtils.getString(SPUtils.USER_ID), "", false ,this ,umShareListener,SHARE_MEDIA.WEIXIN,SPUtils.getString(SPUtils.USER_NAME));


//                ShareAction wei_friend = new ShareAction(this);
//                wei_friend.withText(UMengInfo.getText(SPUtils.getString(SPUtils.USER_NAME)));
//                wei_friend.withMedia(new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher));
//                wei_friend.withTitle(UMengInfo.title);
//                wei_friend.withTargetUrl(UMengInfo.getH5Center(SPUtils.getString(SPUtils.USER_ID)));
//                wei_friend.setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener).share();
                break;
            case R.id.iv_qq:
//                UMengInfo.getUserShare(SPUtils.getString(SPUtils.USER_ID), "", false,this,umShareListener,SHARE_MEDIA.QQ,SPUtils.getString(SPUtils.USER_NAME));

//                ShareAction qq = new ShareAction(this);
//                qq.withText(UMengInfo.getText(SPUtils.getString(SPUtils.USER_NAME)));
//                qq.withMedia(new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher));
//                qq.withTitle(UMengInfo.title);
//                qq.withTargetUrl(UMengInfo.getUserShare(SPUtils.getString(SPUtils.USER_ID), "", false));
//                qq.setPlatform(SHARE_MEDIA.QQ).setCallback(umShareListener).share();
                break;
            case R.id.iv_weibo:
//                UMengInfo.getUserShare(SPUtils.getString(SPUtils.USER_ID), "", false ,this ,umShareListener,SHARE_MEDIA.SINA,SPUtils.getString(SPUtils.USER_NAME));


//                ShareAction weibo = new ShareAction(this);
//                weibo.withText(UMengInfo.getText(SPUtils.getString(SPUtils.USER_NAME)));
//                weibo.withMedia(new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher));
//                weibo.withTitle(UMengInfo.title);
//                weibo.withTargetUrl(UMengInfo.getH5Center(SPUtils.getString(SPUtils.USER_ID)));
//                weibo.setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener).share();
                break;
            case R.id.tv_begin_live:
                String isDefaultAvatar = SPUtils.getString(SPUtils.USER_IS_DEFAULT_AVATAR);
                if (!"N".equals(isDefaultAvatar)){
                    UIUtils.showToast("请到个人中心设置头像");
                    break;
                }else{

                }

                if (!checkBox.isChecked()) {
                    UIUtils.showToast("请遵守《99直播管理条例》");
                    break;
                }
                if (!TCUtils.isNetworkAvailable(this)) {
                    Toast.makeText(getApplicationContext(), "当前网络环境不能发布直播", Toast.LENGTH_SHORT).show();
                } else {
                    goStartLive();
                }
                //直播统计
                YMClick.onEvent(LivePushSetActivity.this, YMClick.STRAT_LIVE, "start_live");
                break;
            case R.id.checktext:
                checkBox.setChecked(!checkBox.isChecked());
                break;
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Toast.makeText(LivePushSetActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            Toast.makeText(LivePushSetActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(LivePushSetActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };


    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
//            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[permissions.size()]),
                        TCConstants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }


    /**
     * 开始直播推流
     */
    private void goStartLive() {
        String title = et_live_title.getText().toString().trim();
        Intent intent = new Intent(this, LivePublisherActivity.class);
//        intent.putExtra("nick_name", dataMap.get("nickname"));
//        intent.putExtra("user_head", dataMap.get("avatar"));
        intent.putExtra("title", title);
        intent.putExtra("city", city);
        startActivity(intent);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case TCConstants.LOCATION_PERMISSION_REQ_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!LocationHelper.getMyLocation(this, this)) {
                        tv_local.setText(getString(R.string.text_live_lbs_fail));
                    }
                }
                break;
            case TCConstants.WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                mPermission = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(final int code, double lat1, double long1, final String location) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (0 == code) {
                    tv_local.setText(location);
                    city = location;
                } else {
                    tv_local.setText(getString(R.string.text_live_lbs_fail));
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    void back() {
        onBackPressed();
    }
}
