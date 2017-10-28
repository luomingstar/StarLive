package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by fuyk on 2016/11/10.
 */

public class ShareProtocol extends BaseProtocol {

    @Override
    protected String getPath() {
        return "/v1/share/";
    }

    public Observable<String> getShareUrl(LiveRequestParams params) {
        String uri = getPath() + "index/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     *参数：uid:用户ID
     */
    public Observable<String> ShareCallback(LiveRequestParams params) {
        String uri = getPath() + "callBack/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }
}
