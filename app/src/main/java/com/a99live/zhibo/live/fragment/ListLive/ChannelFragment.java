package com.a99live.zhibo.live.fragment.ListLive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.ChannelFragmentAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.NewRoomProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.CircleProgressView;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/12/20.
 */

public class ChannelFragment extends RxFragment {

    @Bind(R.id.tabs_channel)
    TabLayout mTabLayout;

    @Bind(R.id.channel_view_pager)
    ViewPager mViewPager;

    @Bind(R.id.rl_error)
    RelativeLayout rl_error;

//    private LayoutInflater mInflater;
//    private List<String> mTitleList = new ArrayList<>();//页卡标题集合


    protected boolean isVisible;
    protected  boolean isPrepared;

    private CircleProgressView progressView;
    private ChannelFragmentAdapter mAdapter;
    private ArrayList<Map<String, String>> data;
    private View view;

    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }
    protected void onVisible(){
        getTagFromNet();
    }
    //    protected abstract void preLoad();
    protected void onInvisible(){}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ChannelFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ChannelFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_channel, null);
            progressView = (CircleProgressView) view.findViewById(R.id.loadingImageView);
            progressView.setVisibility(View.VISIBLE);
            progressView.spin();
            ButterKnife.bind(this, view);
            //提前加载
            data = new ArrayList<>();
            mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//设置tab模式，当前为系统默认模式
            mAdapter = new ChannelFragmentAdapter(getChildFragmentManager(), data);
//        }
            mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
            mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
            mTabLayout.setTabsFromPagerAdapter(mAdapter);
            isPrepared = true;
            getTagFromNet();
        }else{
            //避免引起挂载多个Parent
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view);
            }
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressView != null && progressView.isSpinning()){
            progressView.stopSpinning();
        }

    }

    /**获取标签接口*/
    private void getTagFromNet(){
        if (!isPrepared || !isVisible){
            return;
        }
        isPrepared = false;
        progressView.setVisibility(View.VISIBLE);
        rl_error.setVisibility(View.GONE);
        LiveRequestParams params = new LiveRequestParams();

        NewRoomProtocol newRoomProtocol = new NewRoomProtocol();
        newRoomProtocol.getTag(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                if (dataList.size()>0){
                                    initView(dataList);
                                }
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{
                            UIUtils.showToast(s);
                        }
                        if (progressView != null ){
                            progressView.setVisibility(View.GONE);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (progressView != null ){
                            progressView.setVisibility(View.GONE);
                        }
                        UIUtils.showToast("网络异常");
                        rl_error.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void initView(ArrayList<Map<String, String>> dataList) {
//        mInflater = LayoutInflater.from(getActivity());
//        if (mAdapter==null) {
        mTabLayout.removeAllTabs();
        data.clear();
        for (int i = 0; i < dataList.size(); i++){
            Map<String, String> channelData = dataList.get(i);

//                mTitleList.add(channelData.get("name"));
            mTabLayout.addTab(mTabLayout.newTab().setText(channelData.get("name")));
        }
        data.addAll(dataList);
        mAdapter.notifyDataSetChanged();
//        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//设置tab模式，当前为系统默认模式
//        mAdapter = new ChannelFragmentAdapter(getChildFragmentManager(), dataList);
////        }
//        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
//        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
//        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
    }

    @OnClick({R.id.rl_error})
    void onClick(View view){
        switch (view.getId()){
            case R.id.rl_error:
                isPrepared = true;
                getTagFromNet();
                break;
        }
    }
}
