package com.a99live.zhibo.live.utils;

/**
 * 静态函数
 */
public class TCConstants {

    //是否是测试包 true 线下包  false 线上包
    public static boolean isTest = false;
    
    /**微信支付APPID*/
    public static final String APP_ID = "wx8af34fd3de90a8f9";

    /**
	* 腾讯云服务配置专区（请自主替换）
	*/
    //云通信服务相关配置
    public static final int IMSDK_ACCOUNT_TYPE = isTest ? 7089 : 8057;
    public static final int IMSDK_APPID = isTest ? 1400013889 : 1400016829;

    //COS存储服务相关配置
    public static final String COS_BUCKET = "";
    public static final String COS_APPID  = isTest ? "10063116" : "10069549";

	/**
	* 常量字符串
	*/
    public static final String USER_INFO        = "user_info";
    public static final String UCODE            = "ucode";
    public static final String USER_SIG         = "user_sig";
    public static final String USER_NICK        = "user_nick";
    public static final String USER_SIGN        = "user_sign";
    public static final String USER_HEADPIC     = "user_headpic";
    public static final String USER_COVER       = "user_cover";
    public static final String USER_LOC         = "user_location";
    public static final String SVR_RETURN_CODE  = "returnValue";
    public static final String SVR_RETURN_MSG   = "returnMsg";
    public static final String SVR_RETURN_DATA  = "returnData";
//    public static final String USER_UCODE       = "user_ucode";

    //主播退出广播字段
    public static final String EXIT_APP         = "EXIT_APP";

    public static final int USER_INFO_MAXLEN    = 30;
    public static final int TV_TITLE_MAX_LEN    = 30;
    public static final int NICKNAME_MAX_LEN    = 20;

    public static final int TEXT_TYPE           = 0;
    public static final int MEMBER_ENTER        = 1;
    public static final int MEMBER_EXIT         = 2;
    public static final int PRAISE              = 3;
    public static final int SYSTEM_TYPE         = 4;
    public static final int SEND_GIFT           = 5;

    public static final int LOCATION_PERMISSION_REQ_CODE = 1;
    public static final int WRITE_PERMISSION_REQ_CODE    = 2;

    public static final String ANCHOR_ID        = "ucode";
    public static final String PUBLISH_URL      = "publish_url";
    public static final String ROOM_ID          = "room_id";
    public static final String ROOM_TITLE       = "room_title";
    public static final String COVER_PIC        = "cover_pic";
    public static final String GROUP_ID         = "group_id";
    public static final String PLAY_URL         = "play_url";
    public static final String PLAY_TYPE        = "play_type";
    public static final String PUSHER_AVATAR    = "pusher_avatar";
    public static final String PUSHER_ID        = "pusher_id";
    public static final String MEMBER_COUNT     = "member_count";
    public static final String HEART_COUNT      = "heart_count";
    public static final String FILE_ID          = "file_id";
    public static final String ACTIVITY_RESULT  = "activity_result";

    public static final String CMD_KEY          = "userAction";
    public static final String DANMU_TEXT       = "actionParam";
    public static final String YOUMENGKEY       =  isTest ? "dev_address" : "online_address_https";//online_address  dev_address  新版加的online_address_https
    public static final String YOUMENGLEVELKEY  =  isTest ? "dev_config" : "online_config";
    public static final String LEVEL            = "level";
    public static final String TEXTMSG          = "textMsg";

    public static final String ROBOT_NAME       = "nickname";
    public static final String ROBOT_AVATAR     = "avatar";
    public static final String ROB0T_FULLHEADIMG = "full_head_image";
    public static final String ROBOT_USER_ID    = "user_id";
    public static final String ROBOT_IDENTITY   = "identity";
    public static final String ROBOT_ALL        = "yetRobotModels";
    public static final String ROBOT_CHAT       = "language";
    public static final String ROBOT_ACTION     = "action";
    public static final String ROBOT_LEVEL      = "robot_level";
    public static final String YETROBOTSIZE     = "yetRobotSize";
    public static final String SHUT_UP          = "limitUseridenty";
	
	public static final String NOTIFY_QUERY_USERINFO_RESULT = "notify_query_userinfo_result";

    /**
	* IM 互动消息类型
	*/
    public static final int IMCMD_EnterLive     = 1;   // 用户加入直播, Group消息  1
    public static final int IMCMD_ExitLive      = 2;   // 用户退出直播, Group消息  2
    public static final int IMCMD_PRAISE        = 3;   // 点赞消息, Demo中使用Group消息  3
    public static final int IMCMD_DANMU         = 6;   // 弹幕消息
    public static final int IMCMD_PAILN_TEXT    = 4;   // 文本消息
    public static final int IMCMD_GIFT          = 7;   // 礼物
    public static final int IMCMD_ROBOT         = 8;   // 机器人加入
    public static final int IMCMD_ROBOT_ALL     = 9;   // 已经发送的机器人，当有新用户加入的时候再次发送
    public static final int IMCMD_ROBOT_PRAISE  = 10;  // 机器人点赞
    public static final int IMCMD_ROBOT_CHAT    = 11;  // 机器人说话
    public static final int IMCMD_FOLLOW        = 12;  // 关注主播发送消息
    public static final int IMCMD_SHUTUP        = 13;  // 禁言用户
    public static final int IMCMD_SHARE_MSG     = 14;  // 分享消息提示

