package com.a99live.zhibo.live.activity.user;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.activity.LivePlayerActivity;
import com.a99live.zhibo.live.activity.VoderActivity;
import com.a99live.zhibo.live.adapter.MyLiveAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.RoomProtocol;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.LoginManager;
import com.a99live.zhibo.live.utils.SystemBarTintManager;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.PageStateLayout;
import com.a99live.zhibo.live.view.list.XListView;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 我的直播页
 * Created by fuyk on 2016/8/27.
 */
public class MyLiveActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener, MyLiveAdapter.DelMyLiveClickListener {
    private UserProtocol userProtocol;
    private RoomProtocol roomProtocol;
    private PageStateLayout mPageStateLayout;

    private int mPage = 1;
    private int mNextPage = mPage;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_att_empty)
    TextView tv_friend_empty;

    @Bind(R.id.list_view)
    XListView myLiveList;

    private MyLiveAdapter myLiveAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        initView();
        initData();
    }

    private void initData() {
        userProtocol = new UserProtocol();
        roomProtocol = new RoomProtocol();
        onRefresh();
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

        tv_title.setText(R.string.myLive);
        tv_friend_empty.setText(R.string.no_my_live);

        myLiveList.setPullRefreshEnable(true);
        myLiveList.setXListViewListener(this);

        myLiveList.setPullLoadEnable(true);
        myLiveList.setAutoLoadEnable(true);
        myLiveList.setOnItemClickListener(this);
    }

    /**
     * 获取我的直播列表
     */
    private void getMyLiveList(int page) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("page", page);

        userProtocol.MyLiveInfo(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            String code = map.get("code");
                            if ("0".equals(code)){
                                String data = map.get("data");
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                initPage(dataList);
                                return;
                            }else if ("20000006".equals(code)) {
                                Toast.makeText(LiveZhiBoApplication.getApp(), "房间未找到，请刷新列表", Toast.LENGTH_SHORT).show();
                            } else if ("111111110".equals(code)  || "11111113".equals(code) ) {
                                LoginManager.goLoginActivity(MyLiveActivity.this);
                            } else if ("11111122".equals(code)) {
                                UIUtils.showToast("在别处登录，请重新登录");
                                LoginManager.clearUser();
                                LoginManager.goLoginActivity(MyLiveActivity.this);
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                            myLiveList.onRefreshFinish();
                        }else{
                            UIUtils.showToast(s);
                            myLiveList.onRefreshFinish();
                        }

                        if (myLiveAdapter != null && myLiveAdapter.getData() != null && myLiveAdapter.getData().size() >0){
                            mPage--;
                        }else {
                            showPage(PageStateLayout.STATE_ERROR);
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (myLiveAdapter != null && myLiveAdapter.getData() != null && myLiveAdapter.getData().size()>0){
                            UIUtils.showToast("网络异常，请重试或反馈给我们");
                        }else{
                            showPage(PageStateLayout.STATE_ERROR);
                        }
                        mPage--;
                        myLiveList.onRefreshFinish();
                    }
                });
    }

    private void initPage(ArrayList<Map<String, String>> data) {

        mNextPage = mPage + 1;
        if (data.size() >  0){
            showPage(PageStateLayout.STATE_SUCCEED);
            if (myLiveAdapter != null && mPage==1) {
                //下拉刷新
                myLiveAdapter.getData().clear();
                myLiveList.onRefreshFinish();
                myLiveList.updateRefreshTime();
            }

            if (myLiveAdapter == null) {
                myLiveAdapter = new MyLiveAdapter(this, data);
                myLiveAdapter.setListener(this);
                myLiveList.setAdapter(myLiveAdapter);
            } else {
                myLiveAdapter.getData().addAll(data);
                myLiveAdapter.notifyDataSetChanged();
            }
        }else{
            if (mPage == 1)
                showPage(PageStateLayout.STATE_EMPTY);

            myLiveList.setAutoLoadEnable(false);
            myLiveList.setPullLoadEnable(false);
        }
        myLiveList.onRefreshFinish();
    }

    protected void showPage(int pageState) {
        mPageStateLayout.setPageState(pageState);
    }

    @Override
    public void onRefresh() {
        mPage = mNextPage = 1;
        getMyLiveList(mPage);
    }

    @Override
    public void onLoadMore() {
        if (mNextPage == mPage) {
            return;
        }
        mPage = mNextPage;
        getMyLiveList(mPage);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Map<String,String> item = myLiveAdapter.getItem(position);
        if ("0".equals(item.get("is_live"))) {
            enterPlayRoom(item.get("id"), item.get("bg_img_url"));
        }else if ("1".equals(item.get("is_live"))){
            enterLiveRoom(item.get("room_id"), item.get("bg_img_url"));
        }
    }

    /**
     * 进入录播接口
     */
    private void enterPlayRoom(String lu_id, final String avator) {

        goPlayRoom(lu_id, avator);

    }

    /**
     * 进入录播房间
     *
     * @param lu_id
     */
    private void goPlayRoom(String lu_id, String avator) {
        Intent intent = new Intent(this, VoderActivity.class);
        intent.putExtra("lu_id", lu_id);
        intent.putExtra(TCConstants.COVER_PIC, avator);
        startActivity(intent);
        this.overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
    }

    /**
     * 进入直播间接口
     */
    private void enterLiveRoom(String room_id, final String avator) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("roomId", room_id);

        roomProtocol.enterLive(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){

                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(map.get("data"));
                                if (dataList.size()>0){
                                    Map<String, String> datamap = dataList.get(0);
                                    goLiveRoom(datamap,avator);

                                }

                            }

                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast("服务器繁忙，请重试或反馈给我们");
                    }
                });
    }

    /**
     * 进入直播房间
     *
     * @param
     */
    private void goLiveRoom(Map<String,String> dataMap, String avator) {
        Intent intent = new Intent(this, LivePlayerActivity.class);
        intent.putExtra("room_id", dataMap.get("room_id"));
        intent.putExtra(TCConstants.COVER_PIC, avator);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
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

    @Override
    public void onDelMyLiveClick(String delId, final int position) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("itemID" ,delId);
        roomProtocol.removeMylive(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                Toast.makeText(MyLiveActivity.this, R.string.del_success, Toast.LENGTH_SHORT).show();
                                myLiveAdapter.getData().remove(position);
                                myLiveAdapter.notifyDataSetChanged();
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
                        Toast.makeText(MyLiveActivity.this, R.string.del_error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 支持沉浸式布局
     */
    private void initImmersionStyle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.text_5a5b);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }
}
