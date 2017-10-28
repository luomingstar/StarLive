package com.a99live.zhibo.live.fragment.ListLive;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.LivePlayerActivity;
import com.a99live.zhibo.live.activity.VoderActivity;
import com.a99live.zhibo.live.adapter.FragmentAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.RoomProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.LoginManager;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.utils.YMClick;
import com.a99live.zhibo.live.view.PageStateLayout;
import com.a99live.zhibo.live.view.list.XListView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 首页关注界面
 * Created by fuyk on 2016/9/10.
 */
public class FocusFragment extends BaseFragment implements XListView.IXListViewListener, AdapterView.OnItemClickListener {

    private RoomProtocol roomProtocol;
    private FragmentAdapter fragmentAdapter;

    @Bind(R.id.list_view)
    XListView focusListView;

    private int mOminid = 0;
    private int mHminid = 0;

    @Override
    protected View initContentView() {
        View view = View.inflate(getActivity(), R.layout.fragment_focus, null);
        ButterKnife.bind(this, view);

        focusListView.setPullRefreshEnable(true);
        focusListView.setXListViewListener(this);

        focusListView.setPullLoadEnable(true);
        focusListView.setOnItemClickListener(this);
        roomProtocol = new RoomProtocol();
        isPrepared = true;
        initContentData();
        return view;
    }

    @Override
    protected void initContentData() {
//        roomProtocol = new RoomProtocol();
        if (!isPrepared || !isVisible){
            return;
        }
        onRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FocusFragment");
    }


//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser){
//            onRefresh();
//        }else{
//
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FocusFragment");
    }

    /**
     * 获取列表数据
     */
    private void getFocusList() {
        LiveRequestParams params = new LiveRequestParams();
        params.put("o_minid", mOminid);
        params.put("h_minid", mHminid);

        roomProtocol.getFocusList(params)
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
                                initPage(data);
                                return;
                            }else if ("20000006".equals(code)) {
                                Toast.makeText(LiveZhiBoApplication.getApp(), "房间未找到，请刷新列表", Toast.LENGTH_SHORT).show();
                            } else if ("111111110".equals(code)  || "11111113".equals(code) ) {
                                LoginManager.clearUser();
                                LoginManager.goLoginActivity(getActivity());
                            } else if ("11111122".equals(code)) {
                                UIUtils.showToast("在别处登录，请重新登录");
                                LoginManager.clearUser();
                                LoginManager.goLoginActivity(getActivity());
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                            focusListView.onRefreshFinish();
                        }else{
                            UIUtils.showToast(s);
                            focusListView.onRefreshFinish();
                        }

                        if (fragmentAdapter != null && fragmentAdapter.getData() != null && fragmentAdapter.getData().size() >0){
                            mOminid = 0;
                            mHminid = 0;
                        }else {
                            showPage(PageStateLayout.STATE_ERROR);
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (fragmentAdapter != null && fragmentAdapter.getData() != null && fragmentAdapter.getData().size() >0){
                            UIUtils.showToast(R.string.net_error);
                            focusListView.onRefreshFinish();
                        }else {
                            showPage(PageStateLayout.STATE_ERROR);
                        }
                        mOminid = 0;
                        mHminid = 0;
                    }
                });

    }

    /**
     * 初始化界面
     *
     * @param data
     */
    private void initPage(String data) {
        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(data);
        if (listMapByJson.size()>0){//有数据


            showPage(PageStateLayout.STATE_SUCCEED);
            if (mHminid == 0 && mOminid == 0 && fragmentAdapter != null) {
                //下拉刷新
                fragmentAdapter.getData().clear();
                focusListView.onRefreshFinish();
                focusListView.updateRefreshTime();
            }

            //加載更多
            if (fragmentAdapter == null) {
                fragmentAdapter = new FragmentAdapter(getActivity(), listMapByJson, YMClick.FOCUSPAGE_HEAD_IMG);
                focusListView.setAdapter(fragmentAdapter);
            } else {
                fragmentAdapter.getData().addAll(listMapByJson);
                fragmentAdapter.notifyDataSetChanged();
            }

        }else{//无数据
            if (mHminid == 0 && mOminid == 0 )
                showPage(PageStateLayout.STATE_EMPTY);

            focusListView.setAutoLoadEnable(false);
            focusListView.setPullLoadEnable(false);
        }
        focusListView.onRefreshFinish();


    }

    /**
     * 更新界面
     */
    @Override
    public void onRefresh() {
        mOminid = 0;
        mHminid = 0;
        if (fragmentAdapter != null) {
            fragmentAdapter.initId();
        }
        getFocusList();

    }

    @Override
    public void onLoadMore() {
        if (mHminid == fragmentAdapter.getH_minid() && mOminid == fragmentAdapter.getO_minid()) {
            return;
        }
        mHminid = fragmentAdapter.getH_minid();
        mOminid = fragmentAdapter.getO_minid();
        getFocusList();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Map<String,String> item = fragmentAdapter.getItem(position);
        if (item.get("type").equals("Z")) {
            Intent intent = new Intent(getActivity(), LivePlayerActivity.class);
            intent.putExtra("room_id", item.get("room_id"));
            intent.putExtra("ucode", item.get("ucode"));
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
        } else if (item.get("type").equals("L")) {
            enterPlayRoom(item.get("id"), item.get("user_head"));
        }
    }



    /**
     * 进入录播接口
     */
    private void enterPlayRoom(String lu_id, final String avator) {

        goPlayRoom(lu_id,avator);

    }

    /**
     * 进入录播房间
     *
     * @param lu_id
     */
    private void goPlayRoom(String lu_id , String avator) {
        Intent intent = new Intent(getActivity(), VoderActivity.class);
        intent.putExtra("lu_id", lu_id);
        intent.putExtra(TCConstants.COVER_PIC, avator);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
    }
}
