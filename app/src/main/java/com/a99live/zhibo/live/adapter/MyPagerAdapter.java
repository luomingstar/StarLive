package com.a99live.zhibo.live.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 首页fragment切换adapter
 * Created by fuyk on 2016/8/29.
 */
public class MyPagerAdapter  extends FragmentPagerAdapter {
    private List<Fragment> views;
    private List<String> mTille;

    public MyPagerAdapter(FragmentManager fragmentManager,List<Fragment> mViewList, List<String> mTiele) {
        super(fragmentManager);
        this.views = mViewList;
        this.mTille = mTiele;
    }

    @Override
    public Fragment getItem(int position) {
        return views.get(position);
    }

    @Override
    public int getCount() {
        return views.size();
    }


    //配置标题的方法
    @Override
    public CharSequence getPageTitle(int position) {
        return mTille.get(position);
    }

}
