package com.a99live.zhibo.live.view.weight;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.GridView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.GiftItemAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by JJGCW on 2016/10/11.
 */

public class GiftItemView {
    private Context mContext;
    private GiftItemAdapter adapter;
    private  List<Map<String,String>> mData;
    private Handler mHandler;
    private int mPage;

    public GiftItemView(Context context, List<Map<String,String>>data, Handler handler,int page){
        this.mContext = context;
        this.mData = data;
        this.mHandler = handler;
        this.mPage = page;
    }

    public View initView(){
        View view = View.inflate(mContext, R.layout.gift_item_view,null);
        GridView gridView = (GridView) view.findViewById(R.id.gift_item_gridview);
        adapter = new GiftItemAdapter(mPage,mHandler,mContext,mData,R.layout.gift_item,new String[]{"price"},new int[]{R.id.gift_num});
        gridView.setAdapter(adapter);
        return  view;
    }

    public void refresh(int page,int position){
        adapter.setRefresh(page,position);
    }

}
