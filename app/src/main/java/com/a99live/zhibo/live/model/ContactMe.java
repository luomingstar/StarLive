package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by fuyk on 2016/9/20.
 */
public class ContactMe {


    /**
     * code : 0
     * msg : success
     * data :
     99直播 - 中老年的直播平台
     北京久久互动文化传媒有限公司
     办公地址：北京市海淀区知春路9号坤讯大厦1208，临近10号线地铁西土城站
     TEL：010-65928933
     E-mail:
     人力资源部：hr@999d.com
     市场部：market@999d.com：０１０－65922662

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
