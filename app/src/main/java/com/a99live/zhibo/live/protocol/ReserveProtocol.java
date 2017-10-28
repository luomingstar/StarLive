package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by JJGCW on 2017/2/16.
 */

public class ReserveProtocol extends BaseProtocol{


    @Override
    protected String getPath() {
        return "/v1/reserve/";
    }

    /**
     * 获取预约信息
     * 参数  room_id  房间Id page num
     */
    public Observable<String> getOrder(LiveRequestParams params){
        String uri = getPath() + "lists/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 预约
     * 参数  room_id  房间Id page num
     */
    public Observable<String> setRecordUser(LiveRequestParams params){
        String uri = getPath() + "recordUser/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 预约详情
     */
    public Observable<String> getYuYueDetail(LiveRequestParams params){
        String uri = getPath() + "detail/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }




}
