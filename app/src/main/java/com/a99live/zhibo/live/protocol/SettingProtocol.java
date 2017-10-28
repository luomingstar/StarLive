package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by fuyk on 2016/9/20.
 */
public class SettingProtocol extends BaseProtocol {
    @Override
    protected String getPath() {
        return "/v1/about/";
    }

    /**
     * 意见反馈
     */
    public Observable<String> feedback(LiveRequestParams params) {
        String uri = getPath() + "suggest/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 联系我们
     */
    public Observable<String> contact(LiveRequestParams params) {
        String uri = getPath() + "contact/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 关于99
     */
    public Observable<String> about(LiveRequestParams params) {
        String uri = getPath() + "me/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 用户协议
     */
    public Observable<String> agreement(LiveRequestParams params) {
        String uri = getPath() + "arg/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 充值协议
     */
    public Observable<String> getPayAgreement(LiveRequestParams params) {
        String uri = getPath() + "czxy/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 直播协议
     */
    public Observable<String> getLiveAgreement(LiveRequestParams params) {
        String uri = getPath() + "zbxy/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

}
