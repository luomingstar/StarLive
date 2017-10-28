package com.a99live.zhibo.live.fragment.ListLive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.event.EmptyClickEvent;
import com.a99live.zhibo.live.view.PageStateLayout;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by fuyk on 2016/9/9.
 */
public abstract class BaseFragment extends RxFragment {

    private PageStateLayout mPageStateLayout;

    protected boolean isVisible;
    protected  boolean isPrepared;

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
        initContentData();
    }
//    protected abstract void preLoad();
    protected void onInvisible(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mPageStateLayout == null) {
            mPageStateLayout = new PageStateLayout(getActivity());
            mPageStateLayout.setContentView(initContentView());
            Class<? extends BaseFragment> aClass = this.getClass();
            if (aClass.getSimpleName().equals("HotFragment") || aClass.getSimpleName().equals("NewFragment")){
                View emptyView = View.inflate(getActivity(), R.layout.layout_attention_empty, null);
                TextView tv_att_empty = (TextView) emptyView.findViewById(R.id.tv_att_empty);
                tv_att_empty.setText("主播都休息了，等会再来吧！");
                mPageStateLayout.setEmptyView(emptyView);
            }
            mPageStateLayout.addCallBack(new PageStateCallBack());
        } else {
            //避免引起挂载多个Parent
            ViewParent parent = mPageStateLayout.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mPageStateLayout);
            }
        }
        return mPageStateLayout;
    }



//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        initContentData();
//    }

    private class PageStateCallBack implements PageStateLayout.PageStateCallBack {
        @Override
        public void onErrorClick() {
            reLoading();
        }

        @Override
        public void onEmptyClick() {
            Class<? extends BaseFragment> aClass = BaseFragment.this.getClass();
            if (aClass.getSimpleName().equals("HotFragment") || aClass.getSimpleName().equals("NewFragment")){
//                View emptyView = View.inflate(getActivity(), R.layout.layout_attention_empty, null);
//                TextView tv_att_empty = (TextView) emptyView.findViewById(R.id.tv_att_empty);
//                tv_att_empty.setText("主播都休息了，等会再来吧！");
//                mPageStateLayout.setEmptyView(emptyView);
                reLoading();
            }else{
                EventBus.getDefault().post(new EmptyClickEvent());
            }
        }
    }

    private void  reLoading(){
        //页面出错，点击重新加载
        showPage(PageStateLayout.STATE_LOADING);
        isPrepared = true;
        initContentData();
    }

    protected void showPage(int pageState) {
        mPageStateLayout.setPageState(pageState);
    }

    protected abstract void initContentData();

    protected abstract View initContentView();

}
