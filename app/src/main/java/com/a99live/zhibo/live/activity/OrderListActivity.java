package com.a99live.zhibo.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.user.MyOrderListActivity;
import com.a99live.zhibo.live.adapter.OrderListAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.ReserveProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.UIUtils;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class OrderListActivity extends BaseActivity{

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.iv_title)
    ImageView iv_title;

    @Bind(R.id.orderlist)
    RecyclerView orderlist;

    @Bind(R.id.nodata)
    TextView nodata;

    private ArrayList<Map<String,String>> mData;
    private OrderListAdapter adapter;


    public interface OrderItemClick{
        void orderItem(String id,int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        ButterKnife.bind(this);
        initView();

    }

    public static void goOrderListActivity(Context context){
        Intent intent = new Intent(context,OrderListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        LiveRequestParams params = new LiveRequestParams();
        ReserveProtocol protocol = new ReserveProtocol();
        protocol.getOrder(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                if(dataList.size()>0){
                                    nodata.setVisibility(View.GONE);
                                    orderList(dataList);
                                    return;
                                }
                            }else{

                            }
                        }else{

                        }
                        nodata.setVisibility(View.VISIBLE);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        nodata.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void orderList(ArrayList<Map<String, String>> dataList) {
        mData.clear();
        mData.addAll(dataList);
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        tv_title.setText("预告");
        tv_title.setVisibility(View.VISIBLE);
        iv_title.setImageResource(R.drawable.order_my);
        iv_title.setVisibility(View.VISIBLE);
        mData = new ArrayList<>();
        LinearLayoutManager linear = new LinearLayoutManager(this);
        linear.setOrientation(LinearLayoutManager.VERTICAL);
        orderlist.setLayoutManager(linear);
        adapter = new OrderListAdapter(this,mData,orderItemClick);
        orderlist.setAdapter(adapter);
    }

    private OrderItemClick orderItemClick = new OrderItemClick() {
        @Override
        public void orderItem(String id,int position) {
            setYuYue(id,position);
        }
    };

    @OnClick({R.id.layout_back,R.id.iv_title})
    void onClick(View view){
        switch (view.getId()){
            case R.id.layout_back:
                finish();
                break;
            case R.id.iv_title:
                MyOrderListActivity.goMyOrderListActivity(this);
                break;
        }
    }


    private void setYuYue(String reserve_id, final int position) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("ucode", SPUtils.getString(SPUtils.USER_CODE));
        params.put("reserve_id",reserve_id);
        ReserveProtocol protocol = new ReserveProtocol();
        protocol.setRecordUser(params)
                .observeOn(AndroidSchedulers.mainThread())
//                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                UIUtils.showToast("预约成功");
                                if (mData.size()>position){
                                    mData.get(position).put("status","1");
                                    adapter.notifyItemChanged(position);
                                }else{
                                    initData();
                                }
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{
                            UIUtils.showToast(s);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
                    }
                });
    }

}
