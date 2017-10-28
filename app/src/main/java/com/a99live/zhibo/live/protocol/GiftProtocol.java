package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by fuyk on 2016/9/22.
 */
public class GiftProtocol extends BaseProtocol {
    @Override
    protected String getPath() {
        return "/v1/gift/";
    }

    /**
     * 礼物列表
     */
    public Observable<String> getGiftList(LiveRequestParams params){
        String uri = getPath() + "list/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    /**
     * 送礼物
     *  number:数量
     *  gift_id:礼物id
     *  room_id:房间id
     *  to:接收人id
     */
    public Observable<String> sendGift(LiveRequestParams params){
        String uri = getPath() + "send/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }


}
