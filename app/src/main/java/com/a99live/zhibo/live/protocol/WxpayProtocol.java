package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by fuyk on 2016/10/10.
 */
public class WxpayProtocol extends BaseProtocol {

    @Override
    protected String getPath() {
        return "/v1/wxpay/";
    }

    /**
     * 微信统一下单
     *  type        目前传gift
        order_id    订单ID
     */
    public Observable<String> getOrders(LiveRequestParams params){
        String uri = getPath() + "unified/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 支付结果查询
     *  order_id        订单ID
        transaction_id  微信订单ID
     */
    public Observable<String> getPayResult(LiveRequestParams params){
        String uri = getPath() + "payresult/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }
}
