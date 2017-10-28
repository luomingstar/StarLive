package com.a99live.zhibo.live.activity.xiaovideo.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.a99live.zhibo.live.activity.xiaovideo.fragment.VideoItemFragment;

import java.util.List;
import java.util.Map;

/**
 * Created by JJGCW on 2017/4/12.
 */

public class VideoFragmetnAdapter extends FragmentPagerAdapter {

    private List<Map<String,String>> tagData;

    public VideoFragmetnAdapter(FragmentManager fm,List<Map<String,String>> tagData) {
        super(fm);
        this.tagData = tagData;
    }

    @Override
    public Fragment getItem(int position) {
        Map<String, String> stringStringMap = tagData.get(position);
        String id = stringStringMap.get("id");
        Bundle bundle = new Bundle();
        bundle.putString("channelId", id);
        VideoItemFragment fragment = new VideoItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return tagData==null ? 0 : tagData.size();
    }

    //配置标题的方法
    @Override
    public CharSequence getPageTitle(int position) {
        return tagData.get(position).get("name");
    }
}
