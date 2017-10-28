package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by fuyk on 2016/9/27.
 */
public class UploadModel {

    /**
     * code : 0
     * msg : success
     * data : {"sign":"lwkh1zCoC8+qbB8nUHG5p9rYFiFhPTEwMDYzMTE2JmI9OTlsaXZlJms9QUtJRGRWc0k4aHYza3ZrOUs1dnByaGdRUDAyTlU1cmRRVVpBJmU9MTQ3NDk2NDY1NiZ0PTE0NzQ5NjQ1OTYmcj0xMTQyNjgzMzM5JnU9MCZmPTcxODYwNGE1OWQxOGJjYjk1YzBkYzk2OTRiMjc0NmQ3","bucket":"99live","fileId":"718604a59d18bcb95c0dc9694b2746d7"}
     * append : []
     */

    private int code;
    private String msg;
    /**
     * sign : lwkh1zCoC8+qbB8nUHG5p9rYFiFhPTEwMDYzMTE2JmI9OTlsaXZlJms9QUtJRGRWc0k4aHYza3ZrOUs1dnByaGdRUDAyTlU1cmRRVVpBJmU9MTQ3NDk2NDY1NiZ0PTE0NzQ5NjQ1OTYmcj0xMTQyNjgzMzM5JnU9MCZmPTcxODYwNGE1OWQxOGJjYjk1YzBkYzk2OTRiMjc0NmQ3
     * bucket : 99live
     * fileId : 718604a59d18bcb95c0dc9694b2746d7
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
        private String fileId;

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

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }
    }
}
