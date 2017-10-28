package com.a99live.zhibo.live.parser;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.activity.LoginActivity;
import com.a99live.zhibo.live.net.ErrorThrowable;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Json解析
 * Created by ljb on 2016/5/12.
 */
public class JsonParser {

    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String ENTITY = "entity";
    private static final String DATA = "data";
    private static final String append = "append";

    /**
     * 用户未登录
     */
    public static final int ERROR_UN_LOGIN = 111111110;

    /**
     * 签名验证错误
     */
    public static final int ERROR_HMAC = 100001;

    /**
     * 房间未找到
     */
    public static final int ERROR_UN_ROOM = 20000006;

    private static Gson mGson;

    static {
        mGson = new Gson();
    }


    public static <T> T fromLiveJson(String json, Class<T> clazz) {
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            int code = jsonObject.getInt(CODE);
//            if (code == 0) {
//                String message = jsonObject.getString(MESSAGE);
//                JSONObject entity = jsonObject.getJSONObject(ENTITY);
//                String resultJson = entity.getString(DATA);
                return mGson.fromJson(json, clazz);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return null;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return mGson.fromJson(json, clazz);
    }

    public static void fromError(Throwable throwable, final Activity act) {
        if (throwable instanceof ErrorThrowable){
            ErrorThrowable t = (ErrorThrowable) throwable;
            String errorJson = t.getErrorJson();
            try {
                JSONObject jsonObject = new JSONObject(errorJson);
                int code = jsonObject.getInt(CODE);
                if (code == ERROR_UN_LOGIN){
                    Intent intent = new Intent(act, LoginActivity.class);
                    act.startActivity(intent);
                    Toast.makeText(act,"用户未登录",Toast.LENGTH_SHORT).show();
                } else if (code == ERROR_HMAC) {
                    Toast.makeText(LiveZhiBoApplication.getApp(),"签名验证错误",Toast.LENGTH_SHORT).show();
                } else if (code == ERROR_UN_ROOM){
                    Toast.makeText(LiveZhiBoApplication.getApp(),"房间未找到",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static String toJson(Object o) {
        return mGson.toJson(o);
    }
}
