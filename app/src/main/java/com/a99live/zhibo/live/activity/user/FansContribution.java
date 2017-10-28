package com.a99live.zhibo.live.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.activity.OtherHomeActivity;
import com.a99live.zhibo.live.adapter.FansContributionAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.RankProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.PageStateLayout;
import com.a99live.zhibo.live.view.list.XListView;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.a99live.zhibo.live.R.id.tv_title;

/**
 * Created by JJGCW on 2016/11/15.
 */

public class FansContribution extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener{

    @Bind(tv_title)
    TextView mTvTitle;

    @Bind(R.id.tv_att_empty)
    TextView tv_friend_empty;

    @Bind(R.id.list_view)
    XListView myFansList;

    private int mPage = 1;
    private int mNextPage = mPage;
    private PageStateLayout mPageStateLayout;
    private RankProtocol rankProtocol;
    private boolean isComeByUser;
    private String ucodeOrRoomId;
    private FansContributionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_fanscontribution);
        initView();
        initData();
    }

    public static void goFansContributionByUser(Context context ,String ucode){
        Intent intent = new Intent(context,FansContribution.class);
        intent.putExtra("ucode",ucode);
        context.startActivity(intent);
    }

    public static void goFansContributionByRoom(Context context,String roomId){
        Intent intent = new Intent(context,FansContribution.class);
        intent.putExtra("room_id",roomId);
        context.startActivity(intent);
    }

    private void initData() {
        rankProtocol = new RankProtocol();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (bundle.containsKey("ucode")){
                isComeByUser = true;
                ucodeOrRoomId = bundle.getString("ucode");
            }else if (bundle.containsKey("room_id")){
                isComeByUser = false;
                ucodeOrRoomId = bundle.getString("room_id");
            }else{
                UIUtils.showToast(R.string.net_error);
                onBackPressed();
            }
        }
        onRefresh();
    }

    private void initView(){

        mPageStateLayout = new PageStateLayout(this);
        View contentView = View.inflate(this , R.layout.layout_attention_content , null);
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

        mTvTitle.setText("粉丝贡献");
        tv_friend_empty.setText("暂无粉丝贡献");
//        TextView textView = new TextView(this);
//        textView.setText("送礼榜");
//        textView.setTextColor(ContextCompat.getColor(this,R.color.text_222));
//        textView.setTextSize(16);
//        myFansList.addHeaderView(textView);
//        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        textView.setLayoutParams(layoutParams);
//        textView.setPadding(UIUtils.getDimen(R.dimen.dp_10),UIUtils.getDimen(R.dimen.dp_20),UIUtils.getDimen(R.dimen.dp_10),UIUtils.getDimen(R.dimen.dp_20));
        myFansList.setPullRefreshEnable(true);
        myFansList.setXListViewListener(this);

        myFansList.setPullLoadEnable(true);
        myFansList.setAutoLoadEnable(true);
        myFansList.setOnItemClickListener(this);
    }

    @OnClick({R.id.layout_back})
    void onClick(View view){
        switch (view.getId()){
            case R.id.layout_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Map<String,String> item = adapter.getItem(position);
        OtherHomeActivity.goOtherHomeActivity(this,item.get("ucode"));
    }

    @Override
    public void onRefresh() {
        mPage = mNextPage = 1;
        getFansContribution(mPage,isComeByUser);
    }

    @Override
    public void onLoadMore() {
        if (mNextPage == mPage) {
            return;
        }
        mPage = mNextPage;
        getFansContribution(mPage,isComeByUser);
    }

    private void getFansContribution(int page, boolean isComeByUser){
        LiveRequestParams params = new LiveRequestParams();
        params.put("page", page);
        if (!isComeByUser){
            params.put("room_id",ucodeOrRoomId);
        }else{
            params.put("ucode",ucodeOrRoomId);
        }
        Observable<String> fansContribution = isComeByUser ? rankProtocol.getFansContributionInUser(params) : rankProtocol.getFansContributionInRoom(params);
        fansContribution.observeOn(AndroidSchedulers.mainThread()).compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                showPage(PageStateLayout.STATE_SUCCEED);
                                initPage(dataList);
                                return;
                            }else{
                                UIUtils.showToast(map.get("msg"));
                                myFansList.onRefreshFinish();
                            }
                        }else{
                            UIUtils.showToast(s);
                            myFansList.onRefreshFinish();
                        }
                        if (adapter != null && adapter.getData() != null && adapter.getData().size()>0){
                            mPage--;
                        }else{
                            showPage(PageStateLayout.STATE_ERROR);
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (adapter != null &&adapter.getData()!= null && adapter.getData().size()>0){
                            UIUtils.showToast("网络异常，请重试或反馈给我们");
                        }else{
                            showPage(PageStateLayout.STATE_ERROR);
                        }
                        mPage--;
                        myFansList.onRefreshFinish();
                    }
                });
    }

    private void initPage(ArrayList<Map<String, String>> dataList) {
        mNextPage = mPage + 1;
        if (dataList.size()>0){//有数据

            if (adapter != null && mPage==1) {
                //下拉刷新
                adapter.getData().clear();
                myFansList.onRefreshFinish();
                myFansList.updateRefreshTime();
            }

            //加载更多
            if (adapter == null) {
                adapter = new FansContributionAdapter(this, dataList);
                myFansList.setAdapter(adapter);
            } else {
                adapter.getData().addAll(dataList);
                adapter.notifyDataSetChanged();
            }
        }else{//无数据
            if (mPage == 1)
                showPage(PageStateLayout.STATE_EMPTY);
            myFansList.setAutoLoadEnable(false);
            myFansList.setPullLoadEnable(false);
        }
        myFansList.onRefreshFinish();

    }

    protected void showPage(int pageState) {
        mPageStateLayout.setPageState(pageState);
    }
}
