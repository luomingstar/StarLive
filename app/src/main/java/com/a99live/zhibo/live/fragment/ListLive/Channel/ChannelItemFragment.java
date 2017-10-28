package com.a99live.zhibo.live.fragment.ListLive.Channel;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.ChannelAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.ListsProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.CircleProgressView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/12/21.
 */

public class ChannelItemFragment extends BaseChannelFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.recycler_channel)
    RecyclerView recycler_channel;

    @Bind(R.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private ListsProtocol listsProtocol;


    private String channel;
    private View view;

    private CircleProgressView progressView;
    private ChannelAdapter adapter;
    private List<Map<String, String>> mDataList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        channel = getArguments().getString("channelId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = View.inflate(getActivity(), R.layout.fragment_type_item, null);
            progressView = (CircleProgressView) view.findViewById(R.id.loadingImageView);
            progressView.setVisibility(View.VISIBLE);
            progressView.spin();
            ButterKnife.bind(this, view);
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE);
            isPrepared = true;
            preLoad();
        }else{
            //避免引起挂载多个Parent
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view);
            }
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressView != null && progressView.isSpinning()){
            progressView.stopSpinning();
        }
//        isPrepared = false;
//        isVisible = false;//由于预加载的问题，导致第一个会加载一遍数据
        if (view != null) {
            //避免引起挂载多个Parent
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view);
            }
        }
        view = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void preLoad() {
        if (!isPrepared || !isVisible){
            return;
        }
        isPrepared = false;
//        initProgress();
        //加载数据
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recycler_channel.setLayoutManager(layoutManager);
        mDataList = new ArrayList<>();
        adapter = new ChannelAdapter(getActivity(), mDataList);
        recycler_channel.setAdapter(adapter);
        listsProtocol = new ListsProtocol();

        load();
    }

    private void load() {
        LiveRequestParams params = new LiveRequestParams();
        params.put("type", channel);
        listsProtocol.getChannelList(params)
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
                                if (dataList.size()>0){
                                    initView(dataList);
                                }
                            }else {
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{
                            UIUtils.showToast(s);
                        }
                        if (progressView != null ){
                            progressView.setVisibility(View.GONE);
                        }
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
                        if (progressView != null ){
                            progressView.setVisibility(View.GONE);
                        }
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    private void initView(ArrayList<Map<String, String>> dataList) {
//        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
//        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
//        recycler_channel.setLayoutManager(layoutManager);
//        recycler_channel.setAdapter(new ChannelAdapter(getActivity(), dataList));
        mDataList.clear();
        mDataList.addAll(dataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        load();
    }
}
