package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * APP升级
 * Created by fuyk on 2016/9/2.
 */
public class AppProtocol extends BaseProtocol {
    @Override
    protected String getPath() {
        return "/v1/app/";
    }

    /**
     * App升级接口
     * 参数：vs 版本号
     */
    public Observable<String> getAppVersion(LiveRequestParams params) {
        String uri = getPath() + "upgrade/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 获取轮播图接口
     */
    public Observable<String> getBanner(LiveRequestParams params) {
        String uri = getPath() + "ad/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

}
