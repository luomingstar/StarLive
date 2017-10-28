package com.a99live.zhibo.live.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.ShareAdapter;

/**
 * Created by fuyk on 2016/9/9.
 */
public class SharePopupWindow extends PopupWindow {

    private Context context;

    public SharePopupWindow(Context context) {
        this.context = context;
    }

    public void showShareWindow(){
        View view = LayoutInflater.from(context).inflate(R.layout.activity_share, null);
        GridView gridView = (GridView) view.findViewById(R.id.share_gridview);
        ShareAdapter adapter = new ShareAdapter(context);
        gridView.setAdapter(adapter);

        // 设置SelectPicPopupWindow的View
        this.setContentView(view);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        gridView.setOnItemClickListener(new ShareItemClickListener(this));
    }

    private class ShareItemClickListener implements AdapterView.OnItemClickListener {
        private PopupWindow pop;

        public ShareItemClickListener(PopupWindow pop) {
            this.pop = pop;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            share(position);
            pop.dismiss();
        }
    }

    /**
     * 分享
     * @param position
     */
    private void share(int position) {
        if (position == 4){
            qq();
        } else if (position == 5){
            qzone();
        } else {

        }
    }

    /**
     * 分享到QQ空间
     */
    private void qzone() {

    }

    /**
     * 分享到QQ
     */
    private void qq() {

    }

}
