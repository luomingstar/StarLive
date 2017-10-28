package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by fuyk on 2016/11/25.
 */

public class TagListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<Map<String,String>> mTagList;

    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onClick(String tagMessage, String tagId, String content);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TagListAdapter(Context context, List<Map<String,String>> data) {
        this.context = context;
        this.mTagList = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tag, parent, false);
        final TagViewHolder tagViewHolder = new TagViewHolder(view);
        return tagViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Map<String, String> tagMap = mTagList.get(position);
        ((TagViewHolder)holder).tag.setText(tagMap.get("name"));
        String content = tagMap.get("content");
        final ArrayList<Map<String, String>> contentMap = JsonUtil.getListMapByJson(content);
        ((TagViewHolder)holder).tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    if (contentMap.size() > 0) {
                        int m = contentMap.size();
                        int random = new Random().nextInt(m);
                        listener.onClick(tagMap.get("name"), tagMap.get("id"), contentMap.get(random).get(""));
                    }else {
                        listener.onClick(tagMap.get("name"), tagMap.get("id"), "");
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTagList != null? mTagList.size() : 0;
    }

    private class TagViewHolder extends RecyclerView.ViewHolder{
        TextView tag;
        public TagViewHolder(View itemView) {
            super(itemView);
            tag = (TextView) itemView.findViewById(R.id.tv_item_tag);
        }
    }

}
