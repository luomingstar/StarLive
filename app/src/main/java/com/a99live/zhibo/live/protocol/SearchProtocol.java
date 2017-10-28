package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by fuyk on 2016/12/7.
 */

public class SearchProtocol extends BaseProtocol {

    @Override
    protected String getPath() {
        return "/v1/Search/";
    }

    /**
     * 搜索接口
     * 参数：key
     */
    public Observable<String> getSearch(LiveRequestParams params) {
        String uri = getPath() + "user/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }
}
