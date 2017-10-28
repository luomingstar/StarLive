package com.a99live.zhibo.live.net;

import android.text.TextUtils;

import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;

/**
 * Created by fuyk on 2016/8/24.
 */
public class LiveHttpUrl {
    //http：api.test.i999d.cn ; m.test.i999d.cn
    //https: api.test.99zhibo.cc; m.test.99zhibo.cc
//    public static String domain = TCConstants.isTest ? "api.test.i999d.cn" : "api.live.i999d.cn";
    public static String domain = TCConstants.isTest ? "api.test.99zhibo.cc" : "api.99zhibo.cc";
    public static String port = "443";
    public static String h5Domain = TCConstants.isTest ? "m.test.i999d.cn" : "m.live.i999d.cn";
    private static String h5Port = "443";

    private static boolean IS_DEBUG = false;

    private static final String ROOT_URL = "https://";   //60.205.167.55";
    private static final String DEBUG_ROOT_URL = "https://60.205.167.55";
    private static final String H5_URL = "http://";

    public static String getH5Url(){
        String s = getH5UrlString();
        return s;
    }

    public static String getRootUrl() {
        String s = getOnlineConfig();
        return IS_DEBUG ? DEBUG_ROOT_URL : ROOT_URL + s;
    }

    /**
     * 这个方法适用于在请求web view的时候判断接口是不是本地的域名
     * @return
     */
    public static String getIsOurUrl(){
        String s = getOnlineConfigNoPort();
        return ROOT_URL + s;
    }

    /**
     * 这个方法适用于在请求web view的时候判断接口是不是本地的域名
     * @return
     */
    public static String getIsOurUrlWithHttp(){
        String s = getOnlineConfigNoPort();
        return "http://" + s;
    }

    public static void setH5DomainPort(String d ,String p){
        h5Domain = d;
        h5Port = p;
    }

    public static void setDomainPort(String d ,String p){
        domain = d;
        port = p;
    }

    private static String getH5UrlString(){

        if (!TextUtils.isEmpty(h5Domain)){
            return h5Domain + ":" + h5Port;
        }

        String domain = SPUtils.getString(SPUtils.H5_DOMAIN);
        String value = "m.live.i999d.cn";
        if (TextUtils.isEmpty(domain)){

        }else{
            String port = SPUtils.getString(SPUtils.H5_PORT);
            value = domain + ":" + port;
        }
        return value;

    }

    private static String getOnlineConfig(){
        if (domain != null && !"".equals(domain)){
            return domain + ":" + port;
        }

        String domain = SPUtils.getString(SPUtils.API_DOMAIN);
        String value = "api.99zhibo.cc";
        if (domain != null && !"".equals(domain)){
            String port = SPUtils.getString(SPUtils.API_PORT);
            value = domain + ":" + port;
        }
        return value;
    }

    private static String getOnlineConfigNoPort(){
        if (domain != null && !"".equals(domain)){
            return domain ;
        }

        String domain = SPUtils.getString(SPUtils.API_DOMAIN);
        String value = "api.99zhibo.cc";
        if (domain != null && !"".equals(domain)){
//            String port = SPUtils.getString(SPUtils.API_PORT);
            value = domain;
        }
        return value;
    }
}
