package com.a99live.zhibo.live.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.RecommendListAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.NewRoomProtocol;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2017/2/15.
 */

public class RecommendAnchorActivity extends BaseActivity {

    private NewRoomProtocol newRoomProtocol;
    private UserProtocol userProtocol;
    private RecommendListAdapter recommendListAdapter;

    @Bind(R.id.recycler_recommend)
    RecyclerView recycler_recommend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        ButterKnife.bind(this);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initData();
    }

    private void initData() {
        newRoomProtocol = new NewRoomProtocol();
        userProtocol = new UserProtocol();
        RecommendData = new ArrayList<>();
        getRecommendAnchorData();
    }

    /**获取推荐主播列表*/
    private void getRecommendAnchorData() {
        LiveRequestParams params = new LiveRequestParams();

        newRoomProtocol.getRecommen(params)
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
                                    showRecommendAnchor(dataList);
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

    ArrayList<Map<String, String>> RecommendData;
    //is_selected
    private void showRecommendAnchor(ArrayList<Map<String, String>> dataList) {
        if (RecommendData == null){
            RecommendData = new ArrayList<>();
        }
        RecommendData.clear();
        for (int i = 0; i < dataList.size(); i++){
            if (!dataList.get(i).containsKey("is_selected")){
                dataList.get(i).put("is_selected","0");
            }
        }
        RecommendData.addAll(dataList);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recycler_recommend.setLayoutManager(layoutManager);
        recycler_recommend.setAdapter(recommendListAdapter = new RecommendListAdapter(this, RecommendData));
    }

    List<String> anchorTags;

    @OnClick({R.id.tv_recommend_follow, R.id.tv_jump})
    void onClick(View view){
        switch (view.getId()){
            case R.id.tv_recommend_follow:
                anchorTags = new ArrayList<>();
                for (int i = 0; i < RecommendData.size() ; i++){
                    if ("1".equals(RecommendData.get(i).get("is_selected"))){
                        anchorTags.add(RecommendData.get(i).get("user_id"));
                    }
                }

                String recommendTags = Pattern.compile("\\\\b([\\\\w\\\\W])\\\\b").
                        matcher(anchorTags.toString().substring(1, anchorTags.toString().length()-1)).replaceAll("'$1'");

                batchFollow(recommendTags);
                break;
            case R.id.tv_jump:
                finish();
                break;
        }
    }

    private void batchFollow(String fids) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("fids", fids);

        userProtocol.getFollowBatch(params)
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
                                    finish();
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scale_big_in, R.anim.tran_bottom_out);
    }

    @Override
    public void onBackPressed() {
    }
}
