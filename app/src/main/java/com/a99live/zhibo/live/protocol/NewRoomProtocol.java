package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by fuyk on 2016/11/7.
 */

public class NewRoomProtocol extends BaseProtocol {

    @Override
    protected String getPath() {
        return "/v1/room2/";
    }

    /**
     * 获取推流地址
     */
    public Observable<String> getStream(LiveRequestParams params){
        String uri = getPath() + "streamurl/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 创建房间
     */
    public Observable<String> creatRoom(LiveRequestParams params){
        String uri = getPath() + "create/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 标签
     */
    public Observable<String> getTag(LiveRequestParams params){
        String uri = getPath() + "tag/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 禁言
     * 参数：groupid
     *      number99
     */
    public Observable<String> noSay(LiveRequestParams params){
        String uri = getPath() + "forbidmsg/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 检查直播是createroom是否成功
     * @param params
     * @return
     */
    public Observable<String> ping(LiveRequestParams params){
        String uri = getPath() + "ping/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     *推荐主播
     */
    public Observable<String> getRecommen(LiveRequestParams params){
        String uri = getPath() + "recommendav/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }
}
