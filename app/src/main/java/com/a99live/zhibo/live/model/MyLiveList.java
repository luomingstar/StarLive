package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * 我的直播列表
 * Created by fuyk on 2016/9/2.
 */
public class MyLiveList {


    /**
     * code : 0
     * msg : success
     * data : [{"room_id":"12","create_time":"2016-09-14 17:32:33","room_title":"99直播","online_num":"0","bg_img_url":"http://99zhiboyidongduan-10063116.file.myqcloud.comhttp://99zhiboyidongduan-10063116.file.myqcloud.com/head/head20160912143941.jpg","video_url":"[\"http:\\/\\/200026474.vod.myqcloud.com\\/200026474_de4a99bf893b4d4e8131925c8f38c218.f0.flv\"]","user_id":"5","nickname":"久久16670","ucode":988,"is_live":0,"format_time":"刚刚"}]
     * append : []
     */

    private int code;
    private String msg;
    /**
     * id : 284
     * room_id : 12
     * create_time : 2016-09-14 17:32:33
     * room_title : 99直播
     * online_num : 0
     * bg_img_url : http://99zhiboyidongduan-10063116.file.myqcloud.comhttp://99zhiboyidongduan-10063116.file.myqcloud.com/head/head20160912143941.jpg
     * video_url : ["http:\/\/200026474.vod.myqcloud.com\/200026474_de4a99bf893b4d4e8131925c8f38c218.f0.flv"]
     * user_id : 5
     * nickname : 久久16670
     * ucode : 988
     * is_live : 0
     * format_time : 刚刚
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
        private String room_id;
        private String create_time;
        private String room_title;
        private String online_num;
        private String bg_img_url;
        private String video_url;
        private String user_id;
        private String nickname;
        private int ucode;
        private int is_live;
        private String format_time;

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

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
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

        public String getBg_img_url() {
            return bg_img_url;
        }

        public void setBg_img_url(String bg_img_url) {
            this.bg_img_url = bg_img_url;
        }

        public String getVideo_url() {
            return video_url;
        }

        public void setVideo_url(String video_url) {
            this.video_url = video_url;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getUcode() {
            return ucode;
        }

        public void setUcode(int ucode) {
            this.ucode = ucode;
        }

        public int getIs_live() {
            return is_live;
        }

        public void setIs_live(int is_live) {
            this.is_live = is_live;
        }

        public String getFormat_time() {
            return format_time;
        }

        public void setFormat_time(String format_time) {
            this.format_time = format_time;
        }
    }
}
