package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;

/**
 * 分享adapter
 * Created by fuyk on 2016/9/9.
 */
public class ShareAdapter extends BaseAdapter {

    private static String[] shareNames = new String[] {"微信好友","朋友圈","新浪微博","QQ好友","QQ空间"};
    private int[] shareIcons = new int[] {R.mipmap.share_weichat, R.mipmap.share_weichat_qzone, R.mipmap.share_weibo, R.mipmap.share_qq, R.mipmap.share_qq_qzone};

    private LayoutInflater inflater;

    public ShareAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return shareNames.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = inflater.inflate(R.layout.item_share, null);
        }

        ImageView shareIcon = (ImageView) convertView.findViewById(R.id.iv_share_icon);
        TextView shareTitle = (TextView) convertView.findViewById(R.id.tv_share_title);
        shareIcon.setImageResource(shareIcons[position]);
        shareTitle.setText(shareNames[position]);

        return null;
    }
}
