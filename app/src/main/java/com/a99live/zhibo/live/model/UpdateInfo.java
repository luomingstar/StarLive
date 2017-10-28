package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by fuyk on 2016/9/30.
 */
public class UpdateInfo {


    /**
     * code : 0
     * msg : success
     * data : {"ios_url":"","and_url":"http://99zhiboyidongduan-10063116.file.myqcloud.com/admin/txt/b268117f7aa1c29eeaded4683992e0e8.txt","win_url":"","vnum":"1.0.0","force_update":true,"desc":"第一个版本"}
     * append : []
     */

    private int code;
    private String msg;
    /**
     * ios_url :
     * and_url : http://99zhiboyidongduan-10063116.file.myqcloud.com/admin/txt/b268117f7aa1c29eeaded4683992e0e8.txt
     * win_url :
     * vnum : 1.0.0
     * force_update : true
     * desc : 第一个版本
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
        private String ios_url;
        private String and_url;
        private String win_url;
        private String vnum;
        private boolean force_update;
        private String desc;

        public String getIos_url() {
            return ios_url;
        }

        public void setIos_url(String ios_url) {
            this.ios_url = ios_url;
        }

        public String getAnd_url() {
            return and_url;
        }

        public void setAnd_url(String and_url) {
            this.and_url = and_url;
        }

        public String getWin_url() {
            return win_url;
        }

        public void setWin_url(String win_url) {
            this.win_url = win_url;
        }

        public String getVnum() {
            return vnum;
        }

        public void setVnum(String vnum) {
            this.vnum = vnum;
        }

        public boolean isForce_update() {
            return force_update;
        }

        public void setForce_update(boolean force_update) {
            this.force_update = force_update;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
