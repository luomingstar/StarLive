package com.a99live.zhibo.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.SearchAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.SearchProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.KeyBoardUtils;
import com.a99live.zhibo.live.utils.UIUtils;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/12/7.
 */

public class SearchActivity extends BaseActivity {

    private SearchProtocol searchPrtocol;

    private SearchAdapter searchAdapter;

    @Bind(R.id.search_edit)
    EditText search_edit;

    @Bind(R.id.iv_search_text_clear)
    ImageView iv_search_text_clear;

    @Bind(R.id.recycler_search)
    RecyclerView recycler_search;

    @Bind(R.id.iv_search_empty)
    ImageView iv_search_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(search_edit.getText().toString())){
                    iv_search_text_clear.setVisibility(View.VISIBLE);
                }else{
                    iv_search_text_clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        iv_search_text_clear.setVisibility(View.GONE);
        KeyBoardUtils.openKeybord(search_edit, SearchActivity.this);
        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    KeyBoardUtils.closeKeybord(search_edit, SearchActivity.this);
                    search();
                }
                return false;
            }
        });
    }

    private void search() {
        String searchContext = search_edit.getText().toString().trim();
        if (TextUtils.isEmpty(searchContext)){
            UIUtils.showToast("输入不能为空");
        }else {
            getSearchInfo(searchContext);
//            iv_search_text_clear.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        searchPrtocol = new SearchProtocol();
    }

    public static void goSearchActivty(Context context){
        context.startActivity(new Intent(context, SearchActivity.class));
    }

    @OnClick({R.id.tv_search_cancel, R.id.iv_search_text_clear})
    void onClick(View view){
        switch (view.getId()){
            case R.id.tv_search_cancel:
                back();
                break;
            case R.id.iv_search_text_clear:
                search_edit.setText("");
                iv_search_text_clear.setVisibility(View.GONE);
                break;
        }
    }

    private void getSearchInfo(String searchContext){
        LiveRequestParams params = new LiveRequestParams();
        params.put("key", searchContext);

        searchPrtocol.getSearch(params)
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
                                    showSearch(dataList);
                                    recycler_search.setVisibility(View.VISIBLE);
                                    iv_search_empty.setVisibility(View.GONE);
                                }else{
                                    UIUtils.showToast("未找到搜索结果");
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
                        recycler_search.setVisibility(View.GONE);
                        iv_search_empty.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void showSearch(ArrayList<Map<String, String>> dataList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_search.setLayoutManager(linearLayoutManager);
        recycler_search.setAdapter(searchAdapter = new SearchAdapter(this, dataList));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void back() {
        finish();
    }

}
