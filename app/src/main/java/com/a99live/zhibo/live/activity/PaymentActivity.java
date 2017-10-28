package com.a99live.zhibo.live.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.OrderProtocol;
import com.a99live.zhibo.live.protocol.WxpayProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.MyProgressDialog;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/10/13.
 */
public class PaymentActivity extends BaseActivity {

    private IWXAPI api;


    private WxpayProtocol wxpayProtocol;
    private OrderProtocol orderProtocol;

    private String product;
    private String money;
    private String amount;

    private boolean isFinish = false;



    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_pay_way)
    TextView tv_pay_way;

    @Bind(R.id.tv_money)
    TextView tv_money;

    @Bind(R.id.tv_order)
    TextView tv_order;

    @Bind(R.id.tv_wexin_pay)
            TextView tv_wexin_pay;

    String type = "Z";
    private MyProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        initProgress();
        ButterKnife.bind(this);
        initData();
        initView();
        addOrder();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinish){
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void initProgress() {

        dialog = new MyProgressDialog(this,"确认订单中");
        dialog.setCancelable(false);
        dialog.show();

    }

    private void initData() {
        product = getIntent().getStringExtra("product");
        money = getIntent().getStringExtra("money");
        amount = getIntent().getStringExtra("amount");
        wxpayProtocol = new WxpayProtocol();
        orderProtocol = new OrderProtocol();
    }

    private void initView() {
        tv_title.setText(R.string.order_sure);

        tv_pay_way.setText("微信");
        tv_money.setText(money + "元");
        tv_order.setText(amount + "钻");
    }

    @OnClick({R.id.layout_back})
    void onClick(View view){
        switch (view.getId()){
            case R.id.layout_back:
                back();
                break;
        }
    }

    /**
     * 下单接口
     */
    private void addOrder(){
        LiveRequestParams params = new LiveRequestParams();
        params.put("product",product);

        orderProtocol.getOrder(params)
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
                                if (dataList.size() > 0){
                                    Map<String, String> map1 = dataList.get(0);
                                    goWeChatClick(map1.get("order_id"));
                                }
                            }else{
                                UIUtils.showToast(map.get("msg"));
                                if (dialog != null){
                                    dialog.dismiss();
                                }
                                finish();
                            }

                        }else{
                            UIUtils.showToast(s);
                            if (dialog != null){
                                dialog.dismiss();
                            }
                            finish();
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d("livelog", "下单失败");
                        UIUtils.showToast("下单失败");
                        if (dialog != null){
                            dialog.dismiss();
                        }
                        finish();
                    }
                });
    }

    /**
     * 微信
     */
    private void goWeChatClick(String order_id) {
        SPUtils.putString(SPUtils.WXPAY_ORDER_ID,order_id);
        SPUtils.putString(SPUtils.WXPAY_SUCCESS,"0");
        getWeixinInfo(order_id);
    }

    /**
     * 微信统一下单
     *  type:gift
     *  order_id: 订单id
     */
    private void getWeixinInfo(String order_id) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("type", type);
        params.put("order_id", order_id);

        wxpayProtocol.getOrders(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                final ArrayList<Map<String, String>> data = JsonUtil.getListMapByJson(map.get("data"));
                                if (data.size() > 0){
                                    final Map<String, String> dataMap = data.get(0);
                                    tv_wexin_pay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            weChatPay(dataMap);
                                        }
                                    });
                                }
                            }else{
                                UIUtils.showToast(map.get("msg"));
                                finish();
                            }
                        }
                        if (dialog != null){
                            dialog.dismiss();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast("订单请求异常");
                        if (dialog != null){
                            dialog.dismiss();
                        }
                        finish();
                    }
                });
    }

    private boolean isWXAppInstalledAndSupported() {
        IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
        msgApi.registerApp(TCConstants.APP_ID);

        boolean sIsWXAppInstalledAndSupported = msgApi.isWXAppInstalled()
                && msgApi.isWXAppSupportAPI();

        return sIsWXAppInstalledAndSupported;
    }

    /**
     * 调微信支付sdk
     */
    private void weChatPay(Map<String, String> dataMap) {
//        if (!isWXAppInstalledAndSupported()){
//            UIUtils.showToast("请安装微信客户端");
//            return;
//        }


        api = WXAPIFactory.createWXAPI(this, dataMap.get("appid"));
        api.registerApp(dataMap.get("appid"));
        boolean sIsWXAppInstalledAndSupported = api.isWXAppInstalled()
                && api.isWXAppSupportAPI();
        if (!sIsWXAppInstalledAndSupported){
            UIUtils.showToast("请安装微信客户端");
            return;
        }
        isFinish = true;

        PayReq payRequest = new PayReq();
        payRequest.appId = dataMap.get("appid");
        payRequest.partnerId = dataMap.get("partnerid");
        payRequest.prepayId = dataMap.get("prepayid");
        payRequest.packageValue = "Sign=WXPay";
        payRequest.nonceStr = dataMap.get("noncestr");
        payRequest.timeStamp = dataMap.get("timestamp");
        payRequest.sign = dataMap.get("sign");
        //发起请求
        api.sendReq(payRequest);

    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        overridePendingTransition(R.anim.scale_big_in, R.anim.tran_right_out);
        finish();
    }

}
