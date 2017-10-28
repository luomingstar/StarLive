package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by JJGCW on 2017/4/11.
 */

public class VideoProtocol extends BaseProtocol {
    @Override
    protected String getPath() {
        return "/v1/video/";
    }


    /**
     * 获取标签
     */
    public Observable<String> getVideoTag(LiveRequestParams params){
        String uri = getPath() + "tag/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    /**
     * 获取list
     */
    public Observable<String> getVideoList(LiveRequestParams params){
        String uri = getPath() + "list/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    /**
     * 获取top
     */
    public Observable<String> getVideoTop(LiveRequestParams params){
        String uri = getPath() + "top/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    /**
     *点赞
     */
    public Observable<String> getVideoLike(LiveRequestParams params){
        String uri = getPath() + "like/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    public Observable<String> getCList(LiveRequestParams params){
        String uri = getPath() + "clist/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    public Observable<String> getShare(LiveRequestParams params){
        String uri = getPath() + "share/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    public Observable<String> getComm(LiveRequestParams params){
        String uri = getPath() + "comm/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    public Observable<String> getPlay(LiveRequestParams params){
        String uri = getPath() + "play/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }
}