    //ERROR CODE TYPE
    public static final int ERROR_GROUP_NOT_EXIT             = 10010;
    public static final int ERROR_QALSDK_NOT_INIT            = 6013;
    public static final int ERROR_JOIN_GROUP_ERROR           = 10015;
    public static final int SERVER_NOT_RESPONSE_CREATE_ROOM  = 1002;
    public static final int NO_LOGIN_CACHE                   = 1265;


	/**
	* 用户可见的错误提示语
	*/
    public static final String ERROR_MSG_NET_DISCONNECTED    = "网络异常，请检查网络";
    public static final String ERROR_MSG_NET_BUSY = "网络不太好哦";

    //直播端错误信息
    public static final String ERROR_MSG_CREATE_GROUP_FAILED = "创建直播房间失败,Error:";
    public static final String ERROR_MSG_GET_PUSH_URL_FAILED = "拉取直播推流地址失败,Error:";
    public static final String ERROR_MSG_OPEN_CAMERA_FAIL    = "无法打开摄像头，需要摄像头权限";
    public static final String ERROR_MSG_OPEN_MIC_FAIL       = "无法打开麦克风，需要麦克风权限";

    //播放端错误信息
    public static final String ERROR_MSG_GROUP_NOT_EXIT      = "直播已结束，加入失败";
    public static final String ERROR_MSG_JOIN_GROUP_FAILED   = "网络超时，加入房间失败";
    public static final String ERROR_MSG_LIVE_STOPPED        = "直播已结束";
    public static final String ERROR_MSG_NOT_QCLOUD_LINK     = "非腾讯云链接，若要放开限制请联系腾讯云商务团队";
    public static final String ERROR_RTMP_PLAY_FAILED        = "视频流播放失败";

    public static final String TIPS_MSG_STOP_PUSH            = "确定关闭直播？";

    //网络类型
    public static final int NETTYPE_WIFI = 0;
    public static final int NETTYPE_NONE = 1;
    public static final int NETTYPE_2G   = 2;
    public static final int NETTYPE_3G   = 3;
    public static final int NETTYPE_4G   = 4;

    public static final String SYSTEM_MESSAGE = "我们提倡绿色直播，封面和直播内容含有吸烟、低俗、引诱、暴露等都将会被封号，同时禁止直播聚众闹事、集会，网警将会２４小时在线巡查！";

    //直播与观看的时候的礼物消息和点击头像查看信息的消息id
    public static final int OPEN_USER_BY_IDENTITY = 8;

    public static final String IM_C2C_MESSAGE_HINT_SHOW = "IM_C2C_MESSAGE_HINT_SHOW";
    public static final String IM_C2C_MESSAGE_HINT_GONE = "IM_C2C_MESSAGE_HINT_GONE";

    /**
     * UGC小视频录制信息
     */
    public static final String VIDEO_RECORD_TYPE        = "type";
    public static final String VIDEO_RECORD_RESULT      = "result";
    public static final String VIDEO_RECORD_DESCMSG     = "descmsg";
    public static final String VIDEO_RECORD_VIDEPATH    = "path";
    public static final String VIDEO_RECORD_COVERPATH   = "coverpath";

    public static final int VIDEO_RECORD_TYPE_PUBLISH   = 1;   // 推流端录制
    public static final int VIDEO_RECORD_TYPE_PLAY      = 2;   // 播放端录制

    //连麦开关
    public static final boolean TX_ENABLE_LINK_MIC                          = true; //开启连麦标志位

    //连麦消息类型
    public static final int LINKMIC_CMD_REQUEST                             = 10001;
    public static final int LINKMIC_CMD_ACCEPT                              = 10002;
    public static final int LINKMIC_CMD_REJECT                              = 10003;
    public static final int LINKMIC_CMD_MEMBER_JOIN_NOTIFY                  = 10004;
    public static final int LINKMIC_CMD_MEMBER_EXIT_NOTIFY                  = 10005;
    public static final int LINKMIC_CMD_KICK_MEMBER                         = 10006;

    //连麦响应类型
    public static final int LINKMIC_RESPONSE_TYPE_ACCEPT                    = 1;    //主播接受连麦
    public static final int LINKMIC_RESPONSE_TYPE_REJECT                    = 2;    //主播拒绝连麦
}
