package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by JJGCW on 2016/11/16.
 */

public class RankProtocol extends BaseProtocol {
    @Override
    protected String getPath() {
        return "/v1/rank/";
    }

    /**
     * 获取粉丝贡献
     * 参数  room_id  房间Id page num
     */
    public Observable<String> getFansContributionInRoom(LiveRequestParams params){
        String uri = getPath() + "rankinroom/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 获取粉丝贡献
     * 参数  ucode  UCODE page num
     */
    public Observable<String> getFansContributionInUser(LiveRequestParams params){
        String uri = getPath() + "rankinuser/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }
}
