package com.a99live.zhibo.live.activity.xiaovideo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.activity.xiaovideo.adapter.VideoFragmetnAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.VideoProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.CircleProgressView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by JJGCW on 2017/4/12.
 */

public class XiaoVideoViewPage extends BaseActivity {
    private CircleProgressView progressView;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tabs_channel)
    TabLayout mTabLayout;

    @Bind(R.id.channel_view_pager)
    ViewPager mViewPager;

    @Bind(R.id.rl_error)
    RelativeLayout rl_error;
    private List<Map<String,String>> tags;
    private VideoFragmetnAdapter mAdapter;

    public static void goXiaoVideoViewPage(Context context){
        Intent intent = new Intent(context,XiaoVideoViewPage.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaovideopage);
        progressView = (CircleProgressView) findViewById(R.id.loadingImageView);
        progressView.setVisibility(View.VISIBLE);
        progressView.spin();
        ButterKnife.bind(this);
        tv_title.setText("小视频");
        tags = new ArrayList<>();
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//设置tab模式，当前为系统默认模式
        mAdapter = new VideoFragmetnAdapter(getSupportFragmentManager(), tags);
//        }
        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
        getTagFromNet();
    }

    private void getTagFromNet(){
        progressView.setVisibility(View.VISIBLE);
        rl_error.setVisibility(View.GONE);
        LiveRequestParams params = new LiveRequestParams();

        VideoProtocol newRoomProtocol = new VideoProtocol();
        newRoomProtocol.getVideoTag(params)
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
        tags.clear();
        for (int i = 0; i < dataList.size(); i++){
            Map<String, String> channelData = dataList.get(i);

//                mTitleList.add(channelData.get("name"));
            mTabLayout.addTab(mTabLayout.newTab().setText(channelData.get("name")));
        }
        tags.addAll(dataList);
        mAdapter.notifyDataSetChanged();
//        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//设置tab模式，当前为系统默认模式
//        mAdapter = new ChannelFragmentAdapter(getChildFragmentManager(), dataList);
////        }
//        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
//        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
//        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
    }

    @OnClick({R.id.rl_error,R.id.layout_back})
    void onClick(View view){
        switch (view.getId()){
            case R.id.rl_error:
                getTagFromNet();
                break;
            case R.id.layout_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressView != null && progressView.isSpinning()){
            progressView.stopSpinning();
        }
    }



}
