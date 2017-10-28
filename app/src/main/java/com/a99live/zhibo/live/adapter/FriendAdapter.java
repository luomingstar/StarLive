package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.base.BaseListAdapter;
import com.a99live.zhibo.live.glide.GlideCircleTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 关注和粉丝adapter
 * Created by fuyk on 2016/9/10.
 */
public class FriendAdapter extends BaseListAdapter {

    public FriendAdapter(Context c, List<Map<String ,String>> data) {
        super(c, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.item_my_attentin_fans, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.id.tag_holder, holder);
        }else {
            convertView.setPadding(0, 0, 0, 0);
            holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
        }

        Map<String,String> item = getItem(position);

        //头像
        Glide.with(mContext)
        .load(item.get("avatar"))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(R.color.bg_page)
                .crossFade()
                .transform(new GlideCircleTransform(mContext))
                .into(holder.iv_avatar);
        //名字
        holder.tv_name.setText(item.get("nickname"));
        //地址
        holder.tv_address.setText(item.get("region"));
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_Avatar)
        ImageView iv_avatar;

        @Bind(R.id.tv_name)
        TextView tv_name;

        @Bind(R.id.tv_address)
        TextView tv_address;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
