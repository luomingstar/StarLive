package com.a99live.zhibo.live.utils;

/**
 * Created by Colin on 16/9/5 16:40.
 * 邮箱：cartier_he@163.com
 * 微信：cartier_he
 */
public class Contanst {

    private static final String PACKAGE = "com.a99live.zhibo.a99live";
    public static final int CAPTURE_PHOTO_ACTIVITY_REQUEST_CODE = 11;//从相册取图
    public static final int CAPTURE_CAMERA_ACTIVITY_REQUEST_CODE = 22;//拍照取图
    public static final int CAPTURE_PHOTOCROP_ACTIVITY_REQUEST_CODE = 44;//系统相机摄像
    public static final int CROP_IMAGE = 33;//截取图片
    public static final int SDK_APPID = 1400013889;
    public static final String ACCOUNT_TYPE = "7089";

    public static final int LOCATION_PERMISSION_REQ_CODE = 1;
    public static final int WRITE_PERMISSION_REQ_CODE = 2;

    public static final int TEXT_TYPE = 0;
    public static final int MEMBER_ENTER = 1;
    public static final int MEMBER_EXIT = 2;

    public static final String CMD_KEY = "userAction";
    public static final String CMD_PARAM = "actionParam";

    public static final String APPLY_CHATROOM = "申请加入";
    public static final int IS_ALREADY_MEMBER = 10013;

    public static final String ACTION_HOST_LEAVE = PACKAGE
            + ".ACTION_HOST_LEAVE";

    public static final int AVIMCMD_Text = -1;         // 普通的聊天消息

    public static final int AVIMCMD_None = AVIMCMD_Text + 1;               // 无事件

    // 以下事件为TCAdapter内部处理的通用事件
    public static final int AVIMCMD_EnterLive = AVIMCMD_None + 1;          // 用户加入直播, Group消息  1
    public static final int AVIMCMD_ExitLive = AVIMCMD_EnterLive + 1;         // 用户退出直播, Group消息  2
    public static final int AVIMCMD_Praise = AVIMCMD_ExitLive + 1;           // 点赞消息, Demo中使用Group消息  3



}
