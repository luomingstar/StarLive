package com.a99live.zhibo.live.event;

/**
 * Created by JJGCW on 2016/12/20.
 */

public class ShutUpEvent {
    String identity;
    String name;

    public ShutUpEvent(String identity, String name) {
        this.identity = identity;
        this.name = name;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
