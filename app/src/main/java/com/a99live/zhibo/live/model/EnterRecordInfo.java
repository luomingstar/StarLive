package com.a99live.zhibo.live.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fuyk on 2016/9/12.
 */
public class EnterRecordInfo implements Serializable {


    /**
     * code : 0
     * msg : success
     * data : {"id":"148","room_id":"145","room_title":"输入直播主题可吸引更多观众","online_num":"2","anchor_id":"3","lng":"116.356896","lat":"40.042827","ip":"59.109.32.75","addr":"北京市","bizid":"4217","live_code":"4217_99_3_201609131941136828","share_count":"0","in_total":"3","video_url":"[\"http:\\/\\/200026474.vod.myqcloud.com\\/200026474_5d0350d7595b40428bc4bad6ecc35956.f0.flv\"]","bg_img_url":"/head/head20160909223600.png","start_time":"2016-09-13 19:41:13","end_time":"2016-09-13 19:43:23","create_time":"2016-09-13 19:43:23","update_time":"2016-09-13 19:43:23","can_show":"1","group_id":"@TGS#aISXY6BE2","nick_name":"bbbbb","head_image":"http://99zhiboyidongduan-10063116.file.myqcloud.com/head/head20160909223600.png","playList":["http://200026474.vod.myqcloud.com/200026474_5d0350d7595b40428bc4bad6ecc35956.f0.flv"]}
     * append : []
     */

    public int code;
    public String msg;
    /**
     * id : 148
     * room_id : 145
     * room_title : 输入直播主题可吸引更多观众
     * online_num : 2
     * anchor_id : 3
     * lng : 116.356896
     * lat : 40.042827
     * ip : 59.109.32.75
     * addr : 北京市
     * bizid : 4217
     * live_code : 4217_99_3_201609131941136828
     * share_count : 0
     * in_total : 3
     * video_url : ["http:\/\/200026474.vod.myqcloud.com\/200026474_5d0350d7595b40428bc4bad6ecc35956.f0.flv"]
     * bg_img_url : /head/head20160909223600.png
     * start_time : 2016-09-13 19:41:13
     * end_time : 2016-09-13 19:43:23
     * create_time : 2016-09-13 19:43:23
     * update_time : 2016-09-13 19:43:23
     * can_show : 1
     * group_id : @TGS#aISXY6BE2
     * nick_name : bbbbb
     * head_image : http://99zhiboyidongduan-10063116.file.myqcloud.com/head/head20160909223600.png
     * playList : ["http://200026474.vod.myqcloud.com/200026474_5d0350d7595b40428bc4bad6ecc35956.f0.flv"]
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

    public static class DataBean implements Serializable{
        public String id;
        public String room_id;
        public String room_title;
        public String online_num;
        public String anchor_id;
        public String lng;
        public String lat;
        public String ip;
        public String addr;
        public String bizid;
        public String live_code;
        public String share_count;
        public String in_total;
        public String video_url;
        public String bg_img_url;
        public String start_time;
        public String end_time;
        public String create_time;
        public String update_time;
        public String can_show;
        public String group_id;
        public String nick_name;
        public String head_image;
        public List<String> playList;
        public String is_follow;

        public String getIs_follow() {
            return is_follow;
        }

        public void setIs_follow(String is_follow) {
            this.is_follow = is_follow;
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

        public String getVideo_url() {
            return video_url;
        }

        public void setVideo_url(String video_url) {
            this.video_url = video_url;
        }

        public String getBg_img_url() {
            return bg_img_url;
        }

        public void setBg_img_url(String bg_img_url) {
            this.bg_img_url = bg_img_url;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
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

        public String getCan_show() {
            return can_show;
        }

        public void setCan_show(String can_show) {
            this.can_show = can_show;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public String getHead_image() {
            return head_image;
        }

        public void setHead_image(String head_image) {
            this.head_image = head_image;
        }

        public List<String> getPlayList() {
            return playList;
        }

        public void setPlayList(List<String> playList) {
            this.playList = playList;
        }
    }
}
