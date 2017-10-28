package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by JJGCW on 2016/12/13.
 */

public class WebProtocol extends BaseProtocol {
    @Override
    protected String getPath() {
        return null;
    }

    /**
     * 提现接口
     */
    public Observable<String> getWebView(String url,LiveRequestParams params){
        String uri = url;
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }
}
