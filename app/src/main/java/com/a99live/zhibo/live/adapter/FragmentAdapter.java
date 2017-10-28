package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.OtherHomeActivity;
import com.a99live.zhibo.live.adapter.base.BaseListAdapter;
import com.a99live.zhibo.live.glide.GlideCircleTransform;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.YMClick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页列表adapter
 * Created by fuyk on 2016/9/10.
 */
public class FragmentAdapter extends BaseListAdapter {

    private int o_minid = Integer.MAX_VALUE;
    private int h_minid = Integer.MAX_VALUE;
    private String head_img = "";

    public FragmentAdapter(Context c, List<Map<String,String>> data,String head) {
        super(c, data);
        this.head_img = head;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_live_show, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.id.tag_holder, holder);
        } else {
            convertView.setPadding(0, 0, 0, 0);
            holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
        }
        final Map<String,String> item = getItem(position);

        //取最小id
        if ("L".equalsIgnoreCase(item.get("type"))) {
            if (item.containsKey("id")) {
                String id_s = item.get("id");
                if (id_s != null && !"".equals(id_s) && !"null".equals(id_s)) {
                    int id = Integer.parseInt(id_s);
                    h_minid = h_minid > id ? id : h_minid;
                }
            }
        } else {
            int room_id = Integer.parseInt(item.get("room_id"));
            o_minid = o_minid > room_id ? room_id : o_minid;
        }

        //头像
        Glide.with(mContext)
                .load(item.get("user_head"))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(R.mipmap.head)
                .crossFade()
                .transform(new GlideCircleTransform(mContext))
                .into(holder.ivAvatar);

        holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,OtherHomeActivity.class);
                intent.putExtra(TCConstants.UCODE, item.get("ucode"));
                mContext.startActivity(intent);
                YMClick.onEventValue(mContext,YMClick.PAGE_HEAD_IMG,"type",head_img,1);
            }
        });

        String guan = "0";
        if (item.containsKey("guan")){
            guan = item.get("guan");
        }

        String approve = "0";
        if (item.containsKey("verify")){
            approve = item.get("verify");
        }

        if ("1".equals(guan)){
            //官方认证
            holder.iv_approve.setVisibility(View.VISIBLE);
            holder.iv_approve.setBackgroundResource(R.drawable.official);
        }else{
            if ("1".equals(approve)){
                //认证
                holder.iv_approve.setVisibility(View.VISIBLE);
                holder.iv_approve.setBackgroundResource(R.drawable.approve);
            }else{
                holder.iv_approve.setVisibility(View.GONE);
            }
        }

//        //认证
//        if (item.containsKey("verify")){
//
//            if ("1".equals(item.get("verify"))){
//                //已认证
//                holder.iv_approve.setVisibility(View.VISIBLE);
//            }else if ("0".equals(item.get("verify"))){
//                //未认证
//                holder.iv_approve.setVisibility(View.GONE);
//            }
//        }else{
//            holder.iv_approve.setVisibility(View.GONE);
//        }

        //标题
        holder.tv_name.setText(item.get("nick_name"));
        //地址
        if (!TextUtils.isEmpty(item.get("addr"))){
            holder.tv_address.setText(item.get("addr"));
        }else {
            holder.tv_address.setText("火星");
        }
        //标签
        holder.tv_tag.setVisibility(View.VISIBLE);
        String tag_id = item.get("tag_id");
        String tag = item.get("tag");
        if ("1".equals(tag_id)){
            holder.tv_tag.setBackgroundResource(R.drawable.shape_tag_life);
        }else if ("2".equals(tag_id)){
            holder.tv_tag.setBackgroundResource(R.drawable.shape_tag_sing);
        }else if ("3".equals(tag_id)){
            holder.tv_tag.setBackgroundResource(R.drawable.shape_tag_quyi);
        }else if ("4".equals(tag_id)){
            holder.tv_tag.setBackgroundResource(R.drawable.shape_tag_dance);
        }else if ("5".equals(tag_id)){
            holder.tv_tag.setBackgroundResource(R.drawable.shape_tag_painting_bg);
        }else if ("6".equals(tag_id)){
            holder.tv_tag.setBackgroundResource(R.drawable.shape_tag_fitness_bg);
        }else if ("7".equals(tag_id)){
            holder.tv_tag.setBackgroundResource(R.drawable.shape_tag_money_bg);
        }else if ("8".equals(tag_id)){
            holder.tv_tag.setBackgroundResource(R.drawable.shape_tag_talk_bg);
        }else {
            holder.tv_tag.setBackgroundResource(R.drawable.shape_tag_talk_bg);
        }
        if (!TextUtils.isEmpty(tag)){
            holder.tv_tag.setText(item.get("tag"));
        }else{
            holder.tv_tag.setVisibility(View.GONE);
        }
//        holder.tv_tag.setText(item.get("tag"));

        //观看人数
        holder.tv_look_num.setText(item.get("online_num"));
        //观看描述
        if (("Z").equals(item.get("type"))){
            holder.tv_look.setText("在看");
        }else if (("L").equals(item.get("type"))){
            holder.tv_look.setText("看过");
        }

        //封面
        Glide.with(mContext)
                .load(item.get("bg_img_url"))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(R.mipmap.list_cover)
                .crossFade()
                .into(holder.iv_cover);
        //类型 直播或录播
        if (item.containsKey("type")) {
            if (item.get("type").equals("Z")) {
                holder.iv_live_state.setImageResource(R.mipmap.type_state_live);
            } else if (item.get("type").equals("L")) {
                holder.iv_live_state.setImageResource(R.mipmap.type_state_huifang);
            }
        }
        //话题
        holder.tv_topic.setText(item.get("room_title"));

        return convertView;
    }


    public int getO_minid() {
        return o_minid;
    }

    public int getH_minid() {
        return h_minid;
    }

    public void initId() {
        h_minid = Integer.MAX_VALUE;
        o_minid = Integer.MAX_VALUE;
    }

    static class ViewHolder {
        @Bind(R.id.ivAvatar)
        ImageView ivAvatar;

        @Bind(R.id.iv_approve)
        ImageView iv_approve;

        @Bind(R.id.tv_name)
        TextView tv_name;

        @Bind(R.id.tv_address)
        TextView tv_address;

        @Bind(R.id.tv_tag)
        TextView tv_tag;

        @Bind(R.id.tv_look_num)
        TextView tv_look_num;

        @Bind(R.id.tv_look)
        TextView tv_look;

        @Bind(R.id.iv_cover)
        ImageView iv_cover;

        @Bind(R.id.iv_live_state)
        ImageView iv_live_state;

        @Bind(R.id.tv_topic)
        TextView tv_topic;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

}
