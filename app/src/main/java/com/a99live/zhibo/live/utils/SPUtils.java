package com.a99live.zhibo.live.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.a99live.zhibo.live.LiveZhiBoApplication;

import java.util.Arrays;
import java.util.Set;


public class SPUtils {

    private static final String SP_NAME = "sp_99live";

    /**IM登录所需要的uid  and  sig***/
    public static final String IMUID = "imuid";
    public static final String IMSIG = "imsig";

    /**用户标识信息*/
    public static final String USER_CODE = "ucode";//用户ucode
    public static final String USER_UAUTH = "uauth";//用户uauth
    /**用户信息*/
    public static final String USER_NAME = "uname";//用户名
    public static final String USER_AVATAR = "uavatar";//用户头像
    public static final String USER_GENDER = "user_gender";//用户性别
    public static final String USER_ID = "uid";//用户标识
    public static final String USER_AVATAR_ID = "user_avatar_id";//用户头像id
    public static final String USER_IDENTITY = "user_identity";//用户99号
    public static final String USER_MOBILE = "mobile";//用户手机号
    public static final String USER_BIRTHDAY = "birthday";//用户出生日期
    public static final String USER_REGION = "region";//用户所在地
    public static final String USER_SIGN = "user_sign";//用户签名
    public static final String USER_STAR = "star";//用户星座
    public static final String USER_IS_DEFAULT_AVATAR = "is_default_avatar";//用户头像是否是默认的 Y/N
    public static final String LIVE_ROOM_ID = "room_id";//直播间的id

    /**获取token*/
    public static final String TAG_TOKEN = "token";//token
    public static final String TAG_TIME = "time";//time

    /**可删除*/
    /**上传图片参数*/
    public static final String SIGN = "sign";
    public static final String BUCKET = "bucket";
    public static final String FILE_ID = "fileId";

    /* 通过友盟获取domain port */
    public static final String API_DOMAIN = "api_domain";
    public static final String API_PORT = "api_port";

    public static final String API_VERSION = "api_version";
    public static final String H5_DOMAIN = "h5_domain";
    public static final String H5_PORT = "h5_port";

    public static final String LEVEL_VERSION = "level_version";
    public static final String LEVEL_USER    = "level_user";
    public static final String LEVEL_SPECIAL = "special";
    //微信 支付 订单号
    public static final String WXPAY_ORDER_ID = "wxpay_order_id";
    public static final String WXPAY_SUCCESS = "wxpay_success";// 0  初始值  -1 -2  失败 2 成功
    //用户账户信息
    public static final String JIU_ZUAN = "jiu_zuan";
    public static final String JIU_BI = "jiu_bi";
    public static final String JIU_RMB = "jiu_rmb";
    public static final String ACCOUNT = "account";
    public static final String WITHDRAW_LINE = "withdraw_line";
    public static final String USER_LEVEL = "user_level";

    //小视频
    public static final String VIDEO_PLAY = "video_play";
    public static final String VIDEO_LIKE = "video_like";


    public static SharedPreferences getInstance() {
        return LiveZhiBoApplication.getApp().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static void putString(String key, String value) {
        getInstance().edit().putString(key, value).commit();
    }

    /**
     * SP中读取key对应的boolean型value
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        return getInstance().getString(key, "");
    }

    public static void putBoolean(String key, boolean value) {
        getInstance().edit().putBoolean(key, value).commit();
    }

    /**
     * SP中读取key对应的boolean型value
     *
     * @param key
     * @return 没有返回false
     */
    public static boolean getBoolean(String key) {
        return getInstance().getBoolean(key, false);
    }

    public static void putInt(String key, int value) {
        getInstance().edit().putInt(key, value).commit();
    }

    /**
     * SP中读取key对应的整形value
     *
     * @param key
     * @return 没有返回0
     */
    public static int getInt(String key) {
        return getInstance().getInt(key, 0);
    }

    public static void putFloat(String key, float value) {
        getInstance().edit().putFloat(key, value).commit();
    }

    /**
     * SP中读取key对应的整形value
     *
     * @param key
     * @return 没有返回-1
     */
    public static float getFloat(String key) {
        return getInstance().getFloat(key, -1);
    }

    public static void putStringSet(String key, Set<String> setValue) {
        getInstance().edit().putStringSet(key, setValue);
    }

    /**
     * SP中读取key对应的String类型set集合
     *
     * @param key
     * @return 没有返回null
     */
    public static Set<String> getStringSet(String key) {
        return getInstance().getStringSet(key, null);
    }


    public static final String TAG_SEARCH_KEYWORD = "search_key_word";
    private static final String FLAG_SPACE = ",";


    /**
     * 保存搜索记录
     */
    public static void saveSearchWord(String keyWord) {
        String str = getString(TAG_SEARCH_KEYWORD);

        if (TextUtils.isEmpty(str)) {
            putString(TAG_SEARCH_KEYWORD, keyWord.trim());
        } else {
            boolean isAdded = Arrays.asList(str.trim().split(FLAG_SPACE)).contains(keyWord);
            if (!isAdded) {
                putString(TAG_SEARCH_KEYWORD, new StringBuilder(str).append(FLAG_SPACE).append(keyWord.trim()).toString());
            }
        }
    }

    /**
     * 获取所有历史搜索词
     */
    public static String[] getSearchWords() {
        String str;
        if (TextUtils.isEmpty(str = getString(TAG_SEARCH_KEYWORD).trim())) {
            return null;
        }
        return str.split(FLAG_SPACE);
    }

    public static boolean isLogined() {
        int userCode = getInt(USER_CODE);
        if (0!=userCode) {
            return true;
        }
        return false;
    }

    /**
     * 清除所有数据
     *
     * @param
     */
    public static void clear() {
        putString(SPUtils.USER_CODE, "");
    }


}
