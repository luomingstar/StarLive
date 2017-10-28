package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by fuyk on 2016/9/20.
 */
public class AboutInfo {


    /**
     * code : 0
     * msg : success
     * data : 关于99关于99关于99关于99关于99关于99关于99关于99关于99关于99关于99
     * append : []
     */

    private int code;
    private String msg;
    private String data;
    private List<?> append;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<?> getAppend() {
        return append;
    }

    public void setAppend(List<?> append) {
        this.append = append;
    }
}
