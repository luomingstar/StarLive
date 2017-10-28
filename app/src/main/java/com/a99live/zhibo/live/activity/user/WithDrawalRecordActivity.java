package com.a99live.zhibo.live.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.adapter.WithDrawalRecordAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.PageStateLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class WithDrawalRecordActivity extends BaseActivity {

    private PageStateLayout mPageStateLayout;
    private UserProtocol userProtocol;
    private LinearLayout linearLayout;
    private WithDrawalRecordAdapter adapter;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_att_empty)
    TextView tv_friend_empty;

    @Bind(R.id.record_list)
    ListView record_list;

    private List<Map<String, String>> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        initView();
        initData();
    }

    public static void goWithDrawlRecordActivity(Context context){
        Intent intent = new Intent(context,WithDrawalRecordActivity.class);
        context.startActivity(intent);
    }

    private void initView() {
        mPageStateLayout = new PageStateLayout(this);
        View contentView = View.inflate(this , R.layout.activity_with_drawal_record , null);
        mPageStateLayout.setContentView(contentView);
        View emptyView = View.inflate(this, R.layout.layout_attention_empty, null);
        mPageStateLayout.setEmptyView(emptyView);
        LinearLayout ll_all = (LinearLayout) findViewById(R.id.ll_all);
        ll_all.addView(mPageStateLayout);
        ButterKnife.bind(this);
        mPageStateLayout.addCallBack(new PageStateLayout.PageStateCallBack() {
            @Override
            public void onErrorClick() {
                onRefresh();
            }

            @Override
            public void onEmptyClick() {
                onRefresh();
            }
        });
        tv_title.setText("提现记录");
        tv_friend_empty.setText("暂无提现记录");
        //广告轮播图
        linearLayout = new LinearLayout(this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.height = UIUtils.getScreenHeight()/6 + UIUtils.getDimen(R.dimen.dp_10);
        linearLayout.setLayoutParams(lp);
        linearLayout.setVisibility(View.GONE);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        record_list.addHeaderView(linearLayout);

    }

    protected void showPage(int pageState) {
        mPageStateLayout.setPageState(pageState);
    }

    private void initData() {
        userProtocol = new UserProtocol();
        data = new ArrayList<>();
        adapter = new WithDrawalRecordAdapter(this, data);
        record_list.setAdapter(adapter);
        onRefresh();
    }

    private void onRefresh(){

        LiveRequestParams params = new LiveRequestParams();
        userProtocol.getWithDrawslist(params)
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

                                initPage(dataList);
                                return;

                            }else{
                                UIUtils.showToast(map.get("msg"));
//                                myFansList.onRefreshFinish();
                            }

                        }else{
                            UIUtils.showToast(s);
//                            myFansList.onRefreshFinish();
                        }
                        if (data.size()>0){

                        }else{
                            showPage(PageStateLayout.STATE_EMPTY);
                        }

//                        if (friendAdapter != null &&friendAdapter.getData()!= null && friendAdapter.getData().size()>0){
//                            mPage--;
//                        }else{
//                            showPage(PageStateLayout.STATE_ERROR);
//                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                        if (data.size()>0){
                            UIUtils.showToast("网络异常，请重试或反馈给我们");
                        }else{
                            showPage(PageStateLayout.STATE_ERROR);
                        }
//                        if (friendAdapter != null &&friendAdapter.getData()!= null && friendAdapter.getData().size()>0){
//                            UIUtils.showToast("网络异常，请重试或反馈给我们");
//                        }else{
//                            showPage(PageStateLayout.STATE_ERROR);
//                        }
//                        mPage--;
//                        myFansList.onRefreshFinish();

                    }
                });

    }

    private void initPage(ArrayList<Map<String, String>> dataList) {
//        mNextPage = mPage + 1;
        if (dataList.size()>0) {//有数据
//            Map<String, String> map = dataList.get(0);
//            if ("0".equals(map.get("code"))){
//                String datas = map.get("data");
//                ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(datas);
//                if (listMapByJson.size()>0){
                    showPage(PageStateLayout.STATE_SUCCEED);
                    data.addAll(dataList);
                    adapter.notifyDataSetChanged();
//                }else{
//                    if (data.size()==0)
//                        showPage(PageStateLayout.STATE_ERROR);
//                }
//            }else{
//                if (data.size()==0)
//                    showPage(PageStateLayout.STATE_EMPTY);
//            }
//            initHead();

//            data.addAll(dataList);
//            adapter.notifyDataSetChanged();
        }else{//无数据
            if (data.size()==0){
                showPage(PageStateLayout.STATE_EMPTY);
            }else{

            }
        }

    }

    private void initHead(){
        View view = View.inflate(this,R.layout.with_drawal_record_head,null);
        TextView moneyNum = (TextView) view.findViewById(R.id.money_num);
        linearLayout.addView(view);
        linearLayout.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.layout_back})
    void onClick(View view){
        switch (view.getId()){
            case R.id.layout_back:
                onBackPressed();
                break;
        }
    }
}
