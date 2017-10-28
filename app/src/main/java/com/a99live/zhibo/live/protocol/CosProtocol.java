package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by fuyk on 2016/9/5.
 */
public class CosProtocol extends BaseProtocol {
    @Override
    protected String getPath() {
        return "/v1/upload/getImageSign";
    }

    public Observable<String> getCosSign(LiveRequestParams params) {
        return createObservable(getPath(), LiveHttpClient.METHOD_POST, params);
    }

    public Observable<String> getUploadSign(LiveRequestParams params) {
        return createObservable(getPath(), LiveHttpClient.METHOD_POST, params);
    }
}
