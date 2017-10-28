package com.a99live.zhibo.live.activity.xiaovideo.fragment;

import com.trello.rxlifecycle.components.support.RxFragment;

/**
 * Created by JJGCW on 2017/4/12.
 */

public abstract class BaseVideoFragment extends RxFragment {

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
