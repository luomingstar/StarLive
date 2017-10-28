package com.a99live.zhibo.live.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.LivePublisherNewActivity;
import com.a99live.zhibo.live.event.ShowHomeEvent;
import com.a99live.zhibo.live.event.ShowUserEvent;
import com.a99live.zhibo.live.fragment.ListLive.LiveListFragment;
import com.a99live.zhibo.live.fragment.User.UserFragment;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.SharedPreferencesUtil;
import com.a99live.zhibo.live.utils.TCUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.utils.YMClick;
import com.a99live.zhibo.live.view.GuideView;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 主界面Fragment
 * Created by fuyk on 2016/8/24.
 */
@SuppressLint("ValidFragment")
public class HomeFragment extends RxFragment {

    @Bind(R.id.tv_home)
    TextView tv_home;

    @Bind(R.id.iv_home)
    ImageView iv_home;

    @Bind(R.id.tv_me)
    TextView tv_me;

    @Bind(R.id.iv_me)
    ImageView iv_me;

    @Bind(R.id.iv_live_show)
    ImageView iv_live_show;

    @Bind(R.id.userfragment)
    RelativeLayout userframent;

    @Bind(R.id.one)
    ImageView one;

    @Bind(R.id.two)
    ImageView two;

    @Bind(R.id.three)
    ImageView three;



    private GuideView guideView;

    private LiveListFragment mListLiveFragment;
    private UserFragment mUserFragment;

    public HomeFragment(){

    }

    public void setConversation(){
        if (mListLiveFragment!= null){
            mListLiveFragment.setSome();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        userframent.setVisibility(View.GONE);
        one.setVisibility(View.VISIBLE);
        two.setVisibility(View.GONE);
        three.setVisibility(View.GONE);
        boolean isFirstHome = SharedPreferencesUtil.getBoolean(getActivity(), SharedPreferencesUtil.FIRST_OPEN_HOME, true);
        if (isFirstHome){
            setGuideView();
        }
//        initView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        initView();
    }

    private void initView() {
        if (mListLiveFragment == null || mUserFragment == null){
            mListLiveFragment = new LiveListFragment();
            mUserFragment = new UserFragment();

            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.home_content, mListLiveFragment)
                    .add(R.id.home_content, mUserFragment)
                    .commit();

            openFragment(mListLiveFragment);
            tv_home.setSelected(true);
            iv_home.setSelected(true);
        }
    }

    private void openFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .hide(mListLiveFragment)
                .hide(mUserFragment)
                .show(fragment)
                .commitAllowingStateLoss();
    }

    @Subscribe
    public  void  onShowHomeEvent(ShowHomeEvent event){
        Log.i("livelog" , "home");
        initSelected();
        tv_home.setSelected(true);
        iv_home.setSelected(true);
        openFragment(mListLiveFragment);
    }

    @Subscribe
    public  void onShowUserEvent(ShowUserEvent event){
        initSelected();
        openFragment(mUserFragment);
        tv_me.setSelected(true);
        iv_me.setSelected(true);
    }


    @OnClick({R.id.bottom_list_live, R.id.iv_live_show, R.id.bottom_user})
    void tabOnClick(View view) {
        switch (view.getId()) {
            case R.id.bottom_list_live:
                initSelected();
                tv_home.setSelected(true);
                iv_home.setSelected(true);
                openFragment(mListLiveFragment);
                break;
            case R.id.iv_live_show:
//                if (SPUtils.getInt(SPUtils.USER_CODE) != 0 && !TextUtils.isEmpty(SPUtils.getString(SPUtils.USER_UAUTH))) {
                if (!TCUtils.isNetworkAvailable(getActivity())){
                    UIUtils.showToast("请检查网络设置");
                }else {
                    goLiveShowActivity();
                }
                YMClick.onEvent(getContext(),"live_count","1");
//                }
                break;
            case R.id.bottom_user:
                if (SPUtils.getString(SPUtils.USER_CODE) != "" && !TextUtils.isEmpty(SPUtils.getString(SPUtils.USER_UAUTH))) {
                    initSelected();
                    tv_me.setSelected(true);
                    iv_me.setSelected(true);
                    openFragment(mUserFragment);
                    boolean isFirstUser = SharedPreferencesUtil.getBoolean(getActivity(), SharedPreferencesUtil.FIRST_OPEN_USER, true);
                    if (isFirstUser){
                        addUserHint();
                    }
                }
                break;
        }
    }

    private void setGuideView(){
        ImageView iv = new ImageView(getActivity());
        iv.setImageResource(R.drawable.guide_shouye);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iv.setLayoutParams(params);

        guideView = GuideView.Builder
                .newInstance(getActivity())
                .setTargetView(iv_live_show)
                .setCustomGuideView(iv)
                .setDirction(GuideView.Direction.TOP)
                .setShape(GuideView.MyShape.CIRCULAR)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView.hide();
                    }
                })
                .build();
        guideView.show();
        SharedPreferencesUtil.putBoolean(getActivity(), SharedPreferencesUtil.FIRST_OPEN_HOME, false);
    }

    private void goLiveShowActivity() {
//        startActivity(new Intent(getActivity(), LivePushSetActivity.class));
        startActivity(new Intent(getActivity(), LivePublisherNewActivity.class));
    }

    private void initSelected() {
        tv_home.setSelected(false);
        tv_me.setSelected(false);
        iv_home.setSelected(false);
        iv_me.setSelected(false);
    }

    private void addUserHint(){
        userframent.setVisibility(View.VISIBLE);
        userframent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (one.getVisibility()==View.VISIBLE){
                    one.setVisibility(View.GONE);
                    two.setVisibility(View.VISIBLE);
                }else if (two.getVisibility() == View.VISIBLE){
                    two.setVisibility(View.GONE);
                    three.setVisibility(View.VISIBLE);
                }else if (three.getVisibility() == View.VISIBLE){
                    userframent.setVisibility(View.GONE);
                }
            }
        });
        SharedPreferencesUtil.putBoolean(getActivity(), SharedPreferencesUtil.FIRST_OPEN_USER, false);
    }

}
