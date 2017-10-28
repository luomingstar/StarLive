package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.LivePlayerActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JJGCW on 2016/10/11.
 */

public class GiftItemAdapter extends SimpleAdapter {
    private Context mContext;
    private List<Map<String,String>> mData;
    private Handler mHandler;
    private int mPage;

    public GiftItemAdapter(int page,Handler handler,Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.mContext = context;
        this.mData = new ArrayList<>();
        this.mData.addAll((List<Map<String,String>>) data);
        this.mHandler = handler;
        this.mPage = page;
    }

    public void setRefresh(int page , int position){
        if (page == mPage){

        }else{
            for (int i=0;i<mData.size();i++){
                mData.get(i).put("select","0");
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Map<String, String> dataMap = mData.get(position);
        View view =  super.getView(position, convertView, parent);
        ImageView gift_img = (ImageView) view.findViewById(R.id.gift_img);
        ImageView select = (ImageView) view.findViewById(R.id.select);
        setGiftImage(gift_img,dataMap.get("img_path"));
        //是否为连击礼物
        String isContinue = dataMap.get("continue");
        if (dataMap.containsKey("select")){
            if ("1".equals(dataMap.get("select"))){
                select.setVisibility(View.VISIBLE);
                select.setImageResource(R.drawable.gift_select);
            }else{
                if ("1".equals(isContinue)){
                    select.setVisibility(View.VISIBLE);
                    select.setImageResource(R.drawable.gift_lian);
                }else{
                    select.setVisibility(View.INVISIBLE);
                }
            }
        }else{
            if ("1".equals(isContinue)){
                select.setVisibility(View.VISIBLE);
                select.setImageResource(R.drawable.gift_lian);
            }else{
                select.setVisibility(View.INVISIBLE);
            }
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mData.get(position).containsKey("select")){
                    if ("1".equals(mData.get(position).get("select"))){
                        mData.get(position).put("select","0");
                        if (mHandler != null)
                            mHandler.obtainMessage(LivePlayerActivity.cancleGift).sendToTarget();//取消礼物
                    }else{
                        if (mHandler != null){
                            Message message = mHandler.obtainMessage(LivePlayerActivity.selectGift);//选择礼物
                            dataMap.put("pageItem",mPage+"");
                            dataMap.put("position",position+"");
                            message.obj = dataMap;
                            message.sendToTarget();
                        }

                        for (int i=0;i<mData.size();i++){
                            if ( i == position){
                                mData.get(i).put("select","1");
                            }else{
                                mData.get(i).put("select","0");
                            }
                        }
                    }
                }else{
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage(LivePlayerActivity.selectGift);//选择礼物
                        dataMap.put("pageItem", mPage + "");
                        dataMap.put("position", position + "");
                        message.obj = dataMap;
                        message.sendToTarget();
                    }
                    for (int i=0;i<mData.size();i++){
                        if ( i == position){
                            mData.get(i).put("select","1");
                        }else{
                            mData.get(i).put("select","0");
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });

        return view;
    }

    private void setGiftImage(ImageView giftImage,String img_path){
        Glide.with(mContext)
                .load(img_path)
//                    .load("http://99live-10063116.image.myqcloud.com/d6eb9b9d3978552a880be01c8d9f5349?imageMogr2/thumbnail/x200")
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(R.mipmap.head)
                .crossFade()
                //.transform(new GlideCircleTransform(getActivity()))
                .into(giftImage);
    }
}
