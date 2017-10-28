package com.a99live.zhibo.live.net;

/**
 * 服务器返回的错误响应
 * Created by ljb on 2016/5/20.
 */
public class ErrorThrowable extends Throwable {

    private String mErrorJson;


    public ErrorThrowable(String mErrorJson) {
        this.mErrorJson = mErrorJson;
    }

    public String getErrorJson() {
        return mErrorJson;
    }

    public void setErrorJson(String mErrorJson) {
        this.mErrorJson = mErrorJson;
    }
}

