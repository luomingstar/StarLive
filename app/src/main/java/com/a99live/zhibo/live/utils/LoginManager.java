package com.a99live.zhibo.live.utils;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.activity.LoginActivity;
import com.a99live.zhibo.live.activity.imchatc2c.FriendshipEvent;
import com.a99live.zhibo.live.activity.imchatc2c.MessageEvent;
import com.a99live.zhibo.live.activity.imchatc2c.RefreshEvent;
import com.a99live.zhibo.live.model.FriendshipInfo;
import com.a99live.zhibo.live.presenter.LoginHelper;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by JJGCW on 2016/10/21.
 */

public class LoginManager {

    /**
     * 需要关闭当前页面
     * @param erreJson
     */
    public static void loginErre(String erreJson , Activity ac ,boolean isFinish){
        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(erreJson);
        if (listMapByJson.size()>0){
            Map<String, String> map = listMapByJson.get(0);
            int code = Integer.parseInt(map.get("code"));
            if (code == 20000006) {
                Toast.makeText(LiveZhiBoApplication.getApp(), "房间未找到，请刷新列表", Toast.LENGTH_SHORT).show();
//                onRefresh();
            } else if (code == 111111110 || code == 11111113) {
                goLoginActivity(ac);
            } else if (code == 11111122) {
                UIUtils.showToast("在别处登录，请重新登录");
                goLoginActivity(ac);
                clearUser();
                if (isFinish){
                    ac.finish();
                }
            }
        }
    }

    public static boolean getCodeIsOk(String erreJson,Activity ac ,boolean isFinish){
        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(erreJson);
        if (listMapByJson.size()>0){
            Map<String, String> map = listMapByJson.get(0);
            int code = Integer.parseInt(map.get("code"));
            if (code == 0){
                return true;
            }
            if (code == 20000006) {
                Toast.makeText(LiveZhiBoApplication.getApp(), "房间未找到，请刷新列表", Toast.LENGTH_SHORT).show();
            } else if (code == 111111110 || code == 11111113) {
                goLoginActivity(ac);
            } else if (code == 11111122) {
                UIUtils.showToast("在别处登录，请重新登录");
                clearUser();
                goLoginActivity(ac);
                if (isFinish){
                    ac.finish();
                }
            }else{
                UIUtils.showToast(map.get("msg"));
            }
        }else{
            UIUtils.showToast(erreJson);
        }
        return false;
    }

    public static void loginErreCode(String e,Activity a, boolean isFinish){
        int code = Integer.parseInt(e);
        if (code == 20000006) {
            Toast.makeText(LiveZhiBoApplication.getApp(), "房间未找到，请刷新列表", Toast.LENGTH_SHORT).show();
//                onRefresh();
        } else if (code == 111111110 || code == 11111113) {
            goLoginActivity(a);
        } else if (code == 11111122) {
            UIUtils.showToast("在别处登录，请重新登录");
            clearUser();
            goLoginActivity(a);
            if (isFinish) {
                a.finish();
            }
        }
    }

    /**
     * 进入登录页
     */
    public static void goLoginActivity(Activity b){
        b.startActivity(new Intent(b, LoginActivity.class));
    }

    /**
     * 清空登陆数据
     */
    public static void clearUser(){
        //登出im防止登录其他号之后im还是以前的人；
        //注册消息监听
        MessageEvent.getInstance().deleteObservers();
        //注册刷新监听
        RefreshEvent.getInstance().deleteObservers();
        //注册好友关系链监听
        FriendshipEvent.getInstance().deleteObservers();
//        //注册群关系监听
//        GroupEvent.getInstance().deleteObservers();
        LoginHelper.getInstance().imLoginOut();
        MessageEvent.getInstance().clear();
        FriendshipInfo.getInstance().clear();
        SPUtils.putString(SPUtils.USER_CODE, "");
        SPUtils.putString(SPUtils.USER_UAUTH, "");
        SPUtils.putString(SPUtils.IMSIG, "");
        SPUtils.putString(SPUtils.IMUID, "");
    }
}

