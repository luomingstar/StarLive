package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.LivePlayerActivity;
import com.a99live.zhibo.live.activity.VoderActivity;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.TCUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Map;

/**
 * Created by fuyk on 2016/12/23.
 */

public class ChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<Map<String,String>> mChannelList;

    public ChannelAdapter(Context context, List<Map<String,String>> data) {
        this.context = context;
        this.mChannelList = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false);
        ChannelViewHolder channelViewHolder = new ChannelViewHolder(view);
        return channelViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Map<String, String> channelMap = mChannelList.get(position);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ((ChannelViewHolder) holder).iv_channel_bg.getLayoutParams();
        layoutParams.height = (UIUtils.getScreenWidth())/2;
        ((ChannelViewHolder) holder).iv_channel_bg.setLayoutParams(layoutParams);

        if (position%2==0){
            ((ChannelViewHolder) holder).rl_channel_item.setPadding(0,0,UIUtils.getDimen(R.dimen.dp_1),0);
        }else{
            ((ChannelViewHolder) holder).rl_channel_item.setPadding(UIUtils.getDimen(R.dimen.dp_1),0,0,0);
        }

        //背景
        Glide.with(context)
                .load(channelMap.get("bg_img_url"))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                .override(200,100)
                .centerCrop()
                .placeholder(R.mipmap.list_cover)
//                .crossFade()
                .into(((ChannelViewHolder)holder).iv_channel_bg);
        //状态
        String type = "L";
        if (channelMap.containsKey("type")){
            type = channelMap.get("type");
            if ("Z".equals(type)){
                ((ChannelViewHolder)holder).iv_channel_state.setImageResource(R.mipmap.type_state_live);
                ((ChannelViewHolder)holder).iv_channel_bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, LivePlayerActivity.class);
                        intent.putExtra("room_id", channelMap.get("room_id"));
                        intent.putExtra("ucode", channelMap.get("ucode"));
                        context.startActivity(intent);
                    }
                });
            }else if ("L".equals(type)){
                ((ChannelViewHolder)holder).iv_channel_state.setImageResource(R.mipmap.type_state_huifang);
                ((ChannelViewHolder)holder).iv_channel_bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, VoderActivity.class);
                        intent.putExtra("lu_id", channelMap.get("id"));
                        intent.putExtra(TCConstants.COVER_PIC, channelMap.get("user_head"));
                        context.startActivity(intent);
                    }
                });
            }
        }

        //标题
        String title = TCUtils.getLimitString(channelMap.get("room_title"), 6);
        ((ChannelViewHolder)holder).tv_channel_title.setText(title);
        //标签
        if (channelMap.get("tag_id").equals("1")){
            ((ChannelViewHolder)holder).tv_channel_tag.setText(channelMap.get("tag"));
            ((ChannelViewHolder)holder).tv_channel_tag.setBackgroundResource(R.drawable.shape_tag_life);
        }else if (channelMap.get("tag_id").equals("2")){
            ((ChannelViewHolder)holder).tv_channel_tag.setText(channelMap.get("tag"));
            ((ChannelViewHolder)holder).tv_channel_tag.setBackgroundResource(R.drawable.shape_tag_sing);
        }else if (channelMap.get("tag_id").equals("3")){
            ((ChannelViewHolder)holder).tv_channel_tag.setText(channelMap.get("tag"));
            ((ChannelViewHolder)holder).tv_channel_tag.setBackgroundResource(R.drawable.shape_tag_quyi);
        }else if (channelMap.get("tag_id").equals("4")){
            ((ChannelViewHolder)holder).tv_channel_tag.setText(channelMap.get("tag"));
            ((ChannelViewHolder)holder).tv_channel_tag.setBackgroundResource(R.drawable.shape_tag_dance);
        }else if (channelMap.get("tag_id").equals("5")){
            ((ChannelViewHolder)holder).tv_channel_tag.setText(channelMap.get("tag"));
            ((ChannelViewHolder)holder).tv_channel_tag.setBackgroundResource(R.drawable.shape_tag_painting_bg);
        }else if (channelMap.get("tag_id").equals("6")){
            ((ChannelViewHolder)holder).tv_channel_tag.setText(channelMap.get("tag"));
            ((ChannelViewHolder)holder).tv_channel_tag.setBackgroundResource(R.drawable.shape_tag_fitness_bg);
        }else if (channelMap.get("tag_id").equals("7")){
            ((ChannelViewHolder)holder).tv_channel_tag.setText(channelMap.get("tag"));
            ((ChannelViewHolder)holder).tv_channel_tag.setBackgroundResource(R.drawable.shape_tag_money_bg);
        }else if (channelMap.get("tag_id").equals("8")){
            ((ChannelViewHolder)holder).tv_channel_tag.setText(channelMap.get("tag"));
            ((ChannelViewHolder)holder).tv_channel_tag.setBackgroundResource(R.drawable.shape_tag_talk_bg);
        }else {
            ((ChannelViewHolder)holder).tv_channel_tag.setText(channelMap.get("tag"));
            ((ChannelViewHolder)holder).tv_channel_tag.setBackgroundResource(R.drawable.shape_tag_talk_bg);
        }
        //地址
        if (!TextUtils.isEmpty(channelMap.get("addr"))){
            ((ChannelViewHolder)holder).tv_channel_address.setText(channelMap.get("addr"));
        }else {
            ((ChannelViewHolder)holder).tv_channel_address.setText("火星");
        }

        //观看人数
        if ("Z".equals(type)){
            ((ChannelViewHolder)holder).tv_channel_look.setText(channelMap.get("online_num") + "在看");
        }else{
            ((ChannelViewHolder)holder).tv_channel_look.setText(channelMap.get("online_num") + "看过");
        }

    }

    @Override
    public int getItemCount() {
        return mChannelList != null? mChannelList.size() : 0;
    }

    private class ChannelViewHolder extends RecyclerView.ViewHolder {

//        @Bind(R.id.iv_channel_bg)
//        ImageView iv_channel_bg;
//
//        @Bind(R.id.iv_channel_state)
//        ImageView iv_channel_state;
//
//        @Bind(R.id.tv_channel_title)
//        TextView tv_channel_title;
//
//        @Bind(R.id.tv_channel_tag)
//        TextView tv_channel_tag;
//
//        @Bind(R.id.tv_channel_address)
//        TextView tv_channel_address;
//
//        @Bind(R.id.tv_channel_look)
//        TextView tv_channel_look;

        RelativeLayout rl_channel_item;
        ImageView iv_channel_bg;
        ImageView iv_channel_state;
        TextView tv_channel_title;
        TextView tv_channel_tag;
        TextView tv_channel_address;
        TextView tv_channel_look;

        public ChannelViewHolder(View v) {
            super(v);
//            ButterKnife.bind(this, v);
            rl_channel_item = (RelativeLayout) v.findViewById(R.id.rl_channel_item);
            iv_channel_bg = (ImageView) v.findViewById(R.id.iv_channel_bg);
            iv_channel_state = (ImageView) v.findViewById(R.id.iv_channel_state);
            tv_channel_title = (TextView) v.findViewById(R.id.tv_channel_title);
            tv_channel_tag = (TextView) v.findViewById(R.id.tv_channel_tag);
            tv_channel_address = (TextView) v.findViewById(R.id.tv_channel_address);
            tv_channel_look = (TextView) v.findViewById(R.id.tv_channel_look);
        }

    }
}
