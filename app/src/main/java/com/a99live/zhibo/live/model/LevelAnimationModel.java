package com.a99live.zhibo.live.model;

/**
 * Created by JJGCW on 2017/3/27.
 */

public class LevelAnimationModel {


    private String level;
    private String avatar;
    private String name;
    private String identity;



    public LevelAnimationModel(String level, String avatar, String name,String identity) {
        this.level = level;
        this.avatar = avatar;
        this.name = name;
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
