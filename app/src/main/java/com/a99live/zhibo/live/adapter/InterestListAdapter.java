package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.content.res.Resources;
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

public class InterestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<Map<String,String>> mInterestList;

    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onSelect(ArrayList<String> interestId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public InterestListAdapter(Context context, List<Map<String,String>> data) {
        this.context = context;
        this.mInterestList = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_interest, parent, false);
        final InterestViewHolder tagViewHolder = new InterestViewHolder(view);
        return tagViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Map<String, String> tagMap = mInterestList.get(position);
        ((InterestViewHolder)holder).interest.setText(tagMap.get("name"));
        ((InterestViewHolder)holder).interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources tv_interest = ((InterestViewHolder) holder).interest.getResources();
                String isSelect = tagMap.get("isSelect");
                if ("0".equals(isSelect)){
                    ((InterestViewHolder)holder).interest.setBackgroundResource(R.drawable.shape_interest_select);
                    ((InterestViewHolder)holder).interest.setTextColor(tv_interest.getColor(R.color.text_5a5b));
                    mInterestList.get(position).put("isSelect", "1");
                }else {
                    ((InterestViewHolder)holder).interest.setBackgroundResource(R.drawable.shape_item_interest_normal);
                    ((InterestViewHolder)holder).interest.setTextColor(tv_interest.getColor(R.color.text_444));
                    mInterestList.get(position).put("isSelect", "0");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mInterestList != null? mInterestList.size() : 0;
    }

    private class InterestViewHolder extends RecyclerView.ViewHolder{
        TextView interest;
        public InterestViewHolder(View itemView) {
            super(itemView);
            interest = (TextView) itemView.findViewById(R.id.tv_item_interest);
        }
    }

}
