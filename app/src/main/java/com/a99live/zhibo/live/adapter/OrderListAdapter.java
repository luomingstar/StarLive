package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.OrderDetailActivity;
import com.a99live.zhibo.live.activity.OrderListActivity;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Map;


/**
 * Created by JJGCW on 2017/2/23.
 */

public class OrderListAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private List<Map<String,String>> mData;
    private OrderListActivity.OrderItemClick mClick;

    public OrderListAdapter(Context context, List<Map<String,String>>data, OrderListActivity.OrderItemClick click){
        this.mContext = context;
        this.mData = data;
        this.mClick = click;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_list_item, parent, false);
        OrderViewHold viewHold = new OrderViewHold(view);
        return viewHold;
    }

    /**
     * {
     "id":"8",
     "image":"http://99live-10063116.image.myqcloud.com/2a315830d1ca644551bfe6ccce0f0bfa?imageMogr2/crop/247x/thumbnail/x1334",
     "title":"cesjhi",
     "live_time":"2017-02-24 14:10:55",
     "count":"0",
     "intro":"fdsgfdasgfdsgfds",
     "nickname":"O(∩_∩)O哈哈~",
     "avatar":"http://99live-10063116.image.myqcloud.com/2bf2a4ffb1d433d2e9c22e7de87b1cb5?imageMogr2/crop/x375/thumbnail/x200",
     "status":1
     },
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Map<String, String> dataMap = mData.get(position);
        final String id = dataMap.get("id");
        String image = dataMap.get("image");
        String title = dataMap.get("title");
        final String status = dataMap.get("status");
        String live_time = dataMap.get("live_time");
        String count = dataMap.get("count");
        String intro = dataMap.get("intro");
        String nickname = dataMap.get("nickname");
        String avatar = dataMap.get("avatar");
        Glide.with(mContext).load(image).diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().into(((OrderViewHold)holder).img_bg);
        Glide.with(mContext).load(avatar).diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().into(((OrderViewHold)holder).avatar);
        ((OrderViewHold)holder).title.setText(title);
        ((OrderViewHold)holder).name.setText(nickname);
        ((OrderViewHold)holder).order_num.setText("/ "+ count + "已预约 /");
        ((OrderViewHold)holder).status.setText(live_time);
        if ("1".equals(status)){
            ((OrderViewHold)holder).button.setSelected(false);
            ((OrderViewHold)holder).button.setText("已预约");
        }else{
            ((OrderViewHold)holder).button.setSelected(true);
            ((OrderViewHold)holder).button.setText("预 约");
        }
        ((OrderViewHold)holder).button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                OrderDetailActivity.goOfderDetrailActivity(mContext,id);
//                UIUtils.showToast("sdfsdf");
                if ("1".equals(status)){
                    UIUtils.showToast("您已预约");
                }else{
                    if (mClick != null){
                        mClick.orderItem(id,position);
                    }
                }
            }
        });
        ((OrderViewHold)holder).item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderDetailActivity.goOfderDetrailActivity(mContext,id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    private class OrderViewHold extends RecyclerView.ViewHolder{
        ImageView img_bg;
        TextView title;
        NewCircleImageView avatar;
        TextView name;
        TextView status;
        TextView order_num;
        TextView button;
        View item;

        public OrderViewHold(View itemView) {
            super(itemView);
            item = itemView;
            img_bg = (ImageView) itemView.findViewById(R.id.img_bg);
            title = (TextView) itemView.findViewById(R.id.title);
            avatar = (NewCircleImageView) itemView.findViewById(R.id.avatar);
            name = (TextView) itemView.findViewById(R.id.name);
            status = (TextView) itemView.findViewById(R.id.status);
            order_num = (TextView) itemView.findViewById(R.id.order_num);
            button = (TextView) itemView.findViewById(R.id.button);
        }
    }
}
