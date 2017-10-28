package com.a99live.zhibo.live.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.a99live.zhibo.live.fragment.ListLive.Channel.ChannelItemFragment;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by fuyk on 2016/12/23.
 */

public class ChannelFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<Map<String, String >> channelTag;

    public ChannelFragmentAdapter(FragmentManager fragmentManager, ArrayList<Map<String, String >> channelTag) {
        super(fragmentManager);
        this.channelTag = channelTag;
    }

    @Override
    public Fragment getItem(int position) {
        Map<String, String> stringStringMap = channelTag.get(position);
        String id = stringStringMap.get("id");
        Bundle bundle = new Bundle();
        bundle.putString("channelId", id);
        ChannelItemFragment fragment = new ChannelItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return channelTag.size();
    }


    //配置标题的方法
    @Override
    public CharSequence getPageTitle(int position) {
        return channelTag.get(position).get("name");
    }

}
