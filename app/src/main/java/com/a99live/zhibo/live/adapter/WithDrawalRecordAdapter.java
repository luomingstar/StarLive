package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.base.BaseListAdapter;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JJGCW on 2016/12/13.
 * withdraw_id
 * user_id
 * account_id
 * amount 提现金额
 * trans_no
 * status 0待审核 1 付款成功 2付款失败 3拒绝付款
 * pay_time 支付时间
 * create_time 创建时间
 * reward 留言
 */

public class WithDrawalRecordAdapter extends BaseListAdapter {


    public WithDrawalRecordAdapter(Context c, List<Map<String, String>> data) {
        super(c, data);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            view = View.inflate(mContext, R.layout.withdrawalrecordadapter_item,null);
            holder = new ViewHolder(view);
            view.setTag(R.id.tag_holder, holder);
        }else{
            holder = (WithDrawalRecordAdapter.ViewHolder) view.getTag(R.id.tag_holder);
        }
        Map<String, String> item = getItem(i);
        String amount = item.get("amount");
        String status = item.get("status");
        String pay_time = item.get("pay_time");
        String create_time = item.get("create_time");
        String remark = item.get("remark");

        holder.money.setText(amount+"元");
        holder.data.setText(create_time);
//        if (!TextUtils.isEmpty(remark)){
            holder.message.setText("留言: "+remark);
//        }else{
//            holder.message.setVisibility(View.GONE);
//        }
        holder.money.setTextColor(Color.parseColor("#999999"));
        if ("0".equals(status)){
            holder.successed.setImageResource(R.drawable.pay_wait);
        }else if ("1".equals(status)){
            holder.successed.setImageResource(R.drawable.pay_success);
            holder.money.setTextColor(Color.parseColor("#FF5A5B"));
        }else if ("2".equals(status)){
            holder.successed.setImageResource(R.drawable.pay_failed);
        }else if ("3".equals(status)){
            holder.successed.setImageResource(R.drawable.pay_refuse);
        }else {
            holder.successed.setImageResource(R.drawable.pay_failed);
        }



        return view;
    }

    static class ViewHolder {

        @Bind(R.id.money)
        TextView money;

        @Bind(R.id.data)
        TextView data;

        @Bind(R.id.message)
        TextView message;

        @Bind(R.id.success)
        ImageView successed;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
