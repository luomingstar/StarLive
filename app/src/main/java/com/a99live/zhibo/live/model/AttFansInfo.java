package com.a99live.zhibo.live.model;

/**
 * Created by fuyk on 2016/9/2.
 */
public class AttFansInfo {

    private int userId;
    private String nickName;
    private String avatar;
    private int ucode;

    public int getUser_id() {
        return userId;
    }

    public void setUser_id(int user_id) {
        this.userId = user_id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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
