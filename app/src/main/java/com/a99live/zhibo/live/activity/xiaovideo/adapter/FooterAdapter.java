package com.a99live.zhibo.live.activity.xiaovideo.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a99live.zhibo.live.R;

/**
 * Created by JJGCW on 2017/4/12.
 */
//用的时候要继承此抽象类，然后实现里面的几个抽象方法即可。
public abstract class FooterAdapter extends BaseAnimAdapter{

    private static final int TYPE_FOOTER = Integer.MIN_VALUE;
    private static final int TYPE_ADAPTEE_OFFSET = 2;
    private boolean canLoadMore = false;


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_FOOTER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return onCreateFooterViewHolder(parent, viewType);
        }
        return onCreateContentItemViewHolder(parent, viewType - TYPE_ADAPTEE_OFFSET);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);
        if (position == getContentItemCount() && holder.getItemViewType() == TYPE_FOOTER) {
            onBindFooterView(holder, position);
        } else {
            onBindContentItemView(holder, position);
        }
    }

    private static class ViewHolderFooter extends RecyclerView.ViewHolder {
        public ProgressBar pro;
        public TextView title;

        public ViewHolderFooter(View v) {
            super(v);
            pro = (ProgressBar) v.findViewById(R.id.pro);
            title = (TextView) v.findViewById(R.id.footerTitle);
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = getContentItemCount();
        if (useFooter()) {
            itemCount += 1;
        }
        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == getContentItemCount() && useFooter()) {
            return TYPE_FOOTER;
        }
        return getContentItemType(position) + TYPE_ADAPTEE_OFFSET;
    }

    public void setCanLoadMore(boolean canLoadMore){
        this.canLoadMore = canLoadMore;
    }

    public boolean isCanLoadMore() {
        return canLoadMore;
    }

    public abstract boolean useFooter();

    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.footerview, parent, false);
        ViewHolderFooter vh = new ViewHolderFooter(v);
        return vh;
    }

    public void onBindFooterView(RecyclerView.ViewHolder holder, int position){
        ViewHolderFooter footerHolder = (ViewHolderFooter) holder;
        if (isCanLoadMore()) {
            footerHolder.pro.setVisibility(View.VISIBLE);
            footerHolder.title.setText("加载更多");
        } else {
            footerHolder.pro.setVisibility(View.GONE);
            footerHolder.title.setText("已到底部");
        }
    }

    public abstract RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType);//创建你要的普通item

    public abstract void onBindContentItemView(RecyclerView.ViewHolder holder, int position);//绑定数据

    public abstract int getContentItemCount();

    public abstract int getContentItemType(int position);//没用到，返回0即可，为了扩展用的
}