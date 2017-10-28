package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by fuyk on 2016/9/2.
 */
public class RoomProtocol extends BaseProtocol {
    @Override
    protected String getPath() {
        return "/v1/room/";
    }

    /**
     * 热门列表
     * o_minid 直播最小id room_id
     * h_minid 录播最小id lu_id
     */
    public Observable<String> getLiveList(LiveRequestParams params){
        String uri = getPath() + "list/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 热门列表
     * o_minid 直播最小id room_id
     * h_minid 录播最小id lu_id
     */
    public Observable<String> getLiveListNew(LiveRequestParams params){
        String uri = "/v1/lists/hot";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }


    /**
     * 关注列表
     * o_mind 直播最小id room_id
     * h_mind 录播最小id lu_id
     */
    public Observable<String> getFocusList(LiveRequestParams params){
        String uri = getPath() + "follow/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 发起直播
     * title
     * image
     */
    public Observable<String> getStartLive(LiveRequestParams params){
        String uri = getPath() + "add/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 通知服务端开始推流
     * room_id
     * live_code
     */
    public Observable<String> startPush(LiveRequestParams params){
        String uri = getPath() + "show/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 关闭直播
     * code 直播码
     */
    public Observable<String> getCloseLive(LiveRequestParams params){
        String uri = getPath() + "stop/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 进入直播间
     * id 房间room_id
     */
    public Observable<String> enterLive(LiveRequestParams params){
        String uri = getPath() + "in/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 减去在线人数
     * roomId 房间id
     */
    public Observable<String> lessOnline(LiveRequestParams params){
        String uri = getPath() + "less/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 增加分享次数
     */
    public Observable<String> shareCount(LiveRequestParams params){
        String uri = getPath() + "share/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 进入录播
     * hid 录播房间id lu_id
     */
    public Observable<String> enterRecord(LiveRequestParams params){
        String uri = getPath() + "play/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 删除我的直播
     */
    public Observable<String> removeMylive(LiveRequestParams params){
        String uri = getPath() + "delmyvideo/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

}
