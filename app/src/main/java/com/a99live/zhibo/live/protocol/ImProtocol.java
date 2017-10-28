package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by fuyk on 2016/9/9.
 */
public class ImProtocol extends BaseProtocol {

    @Override
    protected String getPath() {
        return "/v1/im/";
    }

    /**
     * 获取IM的签名
     */
    public Observable<String> getIMSign(LiveRequestParams params){
        String uri = getPath() + "getsig/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    /**
     * IM 获取系统通知
     * gid :组id
     */
    public Observable<String> getIMMessage(LiveRequestParams params){
        String uri = getPath() + "pullNotice/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

}
