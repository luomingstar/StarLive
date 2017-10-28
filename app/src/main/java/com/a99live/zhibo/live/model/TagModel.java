package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by fuyk on 2016/11/25.
 */

public class TagModel {


    /**
     * code : 0
     * msg : success
     * data : [{"id":"1","name":"相声","content":["woshi","qewwq"]},{"id":"2","name":"曲艺","content":["zxjhvkl","qewjhrk"]},{"id":"3","name":"网红","content":["opipipipi","kljlkjlk"]},{"id":"4","name":"小鲜肉","content":["ppoppopopo","nnbnbnbnbn"]},{"id":"5","name":"逗逼","content":["uuuuuuu","dddddd"]},{"id":"6","name":"女神","content":["ttttt","ccccc"]},{"id":"7","name":"健身","content":["rrrrrrr","ggggc"]}]
     * append : []
     */

    private int code;
    private String msg;
    /**
     * id : 1
     * name : 相声
     * content : ["woshi","qewwq"]
     */

    private List<DataBean> data;
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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public List<?> getAppend() {
        return append;
    }

    public void setAppend(List<?> append) {
        this.append = append;
    }

    public static class DataBean {
        private String id;
        private String name;
        private List<String> content;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getContent() {
            return content;
        }

        public void setContent(List<String> content) {
            this.content = content;
        }
    }
}
