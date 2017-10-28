package com.a99live.zhibo.live.activity.xiaovideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.xiaovideo.XiaoVideoPlayerActivity;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

/**
 * Created by JJGCW on 2017/4/12.
 */

public class XVideoAdapter extends FooterAdapter {

    private Context mContext;
    private List<Map<String,String>> mData;
    private boolean userFooter = false;
    public XVideoAdapter(Context context, List<Map<String,String>> data){
        this.mContext = context;
        this.mData = data;
    }

    public void setUserFooter(boolean canUserFooter){
        userFooter = canUserFooter;
        notifyDataSetChanged();
    }



    @Override
    public boolean useFooter() {
        return userFooter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.xiaoadapter_item,parent,false);
        ViewHolderNomal holder = new ViewHolderNomal(view);
        return holder;
    }

    @Override
    public void onBindContentItemView(RecyclerView.ViewHolder holder, int position) {
        final Map<String, String> map = mData.get(position);
        String nick_name = map.get("nick_name");
        String total_like = map.get("total_like");
        String user_head = map.get("user_head");
        String pg_img_url = map.get("pg_img_url");
        ((ViewHolderNomal)holder).name.setText(nick_name);
        ((ViewHolderNomal)holder).likeNum.setText(total_like);
        Glide.with(mContext)
                .load(user_head)
                .dontAnimate()
                .into(((ViewHolderNomal)holder).head);
        Glide.with(mContext)
                .load(pg_img_url)
                .centerCrop()
                .crossFade()
                .into(((ViewHolderNomal)holder).img);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XiaoVideoPlayerActivity.goXiaoVideoPlayerActivity(mContext,map);
            }
        });
    }

    @Override
    public int getContentItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getContentItemType(int position) {
        return 0;
    }


    public class ViewHolderNomal extends RecyclerView.ViewHolder{

        private final ImageView img;
        private final TextView name;
        private final TextView likeNum;
        private final NewCircleImageView head;

        public ViewHolderNomal(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            name = (TextView) itemView.findViewById(R.id.name);
            likeNum = (TextView) itemView.findViewById(R.id.likeNum);
            head = (NewCircleImageView) itemView.findViewById(R.id.head);
        }
    }
}
