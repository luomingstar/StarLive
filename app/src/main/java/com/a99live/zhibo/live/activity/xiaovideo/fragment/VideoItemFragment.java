package com.a99live.zhibo.live.activity.xiaovideo.fragment;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.xiaovideo.adapter.XVideoAdapter;
import com.a99live.zhibo.live.activity.xiaovideo.weight.RecyclerOnScrollListener;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.VideoProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.CircleProgressView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by JJGCW on 2017/4/12.
 */

public class VideoItemFragment extends BaseVideoFragment implements SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.recycler_channel)
    RecyclerView recycler_channel;

    @Bind(R.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.netErroy)
    RelativeLayout netErroy;

    @Bind(R.id.erroy_img)
    ImageView erroy_img;

    private VideoProtocol videoProtocol;
    private int TOP_NUM = 10;
    private int LIST_NUM = 10;
    private int MAXID = 0;
    private int MINID = Integer.MAX_VALUE;

    private String channel;
    private View view;

    private CircleProgressView progressView;
    private XVideoAdapter adapter;

    private List<Map<String, String>> topList;
    private List<Map<String, String>> nomalList;
    private List<Map<String, String>> allData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        channel = getArguments().getString("channelId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = View.inflate(getActivity(), R.layout.fragment_video_item, null);
            progressView = (CircleProgressView) view.findViewById(R.id.loadingImageView);
            progressView.setVisibility(View.VISIBLE);
            progressView.spin();
            ButterKnife.bind(this, view);
            netErroy.setVisibility(View.GONE);
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

    @OnClick({R.id.netErroy})
    void onClick(View view){
        switch (view.getId()){
            case R.id.netErroy:
                setNetErroy(false,false);
                progressView.setVisibility(View.VISIBLE);
                progressView.spin();
                loadTop();
                break;
        }
    }

    private void setNetErroy(boolean isShow,boolean isNetErroy){
        if (isShow){
            netErroy.setVisibility(View.VISIBLE);
            if (isNetErroy){
                erroy_img.setImageResource(R.drawable.net_error);
            }else{
                erroy_img.setImageResource(R.mipmap.friend);
            }
        }else{
            netErroy.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressView != null && progressView.isSpinning()){
            progressView.stopSpinning();
        }
//        isPrepared = false;
        isVisible = false;//由于预加载的问题，导致第一个会加载一遍数据
//        MAXID = 0;
//        MINID = Integer.MAX_VALUE;
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
        topList = new ArrayList<>();
        nomalList = new ArrayList<>();
        allData = new ArrayList<>();
        adapter = new XVideoAdapter(getActivity(),allData);
//        adapter = new ChannelAdapter(getActivity(), allData);
        recycler_channel.setAdapter(adapter);
        videoProtocol = new VideoProtocol();
        recycler_channel.addOnScrollListener(new RecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                loadList(false);
            }
        });
        loadTop();
    }

    private boolean isLoadMore;
    //刷新 最大id  加载更多 最小id
    private void loadList(final boolean isRefresh){
        if (isLoadMore){
            return;
        }
        if (!isRefresh){
            isLoadMore = true;
        }

        if (isRefresh && adapter != null){
            adapter.setCanLoadMore(true);
        }

        if (!adapter.isCanLoadMore()){
            isLoadMore = false;
            return;
        }

        LiveRequestParams params = new LiveRequestParams();
        int minId = 0;
        int maxId = 0;
        if (isRefresh){
            maxId = MAXID;
            minId = 0;
        }else{
            maxId = 0;
            minId = MINID;
        }
        params.put("o_maxid",maxId);
        params.put("o_minid",minId);
        params.put("o_number",LIST_NUM + "");
        params.put("tag_id",channel);
        videoProtocol.getVideoList(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        parseListData(s,isRefresh);
                        if (progressView != null ){
                            progressView.setVisibility(View.GONE);
                        }
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        isLoadMore = false;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (progressView != null ){
                            progressView.setVisibility(View.GONE);
                        }
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        isLoadMore = false;
                        if (allData.size()==0){
                            if (topList.size()!=0){
                                allData.addAll(topList);
                                adapter.notifyDataSetChanged();
                            }else {
                                setNetErroy(true,true);
                            }
                        }else{
//                            allData.clear();
//                            allData.addAll(topList);
//                            adapter.notifyDataSetChanged();
//                            adapter.setCanLoadMore(true);
//                            adapter.setUserFooter(false);
                        }
                        adapter.setCanLoadMore(true);
                        adapter.setUserFooter(false);
                    }
                });
    }

    private void parseListData(String s,boolean isRefresh){
        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
        if (listMapByJson.size()>0){
            Map<String, String> map = listMapByJson.get(0);
            if ("0".equals(map.get("code"))){
                String data = map.get("data");
                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                if (dataList.size()>0){
                    for (int i=0;i<dataList.size();i++){
                        Map<String, String> map1 = dataList.get(i);
                        String video_id = map1.get("video_id");
                        int maxMinId = Integer.parseInt(video_id);
                        if (MAXID < maxMinId){
                            MAXID = maxMinId;
                        }
                        if (MINID > maxMinId){
                            MINID = maxMinId;
                        }
                    }
                    if (isRefresh){
//                        nomalList.clear();
                        allData.clear();
                        nomalList.addAll(0,dataList);
                        allData.addAll(topList);
                        allData.addAll(nomalList);
                    }else{
//                        allData.clear();
                        nomalList.addAll(dataList);
//                        allData.addAll(topList);
                        allData.addAll(dataList);
                    }
                    adapter.notifyDataSetChanged();
                    adapter.setCanLoadMore(true);
                    adapter.setUserFooter(true);
                    if (!isRefresh && dataList.size()<LIST_NUM){
                        adapter.setCanLoadMore(false);
                        adapter.setUserFooter(false);
                    }
                }else{
                    if (allData.size()==0){
                        if (topList.size()!=0){
                            allData.addAll(topList);
                            adapter.notifyDataSetChanged();
                        }else {
                            setNetErroy(true, false);
                        }
                    }
                    if (isRefresh) {
                        adapter.setCanLoadMore(true);
                        adapter.setUserFooter(false);
                    }else{
                        adapter.setCanLoadMore(false);
                        adapter.setUserFooter(false);
                    }
                }
            }else{
                if (allData.size()==0){
                    if (topList.size()==0){
                        allData.addAll(topList);
                        adapter.notifyDataSetChanged();
                    }else {
                        setNetErroy(true, false);
                    }
                }
                if (isRefresh) {
                    adapter.setCanLoadMore(true);
                    adapter.setUserFooter(false);
                }else{
                    adapter.setCanLoadMore(false);
                    adapter.setUserFooter(false);
                }
            }
        }else{
            if (allData.size()==0){
                if (topList.size()!=0){
                    allData.addAll(topList);
                    adapter.notifyDataSetChanged();
                }else {
                    setNetErroy(true, false);
                }
            }
            if (isRefresh) {
                adapter.setCanLoadMore(true);
                adapter.setUserFooter(false);
            }else{
                adapter.setCanLoadMore(false);
                adapter.setUserFooter(false);
            }
        }
    }

    private void loadTop() {
        LiveRequestParams params = new LiveRequestParams();
        params.put("o_maxid","0");
        params.put("o_minid","0");
        params.put("tag_id",channel);
        params.put("o_number",TOP_NUM + "");
        videoProtocol.getVideoTop(params)
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
//                        if (progressView != null ){
//                            progressView.setVisibility(View.GONE);
//                        }
//                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()){
//                            swipeRefreshLayout.setRefreshing(false);
//                        }
                        loadList(true);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
//                        UIUtils.showToast(R.string.net_error);
                        loadList(true);
//                        if (progressView != null ){
//                            progressView.setVisibility(View.GONE);
//                        }
//                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()){
//                            swipeRefreshLayout.setRefreshing(false);
//                        }
                    }
                });
    }

    private void initView(ArrayList<Map<String, String>> dataList) {
//        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
//        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
//        recycler_channel.setLayoutManager(layoutManager);
//        recycler_channel.setAdapter(new ChannelAdapter(getActivity(), dataList));
        topList.clear();
        topList.addAll(dataList);
    }


    @Override
    public void onRefresh() {
        loadList(true);
    }
}
