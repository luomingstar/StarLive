package com.a99live.zhibo.live;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by fuyk on 2016/8/24.
 */
public class LiveZhiBoApplication extends MultiDexApplication {

    private static Application mApp;
    private static int mTid;
    private static Handler mHandler;
    public static Activity mHomeAct;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        /**初始化友盟分享*/
        UMShareAPI.get(this);
        init();
        initUmeng();
    }

    private void init() {
        context = this;
        mApp = this;
        mTid = android.os.Process.myTid();
        mHandler = new Handler();
    }

    private void initUmeng() {
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //这个不能打开，否则无法统计页面路径
//        MobclickAgent.openActivityDurationTrack(false); //false 关闭自动统计  true 打开
//        MobclickAgent.setCatchUncaughtExceptions(false);、、关闭错误统计  不允许关闭
    }

    {
        /**微信*/
        PlatformConfig.setWeixin("wx8af34fd3de90a8f9", "9207acc60a5406647b073843cde37201");
        /**QQ和QQ空间*/
        PlatformConfig.setQQZone("1105694258", "KWFRc0HnvvST5E6l");
        /**新浪微博*/
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad");
    }

    public static int getMainThreadId(){ return mTid; }

    public static Handler getHandler(){ return mHandler; }

    public static Application getApp(){ return mApp; }

    public static Context getContext(){return context;}
}
