package com.a99live.zhibo.live.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.activity.OtherHomeActivity;
import com.a99live.zhibo.live.adapter.FriendAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.PageStateLayout;
import com.a99live.zhibo.live.view.list.XListView;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 粉丝界面
 * Created by fuyk on 2016/9/10.
 */
public class FansActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener {

    private UserProtocol userProtocol;
    private PageStateLayout mPageStateLayout;

    private int mPage = 1;
    private int mNextPage = mPage;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_att_empty)
    TextView tv_friend_empty;

    @Bind(R.id.list_view)
    XListView myFansList;

    private FriendAdapter friendAdapter;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        initView();
        initData();
    }

    private void initView() {
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

        tv_title.setText(R.string.myfans);
        tv_friend_empty.setText(R.string.no_fanc);

        myFansList.setPullRefreshEnable(true);
        myFansList.setXListViewListener(this);

        myFansList.setPullLoadEnable(true);
        myFansList.setAutoLoadEnable(true);
        myFansList.setOnItemClickListener(this);
    }

    private void initData() {
        userProtocol = new UserProtocol();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            uid =   bundle.getString("uid");
            tv_title.setText(R.string.other_fans);
        }else{
            uid = "";
            tv_title.setText(R.string.myfans);
        }
        onRefresh();
    }

    /**
     * 获取粉丝列表
     */
    private void getFansList(int page){
        LiveRequestParams params = new LiveRequestParams();
        params.put("page", page);

        userProtocol.MyFansList(params)
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
                        if (friendAdapter != null &&friendAdapter.getData()!= null && friendAdapter.getData().size()>0){
                            mPage--;
                        }else{
                            showPage(PageStateLayout.STATE_ERROR);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (friendAdapter != null &&friendAdapter.getData()!= null && friendAdapter.getData().size()>0){
                            UIUtils.showToast("网络异常，请重试或反馈给我们");
                        }else{
                            showPage(PageStateLayout.STATE_ERROR);
                        }
                        mPage--;
                        myFansList.onRefreshFinish();

                    }
                });
    }

    /**
     * 获取粉丝列表
     */
    private void getOtherFansList(int page, String uid){
        LiveRequestParams params = new LiveRequestParams();
        params.put("page", page);
        params.put("ucode", uid);

        userProtocol.MyFansList(params)
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
                        if (friendAdapter != null && friendAdapter.getData() != null && friendAdapter.getData().size()>0){
                            mPage--;
                        }else{
                            showPage(PageStateLayout.STATE_ERROR);
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (friendAdapter != null && friendAdapter.getData() != null && friendAdapter.getData().size()>0){
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

            if (friendAdapter != null && mPage==1) {
                //下拉刷新
                friendAdapter.getData().clear();
                myFansList.onRefreshFinish();
                myFansList.updateRefreshTime();
            }

            //加载更多
            if (friendAdapter == null) {
                friendAdapter = new FriendAdapter(this, dataList);
                myFansList.setAdapter(friendAdapter);
            } else {
                friendAdapter.getData().addAll(dataList);
                friendAdapter.notifyDataSetChanged();
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

    @Override
    public void onRefresh() {
        mPage = mNextPage = 1;
        if (TextUtils.isEmpty(uid)){
            getFansList(mPage);
        }else {
            getOtherFansList(mPage, uid);
        }
    }

    @Override
    public void onLoadMore() {
        if (mNextPage == mPage) {
            return;
        }
        mPage = mNextPage;
        if (TextUtils.isEmpty(uid)){
            getFansList(mPage);
        }else {
            getOtherFansList(mPage, uid);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Map<String,String> item = friendAdapter.getItem(position);
        Intent intent = new Intent(this, OtherHomeActivity.class);
        intent.putExtra(TCConstants.UCODE, item.get("ucode"));
        startActivity(intent);
    }

    @OnClick(R.id.layout_back)
    void onClick() {
        back();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        overridePendingTransition(R.anim.scale_big_in, R.anim.tran_right_out);
        finish();
    }

}
