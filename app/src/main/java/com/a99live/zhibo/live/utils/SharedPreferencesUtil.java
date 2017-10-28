package com.a99live.zhibo.live.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xialo on 2016/7/25.
 */
public class SharedPreferencesUtil {

    private static final String spFileName = "welcomePage";
    public static final String FIRST_OPEN = "first_open";
    public static final String FIRST_OPEN_HOME = "first_open_home";
    public static final String FIRST_OPEN_LIVE = "first_open_live";
    public static final String FIRST_OPEN_USER = "first_open_user";

    public static Boolean getBoolean(Context context, String strKey,
                                     Boolean strDefault) {//strDefault	boolean: Value to return if this preference does not exist.
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        Boolean result = setPreferences.getBoolean(strKey, strDefault);
        return result;
    }

    public static void putBoolean(Context context, String strKey,
                                  Boolean strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean(strKey, strData);
        editor.commit();
    }

}
