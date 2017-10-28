package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.PaymentActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fuyk on 2016/10/12.
 */
public class ProductInfoAdapter extends SimpleAdapter {

    private ArrayList<Map<String ,String>> mData;
    private Context mContext;


    public ProductInfoAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.mContext = context;
        this.mData = (ArrayList<Map<String, String>>) data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        final Map<String, String> map = mData.get(position);
        TextView tv_amount = (TextView) view.findViewById(R.id.tv_amount);
        TextView tv_Presented = (TextView) view.findViewById(R.id.tv_Presented);
        TextView tv_new_user = (TextView) view.findViewById(R.id.tv_new_user);
        TextView tv_price = (TextView) view.findViewById(R.id.tv_price);
        RelativeLayout layout_account = (RelativeLayout) view.findViewById(R.id.layout_account);

        /**钻石数*/
        tv_amount.setText(map.get("v_amount"));

        /**赠送钻石数*/
        if (!map.get("g_number").equals("0")) {
            tv_Presented.setVisibility(View.VISIBLE);
            if (map.get("g_type").equals("1")){
                tv_Presented.setText("赠送" + map.get("g_number") + "钻");
            }else if (map.get("g_type").equals("2")){
                tv_Presented.setText("赠送" + map.get("g_number") + "币");
            }
        }

        /**新人专享*/
        if (map.get("type").equals("2")){
            tv_new_user.setVisibility(View.VISIBLE);
        }else{
            tv_new_user.setVisibility(View.INVISIBLE);
        }

//        /**人民币*/
////        tv_price.setText(map.get(""));
//        tv_price.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(mContext,PaymentActivity.class);
//                intent.putExtra("money", map.get("money"));
//                intent.putExtra("amount", map.get("v_amount"));
//                intent.putExtra("product", map.get("id"));
//                mContext.startActivity(intent);
//
//            }
//        });

        /**item点击事件*/
        layout_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,PaymentActivity.class);
                intent.putExtra("money", map.get("money"));
                intent.putExtra("amount", map.get("v_amount"));
                intent.putExtra("product", map.get("id"));
                mContext.startActivity(intent);
            }
        });

        return view;
    }

    //    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = View.inflate(mContext, R.layout.item_product, null);
//            holder = new ViewHolder(convertView);
//            convertView.setTag(R.id.tag_holder, holder);
//        } else {
//            convertView.setPadding(0, 0, 0, 0);
//            holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
//        }
//        final ProductInfo.DataBean item = getItem(position);
//
//        /**钻石数*/
//        holder.tv_amount.setText(item.getV_amount());
//
//        /**赠送钻石数*/
//        if (item.getG_type() == "1") {
//            holder.tv_Presented.setVisibility(View.VISIBLE);
//            holder.tv_Presented.setText(item.getG_number());
//        }
//
//        /**人民币*/
//        holder.tv_price.setText(item.getMoney());
//        holder.tv_price.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(mContext,PaymentActivity.class);
//                intent.putExtra("money", item.getMoney());
//                intent.putExtra("amount", item.getV_amount());
//                intent.putExtra("product", item.getId());
//                mContext.startActivity(intent);
//
//            }
//        });
//
//        return convertView;
//    }
//
//    static class ViewHolder {
//
//        @Bind(R.id.tv_amount)
//        TextView tv_amount;
//
//        @Bind(R.id.tv_Presented)
//        TextView tv_Presented;
//
//        @Bind(R.id.tv_price)
//        TextView tv_price;
//
//        public ViewHolder(View v) {
//            ButterKnife.bind(this, v);
//        }
//    }

}
