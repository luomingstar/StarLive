package com.a99live.zhibo.live.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.utils.UIUtils;


/**
 * 页面切换管理的Layout
 */
public class PageStateLayout extends FrameLayout {

    public interface PageStateCallBack {
        void onErrorClick();

        void onEmptyClick();
    }

    /**
     * 未知状态
     */
    public static final int STATE_UNKNOW = 0;
    /**
     * 正在加载状态
     */
    public static final int STATE_LOADING = 1;
    /**
     * 错误状态
     */
    public static final int STATE_ERROR = 2;
    /**
     * 空数据状态
     */
    public static final int STATE_EMPTY = 3;
    /**
     * 成功状态
     */
    public static final int STATE_SUCCEED = 4;

    private int mState = STATE_UNKNOW;
    private Context mContext;

    private PageStateCallBack mCallBack;

    private View mLoadingView;
    private View mErrorView;
    private View mEmptyView;
    private View mSucceedView;

    public PageStateLayout(Context context) {
        super(context);
        init(context);
    }

    public PageStateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PageStateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        this.mContext = context;
        /*
        *   android:clipToPadding="false"
        *   android:fitsSystemWindows="true"
        *   配合沉浸式通知栏
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setFitsSystemWindows(true);
            setClipToPadding(false);
        }

        setLayoutParams(new ViewGroup.LayoutParams(-1,-1));

        mLoadingView = initLoadingView();
        mEmptyView = initEmptyView();
        mErrorView = initErrorView();

        if (mLoadingView != null) {
            addView(mLoadingView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
        if (mEmptyView != null) {
            addView(mEmptyView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
        if (mErrorView != null) {
            addView(mErrorView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        showPage();
    }



    /**
     * 初始化错误界面
     */
    private View initErrorView() {
        View errorView = View.inflate(mContext, R.layout.loading_page_error, null);
        errorView.findViewById(R.id.rl_error).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallBack != null) {
                            mCallBack.onErrorClick();
                        }
                    }
                }
        );
        return errorView;
    }

    /**
     * 初始化空界面
     */
    private View initEmptyView() {
        View view = View.inflate(mContext, R.layout.layout_empty_list, null);
        view.findViewById(R.id.tv_search).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCallBack !=null){
                    mCallBack.onEmptyClick();
                }
            }
        });
        return view;
    }

    /**
     * 初始化加载中界面
     */
    private View initLoadingView() {
        return View.inflate(mContext, R.layout.loading_page_load, null);
    }

    /**
     * 更具状态显示界面
     */
    private void showPage() {
        UIUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updatePage();
            }
        });
    }

    private void updatePage() {
        if (null != mSucceedView) {
            mSucceedView.setVisibility(mState == STATE_SUCCEED ? View.VISIBLE : View.GONE);
        }
        if (null != mErrorView) {
            mErrorView.setVisibility(mState == STATE_ERROR ? View.VISIBLE : View.GONE);
        }
        if (null != mEmptyView) {
            mEmptyView.setVisibility(mState == STATE_EMPTY ? View.VISIBLE : View.GONE);
        }
        if (null != mLoadingView) {
            mLoadingView.setVisibility(mState == STATE_UNKNOW || mState == STATE_LOADING ? View.VISIBLE : View.GONE);
    }

}

    public void setPageState(int pageState) {
        this.mState = pageState;
        showPage();
    }

    public int  getCurrentPageState(){
        return mState;
    }

    public void setContentView(View view) {
        if (mSucceedView != null) {
            removeView(mSucceedView);
        }
        mSucceedView = view;
        mSucceedView.setVisibility(GONE);
        addView(mSucceedView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setErrorView(View view) {
        if (mErrorView != null) {
            removeView(mErrorView);
        }
        mErrorView = view;
        mErrorView.setVisibility(GONE);
        addView(mErrorView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setLoadView(View view) {
        if (mLoadingView != null) {
            removeView(mLoadingView);
        }
        mLoadingView = view;
        mLoadingView.setVisibility(GONE);
        addView(mLoadingView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }


    public void setEmptyView(View view) {
        if (mEmptyView != null) {
            removeView(mEmptyView);
        }
        mEmptyView = view;
        mEmptyView.setVisibility(GONE);
        addView(mEmptyView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCallBack !=null){
                    mCallBack.onEmptyClick();
                }
            }
        });
    }

    public void addCallBack(PageStateCallBack callBack) {
        this.mCallBack = callBack;
    }
}
