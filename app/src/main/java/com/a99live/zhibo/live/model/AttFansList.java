package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by fuyk on 2016/9/2.
 */
public class AttFansList {

    /**
     * code : 0
     * msg : success
     * data : [{"user_id":"2","nickname":"九九16667","avatar":"","ucode":949}]
     * append : []
     */

    private int code;
    private String msg;
    /**
     * user_id : 2
     * nickname : 九九16667
     * avatar :
     * ucode : 949
     */

    private List<FriendInfo> data;
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

    public List<FriendInfo> getData() {
        return data;
    }

    public void setData(List<FriendInfo> data) {
        this.data = data;
    }

    public List<?> getAppend() {
        return append;
    }

    public void setAppend(List<?> append) {
        this.append = append;
    }

    public static class FriendInfo {
        private String user_id;
        private String nickname;
        private String avatar;
        private String region;
        private int ucode;

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getUcode() {
            return ucode;
        }

        public void setUcode(int ucode) {
            this.ucode = ucode;
        }
    }
}
