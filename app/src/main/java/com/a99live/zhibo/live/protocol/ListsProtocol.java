package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by fuyk on 2016/12/20.
 */

public class ListsProtocol extends BaseProtocol {
    @Override
    protected String getPath() {
        return "/v1/lists/";
    }

    /**
     * 频道列表
     */
    public Observable<String> getChannelList(LiveRequestParams params){
        String uri = getPath() + "channel/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }
}
