package com.a99live.zhibo.live.event;

/**
 * Created by fuyk on 2016/12/9.
 */

public class FollowPlayerEvent {

    private boolean isFollow;

    public FollowPlayerEvent(boolean isFollow) {
        this.isFollow = isFollow;
    }

    public boolean isFollow() {
        return isFollow;
    }
}
