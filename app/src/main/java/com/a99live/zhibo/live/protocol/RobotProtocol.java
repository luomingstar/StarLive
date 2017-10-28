package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by JJGCW on 2016/11/23.
 */

public class RobotProtocol extends BaseProtocol {

    @Override
    protected String getPath() {
        return "/v1/robot/";
    }

    /**
     * 获取机器人列表
     * 参数
     */
    public Observable<String> getRobotList(LiveRequestParams params){
        String uri = getPath() + "create/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

}
