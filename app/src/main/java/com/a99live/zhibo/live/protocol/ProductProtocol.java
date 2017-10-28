package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by fuyk on 2016/10/11.
 */
public class ProductProtocol extends BaseProtocol {

    @Override
    protected String getPath() {
        return "/v1/product/list";
    }

    /**
     * 获取商品列表
     */
    public Observable<String> getProductList(LiveRequestParams params){
        return createObservable(getPath(), LiveHttpClient.METHOD_POST, params);
    }
}
