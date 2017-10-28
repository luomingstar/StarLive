package com.a99live.zhibo.live.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by JJGCW on 2016/10/13.
 */

public class Tools {

    public static String map2Json(Map<String,String> map){
        JSONObject jsonObject = new JSONObject();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, String> entry = it.next();
            try {
                String value = "";
                if(entry.getValue() != null && !entry.getValue().equals("null"))
                    value = entry.getValue();
                jsonObject.put(entry.getKey(),value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    public static JSONObject map2JsonNoToString(Map<String,String> map){
        JSONObject jsonObject = new JSONObject();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, String> entry = it.next();
            try {
                String value = "";
                if(entry.getValue() != null && !entry.getValue().equals("null"))
                    value = entry.getValue();
                jsonObject.put(entry.getKey(),value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public static String list2Json(List<Map<String,String>> data){
        //添加JS回调代码
        JSONArray jsonArray = new JSONArray();
        for(Map<String,String> map : data){
            JSONObject jsonObject = new JSONObject();
            try {
                Iterator<Map.Entry<String,String>> it = map.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry<String,String> entry = it.next();
                    jsonObject.put(entry.getKey(), entry.getValue());
                }
            } catch (JSONException e) {
//                UtilLog.reportError("JSON生成异常", e);
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    public static JSONArray list2JsonArray(List<Map<String,String>> data){
        //添加JS回调代码
        JSONArray jsonArray = new JSONArray();
        for(Map<String,String> map : data){
            JSONObject jsonObject = new JSONObject();
            try {
                Iterator<Map.Entry<String,String>> it = map.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry<String,String> entry = it.next();
                    jsonObject.put(entry.getKey(), entry.getValue());
                }
            } catch (JSONException e) {
//                UtilLog.reportError("JSON生成异常", e);
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }


    /**
     * 验证手机格式
     */
    public static boolean isMobile(String number) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String num = "[1][3578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }

    /**
     * 检测该包名在手机中的状态;
     * @param  ;
     * @param packageName 完整的包名;
     * @return 状态标志 0-未安装，1-已安装，2-运行在后台，3-当前运行
     */
    public static int isAppInPhone(Context context, String packageName) {
        int res = 0;
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Exception e) {
            return res;
        }
        res = 1;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 该包名是否在当前运行;isTop1就可以了;
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : tasksInfo) {
            if (info.baseActivity.getPackageName().equals(packageName))
                res = 2;
            if (info.topActivity.getPackageName().equals(packageName)) {
                res = 3;
                break;
            }
        }
        return res;
    }

    public static int getDrawableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "drawable", paramContext.getPackageName());
    }
}
