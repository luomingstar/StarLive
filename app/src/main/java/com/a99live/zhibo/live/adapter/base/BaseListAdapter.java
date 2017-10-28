package com.a99live.zhibo.live.adapter.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by fuyk on 2016/9/11.
 */
public abstract class BaseListAdapter extends BaseAdapter {

    protected  Context mContext;
    private  List<Map<String,String>> mData;

    public BaseListAdapter(Context c , List<Map<String,String>> data){
        this.mContext = c;
        this.mData = data;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map<String, String> getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public abstract View getView(int i, View view, ViewGroup viewGroup);

    public List<Map<String,String>> getData() {
        return mData;
    }

    public void setData(List<Map<String,String>> mData) {
        this.mData = mData;
    }
}
