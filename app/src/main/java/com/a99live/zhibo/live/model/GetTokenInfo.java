package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by fuyk on 2016/9/2.
 */
public class GetTokenInfo {


    /**
     * code : 0
     * msg : success
     * data : {"key":"352caab68244eb11","expire":1482913042,"global":{"msg_exp_time":"20"}}
     * append : []
     */

    private int code;
    private String msg;
    /**
     * key : 352caab68244eb11
     * expire : 1482913042
     * global : {"msg_exp_time":"20"}
     */

    private DataBean data;
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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public List<?> getAppend() {
        return append;
    }

    public void setAppend(List<?> append) {
        this.append = append;
    }

    public static class DataBean {
        private String key;
        private int expire;
        /**
         * msg_exp_time : 20
         */

        private GlobalBean global;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getExpire() {
            return expire;
        }

        public void setExpire(int expire) {
            this.expire = expire;
        }

        public GlobalBean getGlobal() {
            return global;
        }

        public void setGlobal(GlobalBean global) {
            this.global = global;
        }

        public static class GlobalBean {
            private String msg_exp_time;

            public String getMsg_exp_time() {
                return msg_exp_time;
            }

            public void setMsg_exp_time(String msg_exp_time) {
                this.msg_exp_time = msg_exp_time;
            }
        }
    }
}
