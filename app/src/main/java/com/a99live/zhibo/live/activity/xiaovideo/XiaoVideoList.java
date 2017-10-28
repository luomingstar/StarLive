package com.a99live.zhibo.live.activity.xiaovideo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.activity.xiaovideo.adapter.AdapterSimple;
import com.a99live.zhibo.live.activity.xiaovideo.adapter.XVideoAdapter;
import com.a99live.zhibo.live.activity.xiaovideo.weight.RecyclerOnScrollListener;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.VideoProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.a99live.zhibo.live.R.id.msg;


/**
 * Created by JJGCW on 2017/4/11.
 */

public class XiaoVideoList extends BaseActivity {
//    private HorizontalScrollView hsv_cur;
//    private LinearLayout ll_cur;
    private ArrayList<Map<String, String>> tagList;
    private int TOP_NUM = 10;
    private int LIST_NUM = 10;
    private int MAXID = 0;
    private int MINID = 0;
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.hsv_cur)
    HorizontalScrollView hsv_cur;
    @Bind(R.id.ll_cur)
    LinearLayout ll_cur;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    private VideoProtocol videoProtocol;
    private List<Map<String, String>> topList;
    private List<Map<String, String>> nomalList;
    private List<Map<String, String>> allData;
    private XVideoAdapter adapter;
    //    private ArrayList<Map<String, String>> names;

    public static void goXiaoVideoList(Context context){
        Intent intent = new Intent(context,XiaoVideoList.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaovideolist);
        ButterKnife.bind(this);
        initView();
        loadTagData();
    }

    private void initView(){
        tv_title.setText("小视频");
        videoProtocol = new VideoProtocol();
        tagList = new ArrayList<>();
        topList = new ArrayList<>();
        nomalList = new ArrayList<>();
        allData = new ArrayList<>();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        GridLayoutManager manager = new GridLayoutManager(this,2);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(manager);
        adapter = new XVideoAdapter(this,allData);
        recyclerview.setAdapter(adapter);
        recyclerview.addOnScrollListener(new RecyclerOnScrollListener(manager) {
            @Override
            public void onLoadMore() {

            }
        });
    }

    private void loadTagData(){
        LiveRequestParams params = new LiveRequestParams();
        videoProtocol.getVideoTag(params)
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
                                    tagList.addAll(dataList);

                                    loadListData("3");
                                }
                            }
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private void loadTopData(){
        LiveRequestParams params = new LiveRequestParams();
        params.put("o_maxid","0");
        params.put("o_minid","0");
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
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(msg);
                                if (dataList.size()>0){
                                    topList.addAll(dataList);
                                }
                            }
                        }

                        loadListData(tagList.get(0).get("id"));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        loadListData(tagList.get(0).get("id"));
                    }
                });
    }

    private void loadListData(String id){
        LiveRequestParams params = new LiveRequestParams();
        params.put("o_maxid","0");
        params.put("o_minid","0");
        params.put("o_number",LIST_NUM + "");
        params.put("tag_id",id);
        videoProtocol.getVideoList(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        parseListData(s);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private void parseListData(String s){
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
                    nomalList.addAll(dataList);

                    allData.addAll(topList);
                    allData.addAll(nomalList);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void setTop() {

        int win_width = UIUtils.getScreenWidth();
        String page = "0";
        hsv_cur = (HorizontalScrollView) findViewById(R.id.hsv_cur);


        ll_cur = (LinearLayout) findViewById(R.id.ll_cur);
        ll_cur.removeAllViews();
        AdapterSimple adapter = new AdapterSimple(hsv_cur, tagList,
                R.layout.xiaovideolist_item, new String[] { "name" },
                new int[] { R.id.hsv_cur_tv });
        SetDataView.ClickFunc[] expertClick = { new SetDataView.ClickFunc() {

            @Override
            public void click(int index, View v) {
                for (int i = 0; i < topList.size(); i++) {
                    if (i == index) {
                        ll_cur.getChildAt(i).setSelected(true);
//                        ll_cur.getChildAt(i)
//                                .findViewById(R.id.cur_tab_bg_select)
//                                .setVisibility(View.VISIBLE);
//						TextView tv = (TextView) ll_cur.getChildAt(i).findViewById(R.id.hsv_cur_tv);
//						tv.setTextColor(getResources().getColor(color.app_common_titlebar));
                    } else{
                        ll_cur.getChildAt(i).setSelected(false);
//                        ll_cur.getChildAt(i).findViewById(R.id.cur_tab_bg_select)
//                                .setVisibility(View.GONE);
//					TextView tv = (TextView) ll_cur.getChildAt(i).findViewById(R.id.hsv_cur_tv);
//					tv.setTextColor(getResources().getColor(color.app_common_titlebar));
                    }
                }
//                viewpager.setCurrentItem(index);

//                ll_cur.getChildAt(index).findViewById(R.id.cur_tab_bg_select)
//                        .setVisibility(View.VISIBLE);
            }
        } };
        SetDataView.horizontalView(hsv_cur, adapter, null, expertClick);
        // 将屏幕宽度均分,每个tab占有等宽
        for (int i = 0; i < tagList.size(); i++) {
            RelativeLayout layout = (RelativeLayout) ll_cur.getChildAt(i);
            ViewGroup.LayoutParams lp = layout.getLayoutParams();
            lp.width = win_width / tagList.size() / 2;

        }
        ll_cur.getChildAt(Integer.valueOf(page)).setSelected(true);
//        ll_cur.getChildAt(Integer.valueOf(page))
//                .findViewById(R.id.cur_tab_bg_select)
//                .setVisibility(View.VISIBLE);
    }

    /**
     * 选择的Column里面的Tab
     * */
    private void selectTab(int postion) {
        // 判断是否选中
        int win_width = UIUtils.getScreenWidth();
        for (int j = 0; j < tagList.size(); j++) {
            if (j == postion) {
                ll_cur.getChildAt(j).setSelected(true);
//                ll_cur.getChildAt(j).findViewById(R.id.cur_tab_bg_select)
//                        .setVisibility(View.VISIBLE);

            } else {
                ll_cur.getChildAt(j).setSelected(false);
//                ll_cur.getChildAt(j).findViewById(R.id.cur_tab_bg_select)
//                        .setVisibility(View.GONE);
            }
        }
        // 统计selectTab
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("选课", names[postion]);
//        MobclickAgent.onEvent(this, "selectTab", map);
        hsv_cur.smoothScrollTo(win_width * postion / 3, 0);
        View checkView = ll_cur.getChildAt(postion);
        int k = checkView.getMeasuredWidth();
        int l = checkView.getLeft();
        int i2 = l + k / 2 - win_width / 2;
        hsv_cur.smoothScrollTo(i2, 0);
    }

    @OnClick({R.id.layout_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.layout_back:
                finish();
                break;

        }
    }
}
