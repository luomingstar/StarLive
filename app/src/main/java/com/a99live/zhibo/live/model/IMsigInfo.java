package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by fuyk on 2016/9/12.
 */
public class IMsigInfo {


    /**
     * code : 0
     * msg : success
     * data : {"uid":"16670","sig":"eJxlz11PgzAUBuB7fgXhVmNoKx818WIRtzEhps5RuGpIW5Y6Bggdbhr-*za2RBKvzsXznrzn-BimaVrv0fIu57zeVZrpQyMt88G0bOv2D5tGCZZrhlrxD*W*Ua1keaFlOyB0MLTtcUQJWWlVqGsAuK435k5s2FBx0fvTMkC*j8cRtR4wfl49hSTYivT1O5MwShZxrA9fN1McTdpEhDyhZBF4kPZwpby8JiRcZ2jG5yjtnY0flOkEgZdyjt*mFNE96cqPWZHyz8zd0XxZP44qtdrK60Eecn18GiPtZdupuro8bAMHAIDt89fGr3EExsVboA__"}
     * append : []
     */

    private int code;
    private String msg;
    /**
     * uid : 16670
     * sig : eJxlz11PgzAUBuB7fgXhVmNoKx818WIRtzEhps5RuGpIW5Y6Bggdbhr-*za2RBKvzsXznrzn-BimaVrv0fIu57zeVZrpQyMt88G0bOv2D5tGCZZrhlrxD*W*Ua1keaFlOyB0MLTtcUQJWWlVqGsAuK435k5s2FBx0fvTMkC*j8cRtR4wfl49hSTYivT1O5MwShZxrA9fN1McTdpEhDyhZBF4kPZwpby8JiRcZ2jG5yjtnY0flOkEgZdyjt*mFNE96cqPWZHyz8zd0XxZP44qtdrK60Eecn18GiPtZdupuro8bAMHAIDt89fGr3EExsVboA__
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
        private String uid;
        private String sig;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getSig() {
            return sig;
        }

        public void setSig(String sig) {
            this.sig = sig;
        }
    }
}
