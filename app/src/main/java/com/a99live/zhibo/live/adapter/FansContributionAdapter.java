package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.base.BaseListAdapter;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JJGCW on 2016/11/16.
 */

public class FansContributionAdapter extends BaseListAdapter {

    public FansContributionAdapter(Context c, List<Map<String, String>> data) {
        super(c, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.item_fansxontribution_view, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.id.tag_holder, holder);
        }else {
//            convertView.setPadding(0, 0, 0, 0);
            holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
        }

        Map<String,String> item = getItem(position);

        //头像
        Glide.with(mContext)
                .load(item.get("avatar"))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(R.color.bg_page)
                .dontAnimate()
                .into(holder.fans_head);
        //名次
        String userNum = (position + 1)+"";
        holder.num.setTextColor(mContext.getResources().getColor(R.color.text_999));
        if ("1".equals(userNum)){
            holder.num.setTextColor(mContext.getResources().getColor(R.color.text_5a5b));
        }else if ("2".equals(userNum)){
            holder.num.setTextColor(mContext.getResources().getColor(R.color.text_ff9c00));
        }else if ("3".equals(userNum)){
            holder.num.setTextColor(mContext.getResources().getColor(R.color.text_42c700));
        }
        holder.num.setText(userNum);
        //名字
        holder.fansname.setText(item.get("nickname"));
        //99币
        holder.fansb.setText(item.get("coins")+ "币");

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.num)
        TextView num;

        @Bind(R.id.fans_head)
        NewCircleImageView fans_head;

        @Bind(R.id.fansname)
        TextView fansname;

        @Bind(R.id.fansb)
        TextView fansb;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
