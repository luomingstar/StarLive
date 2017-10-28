package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Map;

/**
 * Created by fuyk on 2016/11/25.
 */

public class RecommendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<Map<String,String>> mRecommendList;

    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onClick(String tagMessage, String tagId, String content);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RecommendListAdapter(Context context, List<Map<String,String>> data) {
        this.context = context;
        this.mRecommendList = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommend, parent, false);
        final RecommendViewHolder tagViewHolder = new RecommendViewHolder(view);
        return tagViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Map<String, String> recommendMap = mRecommendList.get(position);
        Glide.with(context)
                .load(recommendMap.get("avatar_full_path"))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(R.mipmap.head)
                .dontAnimate()
                .into(((RecommendViewHolder)holder).recommend_head);

//        Glide.with(context)
//                .load(R.color.mengban_red)
//                .centerCrop()
//                .dontAnimate()
//                .transform(new GlideCircleTransform(context))
//                .into(((RecommendViewHolder)holder).mengban_head);
        final String isSelect = recommendMap.get("is_selected");
        if ("0".equals(isSelect)){
            ((RecommendViewHolder) holder).mengban_head.setVisibility(View.GONE);
//            Resources tv_recom = ((RecommendViewHolder) holder).tv_recommend.getResources();
            ((RecommendViewHolder) holder).tv_recommend.setTextColor(Color.parseColor("#666666"));
        }else{
            ((RecommendViewHolder) holder).mengban_head.setVisibility(View.VISIBLE);
//            Resources tv_recom = ((RecommendViewHolder) holder).tv_recommend.getResources();
            ((RecommendViewHolder) holder).tv_recommend.setTextColor(Color.parseColor("#FF5A5B"));
        }

        ((RecommendViewHolder)holder).tv_recommend.setText(recommendMap.get("nickname"));
        ((RecommendViewHolder)holder).layout_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ("0".equals(isSelect)) {
                    mRecommendList.get(position).put("is_selected", "1");
                }else {
                    mRecommendList.get(position).put("is_selected", "0");
                }

                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecommendList != null? mRecommendList.size() : 0;
    }

    private class RecommendViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout layout_recommend;
        NewCircleImageView recommend_head;
        ImageView mengban_head;
        TextView tv_recommend;

        public RecommendViewHolder(View itemView) {
            super(itemView);
            layout_recommend = (RelativeLayout) itemView.findViewById(R.id.rl_layout_recommend);
            recommend_head = (NewCircleImageView) itemView.findViewById(R.id.iv_recommend_head);
            mengban_head = (ImageView) itemView.findViewById(R.id.iv_recommend_mengban);
            tv_recommend = (TextView) itemView.findViewById(R.id.tv_item_recommend);
        }
    }

}
