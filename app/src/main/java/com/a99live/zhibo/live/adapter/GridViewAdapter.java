package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.a99live.zhibo.live.R;

import java.util.List;
import java.util.Map;

/**
 * 分享adapter
 * Created by fuyk on 2016/9/5.
 */
public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, Object>> listItem;

    public GridViewAdapter(Context context, List<Map<String, Object>> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
       if (convertView == null){
           convertView = LayoutInflater.from(context).inflate(R.layout.item_share,null);
       }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_share);
//        TextView textView = (TextView) convertView.findViewById(R.id.tv_share);

        Map<String, Object> map = listItem.get(position);
        imageView.setImageResource((Integer) map.get("image"));
//        textView.setText(map.get("title") + "");
        return convertView;
    }
}
