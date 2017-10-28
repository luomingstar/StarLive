package com.a99live.zhibo.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.TagListAdapter;
import com.a99live.zhibo.live.event.TagEvent;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.NewRoomProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by JJGCW on 2016/11/24.
 */

public class LiveTagActivity extends BaseActivity implements TagListAdapter.OnItemClickListener {

    private NewRoomProtocol newRoomProtocol;
    private TagListAdapter tagListAdapter;

    @Bind(R.id.recycler_tag)
    RecyclerView recycler_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tag);
        ButterKnife.bind(this);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initData();
    }

    public static void goLiveTagActivity(Context context){
        Intent intent = new Intent(context,LiveTagActivity.class);
        context.startActivity(intent);
    }

    private void initData(){
        newRoomProtocol = new NewRoomProtocol();
        getTagFromNet();
    }

    /**获取标签接口*/
    private void getTagFromNet(){
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
                                    showTag(dataList);
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

    private void showTag(ArrayList<Map<String, String>> dataList){

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recycler_tag.setLayoutManager(layoutManager);
        recycler_tag.setAdapter(tagListAdapter = new TagListAdapter(this, dataList));
        tagListAdapter.setOnItemClickListener(this);
    }

    @OnClick(R.id.rl_live_tag)
    void onClick(){
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scale_big_in , R.anim.tran_bottom_out);
    }

    @Override
    public void onClick(String name, String tagId, String content) {

        EventBus.getDefault().post(new TagEvent(name, tagId, content));

        finish();
    }
}
