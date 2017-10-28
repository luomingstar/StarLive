package com.a99live.zhibo.live.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fuyk on 2016/9/12.
 */
public class StartLiveInfo implements Serializable{


    /**
     * code : 0
     * msg : success
     * data : {"room_title":"输入直播主题可吸引更多观众","anchor_id":"5","lng":"39.975788","lat":"116.344405","ip":"59.109.32.75","addr":"","bizid":"4217","live_code":"4217_99_5_201609121544477439","bg_img_url":"/head/head20160912143941.jpg","create_time":"2016-09-12 15:44:47","update_time":"2016-09-12 15:44:47","room_id":"65","pushPath":"rtmp://4217.livepush.myqcloud.com/99zhibo/4217_99_5_201609121544477439?bizid=4217&txSecret=d7aeb20969ba38cf897c8f5ac453fbdc&txTime=57d7ae6f"}
     * append : []
     */

    public int code;
    public String msg;
    /**
     * room_title : 输入直播主题可吸引更多观众
     * anchor_id : 5
     * lng : 39.975788
     * lat : 116.344405
     * ip : 59.109.32.75
     * addr :
     * bizid : 4217
     * live_code : 4217_99_5_201609121544477439
     * bg_img_url : /head/head20160912143941.jpg
     * create_time : 2016-09-12 15:44:47
     * update_time : 2016-09-12 15:44:47
     * room_id : 65
     * pushPath : rtmp://4217.livepush.myqcloud.com/99zhibo/4217_99_5_201609121544477439?bizid=4217&txSecret=d7aeb20969ba38cf897c8f5ac453fbdc&txTime=57d7ae6f
     */

    public DataBean data;
    public List<?> append;

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

    public static class DataBean  implements Serializable{
        public String room_title;
        public String anchor_id;
        public String lng;
        public String lat;
        public String ip;
        public String addr;
        public String bizid;
        public String live_code;
        public String bg_img_url;
        public String create_time;
        public String update_time;
        public String room_id;
        public String pushPath;

        public String getRoom_title() {
            return room_title;
        }

        public void setRoom_title(String room_title) {
            this.room_title = room_title;
        }

        public String getAnchor_id() {
            return anchor_id;
        }

        public void setAnchor_id(String anchor_id) {
            this.anchor_id = anchor_id;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getBizid() {
            return bizid;
        }

        public void setBizid(String bizid) {
            this.bizid = bizid;
        }

        public String getLive_code() {
            return live_code;
        }

        public void setLive_code(String live_code) {
            this.live_code = live_code;
        }

        public String getBg_img_url() {
            return bg_img_url;
        }

        public void setBg_img_url(String bg_img_url) {
            this.bg_img_url = bg_img_url;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }

        public String getPushPath() {
            return pushPath;
        }

        public void setPushPath(String pushPath) {
            this.pushPath = pushPath;
        }
    }
}
