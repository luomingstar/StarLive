package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by wcy09 on 2016/9/6.
 */
public class HotListLive {


    /**
     * code : 0
     * msg : success
     * data : [{"room_id":"135","room_title":"123","online_num":"0","anchor_id":"3","lng":"116.356896","lat":"40.042827","ip":"59.109.32.75","addr":"北京市","bizid":"4217","live_code":"99_3_201609062121172766","share_count":"0","in_total":"0","bg_img_url":"asgdkjsdgkfsa","create_time":"2016-09-06 21:21:17","update_time":"2016-09-06 21:21:17","nick_name":"天边的云","ucode":962,"type":"Z"},{"room_id":"134","room_title":"123","online_num":"0","anchor_id":"3","lng":"116.356896","lat":"40.042827","ip":"59.109.32.75","addr":"北京市","bizid":"4217","live_code":"99_3_201609062121146402","share_count":"0","in_total":"0","bg_img_url":"asgdkjsdgkfsa","create_time":"2016-09-06 21:21:14","update_time":"2016-09-06 21:21:14","nick_name":"天边的云","ucode":962,"type":"Z"},{"room_id":"133","room_title":"123","online_num":"0","anchor_id":"3","lng":"116.356896","lat":"40.042827","ip":"59.109.32.75","addr":"北京市","bizid":"4217","live_code":"99_3_201609062120397167","share_count":"0","in_total":"0","bg_img_url":"asgdkjsdgkfsa","create_time":"2016-09-06 21:20:39","update_time":"2016-09-06 21:20:39","nick_name":"天边的云","ucode":962,"type":"Z"}]
     * append : []
     */

    private int code;
    private String msg;
    /**
     * room_id : 135
     * room_title : 123
     * online_num : 0
     * anchor_id : 3
     * lng : 116.356896
     * lat : 40.042827
     * ip : 59.109.32.75
     * addr : 北京市
     * bizid : 4217
     * live_code : 99_3_201609062121172766
     * share_count : 0
     * in_total : 0
     * bg_img_url : asgdkjsdgkfsa
     * create_time : 2016-09-06 21:21:17
     * update_time : 2016-09-06 21:21:17
     * nick_name : 天边的云
     * ucode : 962
     * type : Z
     */

    private List<HotLiveItem> data;
    private List<?> append;

    @Override
    public String toString() {
        return "HotListLive{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", append=" + append +
                '}';
    }

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

    public List<HotLiveItem> getData() {
        return data;
    }

    public void setData(List<HotLiveItem> data) {
        this.data = data;
    }

    public List<?> getAppend() {
        return append;
    }

    public void setAppend(List<?> append) {
        this.append = append;
    }

    public static class HotLiveItem {
        private String id;
        private String room_id;
        private String room_title;
        private String online_num;
        private String anchor_id;
        private String lng;
        private String lat;
        private String ip;
        private String addr;
        private String bizid;
        private String live_code;
        private String share_count;
        private String in_total;
        private String bg_img_url;
        private String create_time;
        private String update_time;
        private String group_id;
        private String nick_name;
        private int ucode;
        private String number99;
        private String type;

        public String getNumber99() {
            return number99;
        }

        public void setNumber99(String number99) {
            this.number99 = number99;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }

        public String getRoom_title() {
            return room_title;
        }

        public void setRoom_title(String room_title) {
            this.room_title = room_title;
        }

        public String getOnline_num() {
            return online_num;
        }

        public void setOnline_num(String online_num) {
            this.online_num = online_num;
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

        public String getShare_count() {
            return share_count;
        }

        public void setShare_count(String share_count) {
            this.share_count = share_count;
        }

        public String getIn_total() {
            return in_total;
        }

        public void setIn_total(String in_total) {
            this.in_total = in_total;
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

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public int getUcode() {
            return ucode;
        }

        public void setUcode(int ucode) {
            this.ucode = ucode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "room_id='" + room_id + '\'' +
                    ", room_title='" + room_title + '\'' +
                    ", online_num='" + online_num + '\'' +
                    ", anchor_id='" + anchor_id + '\'' +
                    ", lng='" + lng + '\'' +
                    ", lat='" + lat + '\'' +
                    ", ip='" + ip + '\'' +
                    ", addr='" + addr + '\'' +
                    ", bizid='" + bizid + '\'' +
                    ", live_code='" + live_code + '\'' +
                    ", share_count='" + share_count + '\'' +
                    ", in_total='" + in_total + '\'' +
                    ", bg_img_url='" + bg_img_url + '\'' +
                    ", create_time='" + create_time + '\'' +
                    ", update_time='" + update_time + '\'' +
                    ", nick_name='" + nick_name + '\'' +
                    ", ucode=" + ucode +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
