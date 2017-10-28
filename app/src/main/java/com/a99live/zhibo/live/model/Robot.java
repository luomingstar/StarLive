package com.a99live.zhibo.live.model;

/**
 * Created by JJGCW on 2016/11/22.
 */

public class Robot {

    private String nickname;
    private String identity;
    private String mobile;
    private String gender;
    private String avatar;
    private String user_id;
    private String language;
    private String action;
    private String full_head_image;
    private String level;

    public Robot() {
        level = "1";
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getFull_head_image() {
        return full_head_image;
    }

    public void setFull_head_image(String full_head_image) {
        this.full_head_image = full_head_image;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIdentity() {
        return identity;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

//    public String getAvatar() {
//        return avatar;
//    }
//
//    public void setAvatar(String avatar) {
//        this.avatar = avatar;
//    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
