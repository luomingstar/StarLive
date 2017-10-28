package com.a99live.zhibo.live.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.adapter.ProductInfoAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.ProductProtocol;
import com.a99live.zhibo.live.protocol.WxpayProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.LoginManager;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.utils.YMClick;
import com.a99live.zhibo.live.view.weight.MyProgressDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 我的账户页面
 * Created by fuyk on 2016/10/11.
 */
public class AccountActivity extends BaseActivity {
    private ProductProtocol productProtocol;

    private ProductInfoAdapter productInfoAdapter;

    private MyProgressDialog dialog;

    private List<Map<String,String>> mData;

    //账户余额
    private String residue_money;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_money)
    TextView tv_money;

    @Bind(R.id.layout_pay)
    RadioGroup layout_pay;

    @Bind(R.id.list_product)
    ListView list_product;

    @Bind(R.id.rb_pay_agr)
    CheckBox rb_pay_agr;

    @Bind(R.id.checktext)
    TextView checktext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initProgress() {

        dialog = new MyProgressDialog(this,"支付结果查询中");
        dialog.setCancelable(false);
        dialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getIsOrder(){
        String mOrderId = SPUtils.getString(SPUtils.WXPAY_ORDER_ID);
        String isSuccess = SPUtils.getString(SPUtils.WXPAY_SUCCESS);
        if (!"0".equals(isSuccess)){
            if ("2".equals(isSuccess)){//成功
                if (!"".equals(mOrderId)) {
                    initProgress();
                    getPayResult(mOrderId);
                }
            }else if ("-1".equals(isSuccess)){
//                if (!"".equals(mOrderId)) {
//                    if ("-2".equals(mOrderId)){

//                    }else if ("-1".equals(mOrderId)){
                        UIUtils.showToast("支付错误");
//                    }
//                }
            }else if ("-2".equals(isSuccess)){
                UIUtils.showToast("取消支付");
            }
            SPUtils.putString(SPUtils.WXPAY_ORDER_ID,"");
            SPUtils.putString(SPUtils.WXPAY_SUCCESS,"0");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getIsOrder();
    }

    /**
     * 获取支付结果
     * @param order_id
     */
    private void getPayResult(final String order_id){
        getProductInfo();
        WxpayProtocol wxpayProtocol = new WxpayProtocol();
        LiveRequestParams params = new LiveRequestParams();
        params.put("order_id", order_id);

        wxpayProtocol.getPayResult(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog", s);
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");

                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                if (dataList.size()>0){
                                    Map<String, String> dataMap = dataList.get(0);
                                    if ("2".equals(dataMap.get("pay_status")) && "SUCCESS".equals(dataMap.get("pay_desc"))){
                                        UIUtils.showToast("支付成功");
                                        YMClick.onEvent(AccountActivity.this,YMClick.PAY,order_id + "_success");
                                    }else {
                                        UIUtils.showToast(dataMap.get("pay_desc"));
                                        YMClick.onEvent(AccountActivity.this,YMClick.PAY,order_id + "_erre");
                                    }
                                }


                            } else {
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{
                            UIUtils.showToast(s);
                        }

                        if (dialog != null){
                            dialog.dismiss();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast("服务器繁忙，请稍后查看");
                        if (dialog != null){
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void initData() {
        SPUtils.putString(SPUtils.WXPAY_ORDER_ID,"");
        SPUtils.putString(SPUtils.WXPAY_SUCCESS,"0");
        productProtocol = new ProductProtocol();
        mData = new ArrayList<>();
        productInfoAdapter = new ProductInfoAdapter(this,mData,R.layout.item_product,new String[]{"money"},
                new int[]{R.id.tv_price});
        list_product.setAdapter(productInfoAdapter);

        getProductInfo();

    }

    private void initView() {
        tv_title.setText(R.string.my_account);
        layout_pay.check(R.id.weixin_pay);
    }

    /**获取商品列表*/
    private void getProductInfo(){
        LiveRequestParams params = new LiveRequestParams();

        productProtocol.getProductList(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog", "商品列表=" + s);
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");
                                String append = map.get("append");
                                ArrayList<Map<String, String>> listMapByJson2 = JsonUtil.getListMapByJson(append);
                                initTopPage(listMapByJson2.get(0).get("Z"));
                                ArrayList<Map<String, String>> listMapByJson1 = JsonUtil.getListMapByJson(data);
                                if (listMapByJson1.size()>0){
                                    initBottomPage(listMapByJson1);
                                }
                            }else{
                                LoginManager.loginErreCode(map.get("code"),AccountActivity.this,true);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    /**商品列表
     * @param listMapByJson1*/
    private void initBottomPage(ArrayList<Map<String, String>> listMapByJson1) {
        mData.clear();
        mData.addAll(listMapByJson1);
        productInfoAdapter.notifyDataSetChanged();
//        if (productInfoAdapter == null){
//            productInfoAdapter = new ProductInfoAdapter(this,listMapByJson1,R.layout.item_product,new String[]{"money"},
//                    new int[]{R.id.tv_price});
//            list_product.setAdapter(productInfoAdapter);
//        } else {
//            productInfoAdapter.setData(listMapByJson1);
//            productInfoAdapter.notifyDataSetChanged();
//        }
    }

    /**账户余额*/
    private void initTopPage(String append) {
        residue_money = append;
        tv_money.setText(residue_money);
    }

    @OnClick({R.id.layout_back, R.id.weixin_pay, R.id.alipay,R.id.checktext})
    void onClick(View view){
        switch (view.getId()) {
            case R.id.layout_back:
                back();
                break;
            case R.id.weixin_pay:

                break;
            case R.id.alipay:
                UIUtils.showToast("暂未开放");
                break;
            case R.id.checktext:
                rb_pay_agr.setChecked(!rb_pay_agr.isChecked());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        overridePendingTransition(R.anim.scale_big_in, R.anim.tran_right_out);
        setResult(RESULT_OK,new Intent().putExtra("gift", residue_money));
//        if (residue_money != null && !"".equals(residue_money)){
//        }
        finish();
    }

}
