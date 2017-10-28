package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by fuyk on 2016/9/7.
 */
public class CosSignInfo {
    /**
     * code : 0
     * msg : success
     * data : {"sign":"9VrunZcgUDZs1LCJWOlabTqC+EhhPTEwMDYzMTE2Jms9QUtJRGRWc0k4aHYza3ZrOUs1dnByaGdRUDAyTlU1cmRRVVpBJmU9MTQ3MzI0MjYxNSZ0PTE0NzMyNDI1NTUmcj0xMTEyNjMzMDIwJmY9JmI9OTl6aGlib3lpZG9uZ2R1YW4=","bucket":"99zhiboyidongduan","cosFileName":"head20160907180235","directory":"/head","insertOnly":"YES"}
     * append : []
     */

    private int code;
    private String msg;
    /**
     * sign : 9VrunZcgUDZs1LCJWOlabTqC+EhhPTEwMDYzMTE2Jms9QUtJRGRWc0k4aHYza3ZrOUs1dnByaGdRUDAyTlU1cmRRVVpBJmU9MTQ3MzI0MjYxNSZ0PTE0NzMyNDI1NTUmcj0xMTEyNjMzMDIwJmY9JmI9OTl6aGlib3lpZG9uZ2R1YW4=
     * bucket : 99zhiboyidongduan
     * cosFileName : head20160907180235
     * directory : /head
     * insertOnly : YES
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
        private String sign;
        private String bucket;
        private String cosFileName;
        private String directory;
        private String insertOnly;

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getCosFileName() {
            return cosFileName;
        }

        public void setCosFileName(String cosFileName) {
            this.cosFileName = cosFileName;
        }

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }

        public String getInsertOnly() {
            return insertOnly;
        }

        public void setInsertOnly(String insertOnly) {
            this.insertOnly = insertOnly;
        }
    }


//    private String sign;
//    private String bucket;
//    private String cosFileName;
//    private String directory;
//    private String insertOnly;
//
//    private static CosSignInfo ourInstance = new CosSignInfo();
//
//    public static CosSignInfo getInstance() {
//
//        return ourInstance;
//    }
//
//    public String getSign() {
//        return sign;
//    }
//
//    public void setSign(String sign) {
//        this.sign = sign;
//    }
//
//    public String getBucket() {
//        return bucket;
//    }
//
//    public void setBucket(String bucket) {
//        this.bucket = bucket;
//    }
//
//    public String getCosFileName() {
//        return cosFileName;
//    }
//
//    public void setCosFileName(String cosFileName) {
//        this.cosFileName = cosFileName;
//    }
//
//    public String getDirectory() {
//        return directory;
//    }
//
//    public void setDirectory(String directory) {
//        this.directory = directory;
//    }
//
//    public String getInsertOnly() {
//        return insertOnly;
//    }
//
//    public void setInsertOnly(String insertOnly) {
//        this.insertOnly = insertOnly;
//    }


}
