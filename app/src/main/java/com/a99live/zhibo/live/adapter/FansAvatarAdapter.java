package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.OtherHomeActivity;
import com.a99live.zhibo.live.utils.TCUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by fuyk on 2016/11/28.
 */

public class FansAvatarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    private List<Map<String,String>> mFansAvtarList;

    public FansAvatarAdapter(Context context, List<Map<String,String>> data) {
        this.mContext = context;
        this.mFansAvtarList = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_user_avatar, parent, false);
        FansAvatarViewHolder fansAvatarViewHolder = new FansAvatarViewHolder(view);

        return fansAvatarViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Map<String, String> avatarMap = mFansAvtarList.get(position);
        avatarMap.get("avatar");
        TCUtils.showPicWithUrl(mContext, ((FansAvatarAdapter.FansAvatarViewHolder)holder).ivAvatar, avatarMap.get("avatar"),
                R.mipmap.logo_head);

        ((FansAvatarViewHolder) holder).ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OtherHomeActivity.goOtherHomeActivity(mContext, avatarMap.get("ucode"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFansAvtarList.size();
    }

    private class FansAvatarViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;

        public FansAvatarViewHolder(View itemView) {
            super(itemView);

            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        }
    }
}
