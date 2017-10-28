package com.a99live.zhibo.live.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.adapter.MyOrderListAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MyOrderListActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.iv_title)
    ImageView iv_title;

    @Bind(R.id.orderlist)
    RecyclerView orderlist;

    @Bind(R.id.nodata)
    TextView nodata;

    private ArrayList<Map<String,String>> mData;
    private MyOrderListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_list);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    public static void goMyOrderListActivity(Context context){
        Intent intent = new Intent(context,MyOrderListActivity.class);
        context.startActivity(intent);
    }

    private void initData() {
        LiveRequestParams params = new LiveRequestParams();
        UserProtocol protocol = new UserProtocol();
        protocol.getMyReserve(params)
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
        mData.addAll(dataList);
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        tv_title.setText("我的预约");
        tv_title.setVisibility(View.VISIBLE);
//        iv_title.setImageResource(R.drawable.order_my);
        iv_title.setVisibility(View.GONE);
        mData = new ArrayList<>();
        LinearLayoutManager linear = new LinearLayoutManager(this);
        linear.setOrientation(LinearLayoutManager.VERTICAL);
        orderlist.setLayoutManager(linear);
        adapter = new MyOrderListAdapter(this,mData);
        orderlist.setAdapter(adapter);
    }

    @OnClick({R.id.layout_back})
    void onClick(View view){
        switch (view.getId()){
            case R.id.layout_back:
                finish();
                break;
        }
    }
}
