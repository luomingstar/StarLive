package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by fuyk on 2016/9/2.
 */
public class User {
    /**
     * code : 0
     * msg : success
     * data : {"ucode":975,"uauth":"09df61e6b611edb5baef32c53bec5b998a14dfe7","tags":[]}
     * append : []
     */

    private int code;
    private String msg;
    /**
     * ucode : 975
     * uauth : 09df61e6b611edb5baef32c53bec5b998a14dfe7
     * tags : []
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
        private int ucode;
        private String uauth;
        private List<?> tags;

        public int getUcode() {
            return ucode;
        }

        public void setUcode(int ucode) {
            this.ucode = ucode;
        }

        public String getUauth() {
            return uauth;
        }

        public void setUauth(String uauth) {
            this.uauth = uauth;
        }

        public List<?> getTags() {
            return tags;
        }

        public void setTags(List<?> tags) {
            this.tags = tags;
        }
    }


//    private int ucode;
//    private String uauth;
//    /**
//     * code : 0
//     * msg : success
//     * data : {"ucode":988,"uauth":"8cb9066b199961ef"}
//     * append : []
//     */
//
//    private int code;
//    private String msg;
//    /**
//     * ucode : 988
//     * uauth : 8cb9066b199961ef
//     */
//
//    private DataBean data;
//    private List<?> append;
//
//    public int getCode() {
//        return code;
//    }
//
//    public void setCode(int code) {
//        this.code = code;
//    }
//
//    public String getMsg() {
//        return msg;
//    }
//
//    public void setMsg(String msg) {
//        this.msg = msg;
//    }
//
//    public DataBean getData() {
//        return data;
//    }
//
//    public void setData(DataBean data) {
//        this.data = data;
//    }
//
//    public List<?> getAppend() {
//        return append;
//    }
//
//    public void setAppend(List<?> append) {
//        this.append = append;
//    }
//
//    public static class DataBean {
//        private int ucode;
//        private String uauth;
//
//        public int getUcode() {
//            return ucode;
//        }
//
//        public void setUcode(int ucode) {
//            this.ucode = ucode;
//        }
//
//        public String getUauth() {
//            return uauth;
//        }
//
//        public void setUauth(String uauth) {
//            this.uauth = uauth;
//        }
//    }
}
