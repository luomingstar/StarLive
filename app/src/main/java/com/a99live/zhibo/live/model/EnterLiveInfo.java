package com.a99live.zhibo.live.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fuyk on 2016/9/12.
 */
public class EnterLiveInfo implements Serializable {


    /**
     * code : 0
     * msg : success
     * data : {"room_id":"107","room_title":"输入直播主题可吸引更多观众","online_num":"3","anchor_id":"3","lng":"116.356896","lat":"40.042827","ip":"59.109.32.75","addr":"北京市","group_id":"(null)","live_code":"4217_99_3_201609121923076714","share_count":"0","in_total":"3","bg_img_url":"http://99zhiboyidongduan-10063116.file.myqcloud.com/head/head20160909223600.png","create_time":"2016-09-12 19:23:07","update_time":"2016-09-12 19:23:07","bizid":"4217","nick_name":"bbbbb","playList":{"rtmp":"rtmp://4217.liveplay.myqcloud.com/99zhibo/4217_99_3_201609121923076714?txSecret=15ea9f4b536b3471f52133486a0e19d9&txTime=57d7f0cb","flv":"http://4217.liveplay.myqcloud.com/99zhibo/4217_99_3_201609121923076714.flv?txSecret=15ea9f4b536b3471f52133486a0e19d9&txTime=57d7f0cb","hls":"http://4217.liveplay.myqcloud.com/99zhibo/4217_99_3_201609121923076714.m3u8?txSecret=15ea9f4b536b3471f52133486a0e19d9&txTime=57d7f0cb"}}
     * append : []
     */

    public int code;
    public String msg;
    /**
     * room_id : 107
     * room_title : 输入直播主题可吸引更多观众
     * online_num : 3
     * anchor_id : 3
     * lng : 116.356896
     * lat : 40.042827
     * ip : 59.109.32.75
     * addr : 北京市
     * group_id : (null)
     * live_code : 4217_99_3_201609121923076714
     * share_count : 0
     * in_total : 3
     * bg_img_url : http://99zhiboyidongduan-10063116.file.myqcloud.com/head/head20160909223600.png
     * create_time : 2016-09-12 19:23:07
     * update_time : 2016-09-12 19:23:07
     * bizid : 4217
     * nick_name : bbbbb
     * playList : {"rtmp":"rtmp://4217.liveplay.myqcloud.com/99zhibo/4217_99_3_201609121923076714?txSecret=15ea9f4b536b3471f52133486a0e19d9&txTime=57d7f0cb","flv":"http://4217.liveplay.myqcloud.com/99zhibo/4217_99_3_201609121923076714.flv?txSecret=15ea9f4b536b3471f52133486a0e19d9&txTime=57d7f0cb","hls":"http://4217.liveplay.myqcloud.com/99zhibo/4217_99_3_201609121923076714.m3u8?txSecret=15ea9f4b536b3471f52133486a0e19d9&txTime=57d7f0cb"}
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

    public static class DataBean implements Serializable {
        public String room_id;
        public String room_title;
        public String online_num;
        public String anchor_id;
        public String lng;
        public String lat;
        public String ip;
        public String addr;
        public String group_id;
        public String live_code;
        public String share_count;
        public String in_total;
        public String bg_img_url;
        public String create_time;
        public String update_time;
        public String bizid;
        public String nick_name;
        public String is_follow;
        public String number99;
        /**
         * rtmp : rtmp://4217.liveplay.myqcloud.com/99zhibo/4217_99_3_201609121923076714?txSecret=15ea9f4b536b3471f52133486a0e19d9&txTime=57d7f0cb
         * flv : http://4217.liveplay.myqcloud.com/99zhibo/4217_99_3_201609121923076714.flv?txSecret=15ea9f4b536b3471f52133486a0e19d9&txTime=57d7f0cb
         * hls : http://4217.liveplay.myqcloud.com/99zhibo/4217_99_3_201609121923076714.m3u8?txSecret=15ea9f4b536b3471f52133486a0e19d9&txTime=57d7f0cb
         */

        public PlayListBean playList;

        public String getNumber99() {
            return number99;
        }

        public void setNumber99(String number99) {
            this.number99 = number99;
        }

        public String getIs_follow() {
            return is_follow;
        }

        public void setIs_follow(String is_follow) {
            this.is_follow = is_follow;
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

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
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

        public String getBizid() {
            return bizid;
        }

        public void setBizid(String bizid) {
            this.bizid = bizid;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public PlayListBean getPlayList() {
            return playList;
        }

        public void setPlayList(PlayListBean playList) {
            this.playList = playList;
        }

        public static class PlayListBean implements Serializable {
            public String rtmp;
            public String flv;
            public String hls;

            public String getRtmp() {
                return rtmp;
            }

            public void setRtmp(String rtmp) {
                this.rtmp = rtmp;
            }

            public String getFlv() {
                return flv;
            }

            public void setFlv(String flv) {
                this.flv = flv;
            }

            public String getHls() {
                return hls;
            }

            public void setHls(String hls) {
                this.hls = hls;
            }
        }
    }
}
