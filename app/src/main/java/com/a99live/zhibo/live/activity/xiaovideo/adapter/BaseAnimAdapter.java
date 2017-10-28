package com.a99live.zhibo.live.activity.xiaovideo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by JJGCW on 2017/4/12.
 */
public abstract class BaseAnimAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private int lastPosition = -1;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        setAnimation(holder.itemView, position);

    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
//            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_slide_bottom_up);
//            viewToAnimate.startAnimation(animation);
//            lastPosition = position;
        }
    }
}