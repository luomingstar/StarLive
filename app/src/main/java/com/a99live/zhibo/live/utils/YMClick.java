package com.a99live.zhibo.live.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 友盟
 * Created by JJGCW on 2016/10/24.
 */

public class YMClick {
    //开始直播
    public static final String STRAT_LIVE = "start_live";
    //送礼物
    public static final String SEND_GIFT = "send_gift";
    //充值
    public static final String PAY = "pay";
    //广场热门关注的头像统计
    public static final String PAGE_HEAD_IMG = "page_head_img";
    //    热门标签里主播头像：hotpage_head_img
//    广场标签里的主播头像：newpage_head_img
//    关注标签里的主播头像：focuspage_head_img
    //广场
    public static final String NEWPAGE_HEAD_IMG = "newpage_head_img";
    //热门
    public static final String HOTPAGE_HEAD_IMG = "hotpage_head_img";
    //关注
    public static final String FOCUSPAGE_HEAD_IMG = "focuspage_head_img";



    /**
     * 计算事件统计
     * @param context	上下文
     * @param eventID	事件ID
     * @param map_key
     * @param map_value
     * @param value	数值
     */
    public static void onEventValue(Context context,String eventID,String map_key,String map_value,int value){
        if(isStatistics(context)){
            Map<String, String> map = new HashMap<String, String>();
            map.put(map_key, map_value);
            MobclickAgent.onEventValue(context, eventID, map, value);
//			StringManager.print("i", "统计_计算_eventID="+eventID+"_key="+map_key+"_value="+map_value+"_数值="+value);
//			showToast(context, "统计_计算_eventID="+eventID+"_key="+map_key+"_value="+map_value+"_数值="+value);
        }
    }

    /**
     * 计数事件统计	(有map)
     * @param context	上下文
     * @param eventID	事件ID
     * @param map_key
     * @param map_value
     */
    public static void onEvent(Context context,String eventID,String map_key,String map_value){
        if(isStatistics(context)){
            Map<String, String> map = new HashMap<String, String>();
            map.put(map_key, map_value);
            MobclickAgent.onEvent(context, eventID, map);
//			showToast(context, "统计_计数_eventID="+eventID+"_key="+map_key+"_value="+map_value);
        }
    }

    /**
     * 计数事件统计	(无map)
     * @param context	上下文
     * @param eventID	事件ID
     * @param value
     */
    public static void onEvent(Context context, String eventID, String value){
        if(isStatistics(context)){
            MobclickAgent.onEvent(context, eventID , value);
//			showToast(context, "统计_计数_eventID="+eventID+"_value="+value);
        }
    }


    /**
     * 判断是否需要统计(管理员和马甲不统计)
     * @return		true 统计	false 不统计
     */
    @SuppressWarnings("unchecked")
    public static boolean isStatistics(Context context) {
            //没有用户
            return true;
    }

}
