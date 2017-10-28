package com.a99live.zhibo.live.wxapi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.WxpayProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/10/11.
 * 微信支付的回调界面
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private ProgressDialog dialog;

    private WxpayProtocol wxpayProtocol;

    private IWXAPI api;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pay_result);
        /**注册APPID*/
        api = WXAPIFactory.createWXAPI(this, TCConstants.APP_ID);
        api.handleIntent(getIntent(), this);
    }


    private void initProgress() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("提示");
        dialog.setMessage("支付结果查询中");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    /**
     * 得到支付结果回调
     * 返回值：0 支付成功； -1 发生错误；-2 用户取消
     * int COMMAND_UNKNOWN = 0;
     *int COMMAND_SENDAUTH = 1;
     *int COMMAND_SENDMESSAGE_TO_WX = 2;
     *int COMMAND_GETMESSAGE_FROM_WX = 3;
     *int COMMAND_SHOWMESSAGE_FROM_WX = 4;
     *int COMMAND_PAY_BY_WX = 5;
     *int COMMAND_LAUNCH_BY_WX = 6;
     */
    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX){
            if (resp.errCode == 0){
                SPUtils.putString(SPUtils.WXPAY_SUCCESS,"2");
            }else if (resp.errCode == -2){//用户取消
//                SPUtils.putString(SPUtils.WXPAY_ORDER_ID,"-2");
                SPUtils.putString(SPUtils.WXPAY_SUCCESS,"-2");
            }else {//发生错误
//                SPUtils.putString(SPUtils.WXPAY_ORDER_ID,"-1");
                SPUtils.putString(SPUtils.WXPAY_SUCCESS,"-1");
            }
        }
        finish();
    }

    /**
     * 获取支付结果
     * @param order_id
     */
    private void getPayResult(String order_id){
        wxpayProtocol = new WxpayProtocol();
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

                                    }else {
                                    }
                                }


                            } else {
                            }

                        }
                        if (dialog != null){
                            dialog.dismiss();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (dialog != null){
                            dialog.dismiss();
                        }
                    }
                });
    }

}
