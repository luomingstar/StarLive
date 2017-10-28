package com.a99live.zhibo.live.fragment.ListLive.Channel;

import com.trello.rxlifecycle.components.support.RxFragment;

/**
 * Created by fuyk on 2016/12/23.
 */

public abstract class BaseChannelFragment extends RxFragment {

    protected boolean isVisible;
    protected boolean isPrepared;
    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible(){
        preLoad();
    }
    protected abstract void preLoad();
    protected void onInvisible(){}
}
