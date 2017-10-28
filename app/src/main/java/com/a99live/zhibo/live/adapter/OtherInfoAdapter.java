package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.base.BaseListAdapter;
import com.a99live.zhibo.live.utils.UIUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fuyk on 2016/10/19.
 */
public class OtherInfoAdapter extends BaseListAdapter {

    public OtherInfoAdapter(Context c, List<Map<String,String>> data) {
        super(c, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_other_home, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.id.tag_holder, holder);
        } else {
            convertView.setPadding(0, 0, 0, 0);
            holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
        }
        final Map<String,String> item = getItem(position);

        //封面
        ViewGroup.LayoutParams linearParams = holder.iv_cover.getLayoutParams();
        linearParams.height = (UIUtils.getScreenWidth())/2;
        linearParams.width = (UIUtils.getScreenWidth())/2;
        holder.iv_cover.setLayoutParams(linearParams);
        Glide.with(mContext)
                .load(item.get("bg_img_url"))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(R.mipmap.list_cover)
                .crossFade()
                .into(holder.iv_cover);

        //状态
        if (!"0".equals(item.get("is_live"))){
            holder.iv_type.setImageResource(R.mipmap.type_state_live);
        }else {
            holder.iv_type.setImageResource(R.mipmap.type_state_huifang);
        }

        //时间
        holder.tv_time_live.setText(item.get("format_time"));

        //名称
        if (item.get("nickname").length() >= 6) {
            String name = item.get("nickname").substring(0,5);
            holder.tv_name.setText(name + "...");
        }else {
            holder.tv_name.setText(item.get("nickname"));
        }

        //观看人数
        holder.tv_look_number.setText(item.get("online_num") + "人");

        //观看状态
        if (!"0".equals(item.get("is_live"))){
            holder.tv_look.setText("在看");
        }else {
            holder.tv_look.setText("看过");
        }

        return convertView;
    }

    static class ViewHolder {

        @Bind(R.id.iv_cover)
        ImageView iv_cover;

        @Bind(R.id.iv_type)
        ImageView iv_type;

        @Bind(R.id.tv_name)
        TextView tv_name;

        @Bind(R.id.tv_time_live)
        TextView tv_time_live;

        @Bind(R.id.tv_look)
        TextView tv_look;

        @Bind(R.id.tv_look_number)
        TextView tv_look_number;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
