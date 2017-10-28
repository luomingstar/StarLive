package com.a99live.zhibo.live.activity.xiaovideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

/**
 * Created by JJGCW on 2017/4/11.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {

    private Context mContext;
    private  List<Map<String,String>> mData;

    public VideoAdapter(Context context, List<Map<String,String>> data){
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public VideoAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.xiaoadapter_item,parent,false);
        VideoHolder holder = new VideoHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(VideoAdapter.VideoHolder holder, int position) {
        Map<String, String> map = mData.get(position);
        String nick_name = map.get("nick_name");
        String guan = map.get("guan");
        String user_head = map.get("user_head");
        String pg_img_url = map.get("pg_img_url");
        holder.name.setText(nick_name);
        holder.likeNum.setText(guan);
        Glide.with(mContext)
                .load(user_head)
                .dontAnimate()
                .into(holder.head);
        Glide.with(mContext)
                .load(pg_img_url)
                .centerCrop()
                .crossFade()
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return mData==null? 0 : mData.size();
    }

    class VideoHolder extends RecyclerView.ViewHolder{

        private final ImageView img;
        private final TextView name;
        private final TextView likeNum;
        private final NewCircleImageView head;

        public VideoHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            name = (TextView) itemView.findViewById(R.id.name);
            likeNum = (TextView) itemView.findViewById(R.id.likeNum);
            head = (NewCircleImageView) itemView.findViewById(R.id.head);
        }
    }
}
