package com.a99live.zhibo.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.InterestListAdapter;
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
 * 感兴趣页面
 * Created by fuyk on 2017/2/15.
 */

public class InterestActivity extends BaseActivity {

    private NewRoomProtocol newRoomProtocol;
    private UserProtocol userProtocol;
    private InterestListAdapter interesetListAdapter;

    @Bind(R.id.recycler_interest)
    RecyclerView recycler_interest;

    @Bind(R.id.tv_interest_finish)
    TextView tv_interest_finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        ButterKnife.bind(this);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initData();
    }

    private void initData() {
        newRoomProtocol = new NewRoomProtocol();
        userProtocol = new UserProtocol();
        tags = new ArrayList<>();
        getInterestData();
    }

    /**获取兴趣接口*/
    private void getInterestData(){
        LiveRequestParams params = new LiveRequestParams();
        newRoomProtocol.getTag(params).observeOn(AndroidSchedulers.mainThread())
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
                                    showInterestTag(dataList);
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

    ArrayList<Map<String, String>> interestData;
    private void showInterestTag(ArrayList<Map<String, String>> dataList){
        for (int i = 0; i < dataList.size(); i++){
            dataList.get(i).put("isSelect","0");
        }

        interestData = dataList;

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recycler_interest.setLayoutManager(layoutManager);
        recycler_interest.setAdapter(interesetListAdapter = new InterestListAdapter(this, interestData));
    }

    List<String> tags;
    @OnClick(R.id.tv_interest_finish)
    void onClick(){
        if (tags == null){
            tags = new ArrayList<>();
        }
        tags.clear();
        for (int i = 0; i < interestData.size() ; i++){
            if ("1".equals(interestData.get(i).get("isSelect"))){
                tags.add(interestData.get(i).get("id"));
            }
        }
        if (tags.size()==0){
            UIUtils.showToast("请选择感兴趣的内容");
            return;
        }
        String interestTags = Pattern.compile("\\\\b([\\\\w\\\\W])\\\\b").
                matcher(tags.toString().substring(1,tags.toString().length()-1)).replaceAll("'$1'");
        if (interestTags.toString().isEmpty()){
            UIUtils.showToast("请选择感兴趣的内容");
        }else {
            putInterestData(interestTags);
        }
    }

    /**提交兴趣标签*/
    private void putInterestData(String interestTags) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("tags", interestTags);

        userProtocol.getInterest(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                finish();
                                startActivity(new Intent(InterestActivity.this, RecommendAnchorActivity.class));
                                overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
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
