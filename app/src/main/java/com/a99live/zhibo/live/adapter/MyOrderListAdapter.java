package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.LivePlayerActivity;
import com.a99live.zhibo.live.activity.OrderDetailActivity;
import com.a99live.zhibo.live.activity.VoderActivity;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Map;

/**
 * Created by JJGCW on 2017/2/23.
 */

public class MyOrderListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Map<String,String>> mData;

    public MyOrderListAdapter(Context context,List<Map<String,String>>data){
        this.mContext = context;
        this.mData = data;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_list_item, parent, false);
        MyOrderListAdapter.OrderViewHold viewHold = new MyOrderListAdapter.OrderViewHold(view);
        return viewHold;
    }

    /**
     *{
     "room_reserve_id":"8",
     "create_time":"2017-02-17 17:03:09",
     "anchor_id":"2",
     "count":"0",
     "title":"cesjhi",
     "image":"http://99live-10063116.image.myqcloud.com/2a315830d1ca644551bfe6ccce0f0bfa?imageMogr2/crop/247x/thumbnail/x1334",
     "live_time":"2017-02-24 14:10:55",
     "nickname":"O(∩_∩)O哈哈~",
     "avatar":"http://99live-10063116.image.myqcloud.com/2bf2a4ffb1d433d2e9c22e7de87b1cb5?imageMogr2/crop/x375/thumbnail/x200",
     "live_status":1,
     "room_id":"",
     "ucode":""
     },
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Map<String, String> dataMap = mData.get(position);
        final String id = dataMap.get("room_reserve_id");
        String image = dataMap.get("image");
        String title = dataMap.get("title");
        final String live_status = dataMap.get("live_status");
        String live_time = dataMap.get("live_time");
        String count = dataMap.get("count");
        String intro = dataMap.get("intro");
        String nickname = dataMap.get("nickname");
        final String avatar = dataMap.get("avatar");
        Glide.with(mContext).load(image).diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().into(((OrderViewHold)holder).img_bg);
        Glide.with(mContext).load(avatar).diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().into(((OrderViewHold)holder).avatar);
        ((OrderViewHold)holder).title.setText(title);
        ((OrderViewHold)holder).name.setText(nickname);
        ((OrderViewHold)holder).order_num.setText("/ "+ count + "已预约 /");
        ((OrderViewHold)holder).status.setText(live_time);
        // live_status 是1 已结束  0  正在直播  2 未开始
        if ("1".equals(live_status)){
            ((OrderViewHold)holder).button.setSelected(true);
            ((OrderViewHold)holder).button.setText("已结束");
        }else if ("0".equals(live_status)){
            ((OrderViewHold)holder).button.setSelected(true);
            ((OrderViewHold)holder).button.setText("正在直播");
        }else{
            ((OrderViewHold)holder).button.setSelected(false);
            ((OrderViewHold)holder).button.setText("未开始");
        }
        final String room_id = dataMap.get("room_id");
        final String ucode = dataMap.get("ucode");
        ((OrderViewHold)holder).button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("0".equals(live_status)){
                    if (!TextUtils.isEmpty(room_id) && !TextUtils.isEmpty(ucode)){
                        LivePlayerActivity.goLivePlayerActivity(mContext,room_id,ucode);
                    }
                }else if ("1".equals(live_status)){
                    if (!TextUtils.isEmpty(room_id)){
                        VoderActivity.goVoderActivity(mContext,room_id,avatar);
                    }else{
                        UIUtils.showToast("直播已结束，稍后将提供回放");
                    }
                }else{
                    OrderDetailActivity.goOfderDetrailActivity(mContext,id);
                }
            }
        });
        ((OrderViewHold)holder).item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("0".equals(live_status)){
                    if (!TextUtils.isEmpty(room_id) && !TextUtils.isEmpty(ucode)){
                        LivePlayerActivity.goLivePlayerActivity(mContext,room_id,ucode);
                    }
                }else if ("1".equals(live_status)){
                    if (!TextUtils.isEmpty(room_id)){
                        VoderActivity.goVoderActivity(mContext,room_id,avatar);
                    }else{
                        UIUtils.showToast("直播已结束，稍后将提供回放");
                    }
                }else{
                    OrderDetailActivity.goOfderDetrailActivity(mContext,id);
                }
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
