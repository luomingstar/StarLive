package com.a99live.zhibo.live.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.a99live.zhibo.live.activity.UserCardActivity;
import com.a99live.zhibo.live.model.Robot;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.rtmp.TXLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.a99live.zhibo.live.utils.TCConstants.IMCMD_PAILN_TEXT;

/**
 * 消息收发管理类
 * Created by teckjiang on 2016/8/4
 */
public class TCChatRoomMgr implements TIMMessageListener {

    public static final String TAG = TCChatRoomMgr.class.getSimpleName();
    private TIMConversation mGroupConversation;
    private TCChatRoomListener mTCChatRoomListener;
    private String mRoomId;

    private TCChatRoomMgr() {
    }

    private static class TCChatRoomMgrHolder {
        private static TCChatRoomMgr instance = new TCChatRoomMgr();
    }

    public static TCChatRoomMgr getInstance() {
        return TCChatRoomMgrHolder.instance;
    }

    /**
     * 发送消息
     *
     * @param cmd   控制符（代表不同的消息类型）具体查看TCContants.IMCMD开头变量
     * @param param 参数
     */
    public void sendMessage(final int cmd, final String param) {

        sendMessage(cmd, param, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendMsgCallback(i,cmd, null,param);
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendMsgCallback(0,cmd, timMessage,param);
            }
        });
    }

    public void sendShutUpMessage(final int cmd, final JSONObject param) {

        sendShutUpMessage(cmd, param, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendMsgCallback(i,cmd, null,param.toString());
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendMsgCallback(0,cmd, timMessage,param.toString());
            }
        });
    }

    public void sendRobotMessage(final int cmd, final Robot robot) {

        sendRobotMessage(cmd, robot, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendRobotCallBack(-1,cmd, null,robot);
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendRobotCallBack(0,cmd, timMessage,robot);
            }
        });
    }


    /**
     * 观看者退出退出群组接口
     *
     * @param roomId 群组Id
     */
    public void quitGroup(final String roomId) {
        if (!TextUtils.isEmpty(roomId)) {
            sendMessage(TCConstants.IMCMD_ExitLive, "");
        }

        TIMGroupManager.getInstance().quitGroup(roomId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "quitGroup failed, code:" + i + ",msg:" + s);
                if (null != mTCChatRoomListener) {
                    mTCChatRoomListener.onQuitGroupCallback(i, s);
                }
                mTCChatRoomListener = null;
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "quitGroup success, groupid:" + roomId);
                mTCChatRoomListener = null;
            }
        });
    }

    /**
     * 观看者退出退出群组接口
     *
     * @param roomId 群组Id
     */
    public void quitGroupOldRoomId(final String roomId) {

//        sendMessage(TCConstants.IMCMD_ExitLive, "");

        TIMGroupManager.getInstance().quitGroup(roomId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "quitGroup failed, code:" + i + ",msg:" + s);
//                if (null != mTCChatRoomListener) {
//                    mTCChatRoomListener.onQuitGroupCallback(i, s);
//                }
//                mTCChatRoomListener = null;
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "quitGroup success, groupid:" + roomId);
//                mTCChatRoomListener = null;
            }
        });
    }

    /**
     * 主播创建直播群组接口
     */
    public void createGroup() {

        //创建直播间
        TIMGroupManager.getInstance().createAVChatroomGroup("TVShow", new TIMValueCallBack<String>() {
            @Override
            public void onError(int code, String desc) {
                Log.d(TAG, "create av group failed. code: " + code + " errmsg: " + desc);

                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onJoinGroupCallback(code, desc);
            }

            @Override
            public void onSuccess(String roomId) {
                Log.d(TAG, "create av group succ, groupId:" + roomId);
                mRoomId = roomId;
                mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, roomId);
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onJoinGroupCallback(0, roomId);
            }
        });

    }

    /**
     * 主播端退出前删除直播群组
     */
    public void deleteGroup() {

//        sendMessage(TCConstants.IMCMD_ExitLive, "");

        if (mRoomId == null || TextUtils.isEmpty(mRoomId))
            return;

        TIMManager.getInstance().deleteConversation(TIMConversationType.Group, mRoomId);
        TIMGroupManager.getInstance().deleteGroup(mRoomId, new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.d(TAG, "delete av group failed. code: " + code + " errmsg: " + msg);
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onQuitGroupCallback(code, msg);
                mRoomId = null;
                mTCChatRoomListener = null;
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "delete av group succ. groupid: " + mRoomId);
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onQuitGroupCallback(0, mRoomId);
                mRoomId = null;
                mTCChatRoomListener = null;
            }
        });
    }

    /**
     * 加入群组
     *
     * @param roomId 群组ID
     */
    public void joinGroup(final String roomId) {
        mRoomId = roomId;
        TIMGroupManager.getInstance().applyJoinGroup(roomId, "", new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.d("livelog", "joingroup failed, code:" + i + ",msg:" + s);
                mRoomId = null;
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onJoinGroupCallback(i, s);
                else
                    Log.d("livelog", "mPlayerListener not init");
            }

            @Override
            public void onSuccess() {
                Log.d("livelog", "joingroup success, groupid:" + roomId);
                mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, roomId);
                //加入群组成功后，发送加入消息
                sendMessage(TCConstants.IMCMD_EnterLive, "");
                if (null != mTCChatRoomListener) {
                    mTCChatRoomListener.onJoinGroupCallback(0, roomId);
                } else {
                    Log.d("livelog", "mPlayerListener not init");
                }
            }
        });

    }

    /**
     * 获取观看成员列表与基本信息
     * 直播大群只能获取300人以内的成员列表
     * 通过getGroupMembers拉取userId信息后，再拉取user头像、昵称信息
     *
     * @param timValueCallBack 查询结果回调
     */
    public void getGroupMembers(final TIMValueCallBack<List<TIMUserProfile>> timValueCallBack) {

        //获取群组成员信息
        TIMGroupManager.getInstance().getGroupMembers(mRoomId, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {

            @Override
            public void onError(int code, String desc) {
                Log.d(TAG, "GETGroupMembers failed");
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> infoList) {
                Log.d(TAG, "GETGroupMembers success");

                //获取用户id列表
                List<String> identifiers = new ArrayList<>();
                for (TIMGroupMemberInfo info : infoList) {
//                    if (identifiers.size() < 100)
                    identifiers.add(info.getUser());
                }

                //请求获取用户详细信息
                TIMFriendshipManager.getInstance().getUsersProfile(identifiers, timValueCallBack);

            }
        });
    }

    //todo:目前无法通过getGroupMembers获取准确人数
    public void getGroupProfiles(TIMValueCallBack<List<TIMGroupDetailInfo>> timValueCallBack) {
        List<String> groups = new ArrayList<String>() {{
            add(mRoomId);
        }};
        TIMGroupManager.getInstance().getGroupDetailInfo(groups, timValueCallBack);
    }


    /**
     * IMSDK消息回调接口
     *
     * @param list 消息列表
     * @return 默认情况下所有消息监听器都将按添加顺序被回调一次
     * 除非用户在OnNewMessages回调中返回true
     * 此时将不再继续回调下一个消息监听器，此处默认返回false
     */
    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        parseIMMessage(list);
        return false;
    }


    /**
     * 注入消息回调监听类
     * 需要实现并注入TCChatRoomListener才能获取相应消息回调
     *
     * @param listener
     */
    public void setMessageListener(TCChatRoomListener listener) {
        mTCChatRoomListener = listener;
        TIMManager.getInstance().addMessageListener(this);
    }

    /**
     * 解析TIM消息列表
     *
     * @param list 消息列表
     */
    private void parseIMMessage(List<TIMMessage> list) {

        if (list.size() > 0) {
            if (mGroupConversation != null)
                mGroupConversation.setReadMessage(list.get(0));
            Log.d(TAG, "parseIMMessage readMessage " + list.get(0).timestamp());
        }
//        if (!bNeverLoadMore && (tlist.size() < mLoadMsgNum))
//            bMore = false;

        for (int i = list.size() - 1; i >= 0; i--) {
            TIMMessage currMsg = list.get(i);

            for (int j = 0; j < currMsg.getElementCount(); j++) {
                if (currMsg.getElement(j) == null)
                    continue;
                TIMElem elem = currMsg.getElement(j);
                TIMElemType type = elem.getType();
                String sendId = currMsg.getSender();
                final TIMUserProfile timUserProfile = currMsg.getSenderProfile();
                if (timUserProfile == null){
                    continue;
                }
//                Map<String, byte[]> customInfo = timUserProfile.getCustomInfo();
//                if (customInfo != null) {
//                    Log.d("livelog", customInfo.toString());
//                }
//                byte[] tag_profile_custom_levels = customInfo.get("Tag_Profile_Custom_Level");
//                try {
//                    String level = new String(tag_profile_custom_levels,"utf-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                if(sendId.equals(SPUtils.getString(SPUtils.USER_IDENTITY))) {
                    TXLog.d(TAG, "recevie a self-msg type:" + type.name());
                    continue;
                }
                //系统消息
                if (type == TIMElemType.GroupSystem) {
                    if (TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE == ((TIMGroupSystemElem) elem).getSubtype()) {
                        //群被解散
//                        try {
                        String groupId = ((TIMGroupSystemElem) elem).getGroupId();
//                        }catch (Exception e){}
                        if (null != mTCChatRoomListener)
                            mTCChatRoomListener.onGroupDelete(groupId);
                    }
                    if (TIMGroupSystemElemType.TIM_GROUP_SYSTEM_CUSTOM_INFO == ((TIMGroupSystemElem) elem).getSubtype()){
//                        String platform = ((TIMGroupSystemElem) elem).getPlatform();
//                        String opReason = ((TIMGroupSystemElem) elem).getOpReason();
                        byte[] userData = ((TIMGroupSystemElem) elem).getUserData();
                        try {
                            String str = new String(userData, "utf-8");
                            if (null != mTCChatRoomListener){
                                mTCChatRoomListener.onShowFlyGift(str);
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                }
                //定制消息
                if (type == TIMElemType.Custom) {
                    String id, nickname, faceurl;
                    if (timUserProfile != null) {
                        id = timUserProfile.getIdentifier();
                        if (TextUtils.isEmpty(timUserProfile.getNickName()))
                            nickname = sendId;
                        else {
                            nickname = timUserProfile.getNickName();
                        }
                        faceurl = timUserProfile.getFaceUrl();
                    } else {
                        id = sendId;
                        nickname = sendId;
                        faceurl = "";
                    }

                    handleCustomMsg(elem, id, nickname, faceurl);
                    continue;
                }

                //其他群消息过滤

                if (currMsg.getConversation() != null && currMsg.getConversation().getPeer() != null)
//                    if (!CurLiveInfo.getChatRoomId().equals(currMsg.getConversation().getPeer())) {
//                        continue;
//                    }

                    //最后处理文本消息
                    if (type == TIMElemType.Text) {
//                        if (currMsg.isSelf()) {
//                            handleTextMessage(elem, SPUtils.getString(SPUtils.USER_NAME));
//                        } else {
                            String nickname;
                            String faceurl1;
                            if (timUserProfile != null) {
                                if ( (!timUserProfile.getNickName().equals(""))){
                                    nickname = timUserProfile.getNickName();
                                }else{
                                    nickname = sendId;
                                }
                                faceurl1 = timUserProfile.getFaceUrl();
                            } else {
                                nickname = sendId;
                                faceurl1 = "";
                            }
                            handleTextMessage(elem, nickname,sendId);
//                        }
                    }
            }
        }
    }

    /**
     * 退出房间
     */
    public void removeMsgListener() {
        mTCChatRoomListener = null;
        TIMManager.getInstance().removeMessageListener(this);
        if (!TextUtils.isEmpty(mRoomId)) {
            TIMManager.getInstance().deleteConversation(TIMConversationType.Group, mRoomId);
        }
    }

    /**
     * 发送消息
     *
     * @param msg              TIM消息
     * @param timValueCallBack 发送消息回调类
     */
    public void sendTIMMessage(TIMMessage msg, TIMValueCallBack<TIMMessage> timValueCallBack) {
        if (mGroupConversation != null)
            mGroupConversation.sendMessage(msg, timValueCallBack);
    }

    private void sendMessage(int cmd, String param, TIMValueCallBack<TIMMessage> timValueCallBack) {

        JSONObject sendJson = new JSONObject();
        try {
            sendJson.put(TCConstants.CMD_KEY, cmd);
            sendJson.put(TCConstants.DANMU_TEXT, param);
            sendJson.put(TCConstants.TEXTMSG,param);
            String lv = SPUtils.getString(SPUtils.USER_LEVEL);
            Log.d("livelog","level=" + lv);
            if (!TextUtils.isEmpty(lv)){
                sendJson.put(TCConstants.LEVEL,lv);
            }else{
                sendJson.put(TCConstants.LEVEL,"1");
            }

            String cmds = sendJson.toString();
            Log.i(TAG, "send cmd : " + cmd + "|" + cmds);

            TIMMessage msg = new TIMMessage();

            if (cmd == IMCMD_PAILN_TEXT) {
//                JSONObject s = new JSONObject();
//                s.put(TCConstants.TEXTMSG,param);
//                Log.d("livelog","level=" + lv);
//                if (!TextUtils.isEmpty(lv)){
//                    s.put(TCConstants.LEVEL,lv);
//                }else{
//                    s.put(TCConstants.LEVEL,"1");
//                }
//                sendJson.put(TCConstants.TEXTMSG, s);
//                String textMsg = sendJson.toString();

                TIMTextElem elem = new TIMTextElem();
                elem.setText(cmds);

                if (msg.addElement(elem) != 0) {
                    Log.d(TAG, "addElement failed");
                    return;
                }

            }
    //        else if (cmd == TCConstants.IMCMD_DANMU){
    //            TIMTextElem elem = new TIMTextElem();
    //            elem.setText("*#*"+param);
    //
    //            if (msg.addElement(elem) != 0) {
    //                Log.d(TAG, "addElement failed");
    //                return;
    //            }
    //
    //        }
            else {
                if (cmd == TCConstants.IMCMD_DANMU){
                    JSONObject s = new JSONObject();
                    s.put(TCConstants.TEXTMSG,param);
                    Log.d("livelog","level=" + lv);
                    if (!TextUtils.isEmpty(lv)){
                        s.put(TCConstants.LEVEL,lv);
                    }else{
                        s.put(TCConstants.LEVEL,"1");
                    }
                    sendJson.put(TCConstants.DANMU_TEXT, s.toString());
                    cmds = sendJson.toString();
                }

                TIMCustomElem elem = new TIMCustomElem();
                elem.setData(cmds.getBytes());
                elem.setDesc("");
                msg.addElement(elem);
            }

            sendTIMMessage(msg, timValueCallBack);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendShutUpMessage(int cmd, JSONObject param, TIMValueCallBack<TIMMessage> timValueCallBack) {

        JSONObject sendJson = new JSONObject();
        try {
            sendJson.put(TCConstants.CMD_KEY, cmd);
            sendJson.put(TCConstants.SHUT_UP, param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String cmds = sendJson.toString();
        Log.i(TAG, "send cmd : " + cmd + "|" + cmds);

        TIMMessage msg = new TIMMessage();

        if (cmd == IMCMD_PAILN_TEXT) {

            TIMTextElem elem = new TIMTextElem();
            elem.setText(param.toString());

            if (msg.addElement(elem) != 0) {
                Log.d(TAG, "addElement failed");
                return;
            }

        }
//        else if (cmd == TCConstants.IMCMD_DANMU){
//            TIMTextElem elem = new TIMTextElem();
//            elem.setText("*#*"+param);
//
//            if (msg.addElement(elem) != 0) {
//                Log.d(TAG, "addElement failed");
//                return;
//            }
//
//        }
        else {
            TIMCustomElem elem = new TIMCustomElem();
            elem.setData(cmds.getBytes());
            elem.setDesc("");
            msg.addElement(elem);
        }

        sendTIMMessage(msg, timValueCallBack);

    }

    /**
     * 发送机器人消息给所有的用户
     * @param cmd
     * @param robot
     * @param timValueCallBack
     */
    private void sendRobotMessage(int cmd,Robot robot, TIMValueCallBack<TIMMessage> timValueCallBack){
        if (robot != null){
        JSONObject sendJson = new JSONObject();
        try {
            sendJson.put(TCConstants.CMD_KEY, cmd);
            sendJson.put(TCConstants.ROBOT_NAME,robot.getNickname());
            sendJson.put(TCConstants.ROB0T_FULLHEADIMG,robot.getFull_head_image());
            sendJson.put(TCConstants.ROBOT_USER_ID,robot.getUser_id());
            sendJson.put(TCConstants.ROBOT_ACTION,robot.getAction());
            sendJson.put(TCConstants.ROBOT_CHAT,robot.getLanguage());
            sendJson.put(TCConstants.ROBOT_IDENTITY,robot.getIdentity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String cmds = sendJson.toString();
        Log.i(TAG, "send cmd : " + cmd + "|" + cmds);

        TIMMessage msg = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(cmds.getBytes());
        elem.setDesc("");
        msg.addElement(elem);
        sendTIMMessage(msg, timValueCallBack);
        }
    }

    /**
     * 将主播端的机器人分发给所有的新用户，机器人数据统一
     * @param cmd
     * @param robot
     */
    public void sendRobotAll(final int cmd, JSONArray robot, int size){
        JSONObject sendJson = new JSONObject();
        try {
            sendJson.put(TCConstants.CMD_KEY, cmd);
            sendJson.put(TCConstants.ROBOT_ALL,robot);
            sendJson.put(TCConstants.YETROBOTSIZE,size);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String cmds = sendJson.toString();
        Log.i(TAG, "send cmd : " + cmd + "|" + cmds);
        TIMMessage msg = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(cmds.getBytes());
        elem.setDesc("");
        msg.addElement(elem);
        sendTIMMessage(msg, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                if (null != mTCChatRoomListener){
                    mTCChatRoomListener.onSendRobotCallBack(i,cmd,null,null);
                }
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {

            }
        });
    }

    /**
     * 群主禁言用户在群聊中
     */
    public void setShutUp(String groupId, final String user, long time, final UserCardActivity.ShutUp shutUp){
        //对user禁言秒
        TIMGroupManager.getInstance().modifyGroupMemberInfoSetSilence(groupId, user, time, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
//                Log.e(tag, "modifyGroupMemberInfoSetSilence failed: " + code + " desc" + desc);
                shutUp.callBack(code , user, desc);
            }

            @Override
            public void onSuccess() {
//                Log.e(tag, "modifyGroupMemberInfoSetSilence succ");
                shutUp.callBack(0,user,"");
            }
        });
    }


    /**
     * 处理定制消息 赞 加入 退出 弹幕 并执行相关回调
     *
     * @param elem 消息体
     */
    private void handleCustomMsg(TIMElem elem, String identifier, String nickname, String faceUrl) {
        try {
            String customText = new String(((TIMCustomElem) elem).getData(), "UTF-8");
            Log.i(TAG, "cumstom msg  " + customText);
            JSONTokener jsonParser = new JSONTokener(customText);
            JSONObject json = (JSONObject) jsonParser.nextValue();
            int action = json.getInt(TCConstants.CMD_KEY);
            String level = "1";
            if (json.has(TCConstants.LEVEL)){
                level = json.getString(TCConstants.LEVEL);
            }
            switch (action) {
                case TCConstants.IMCMD_PRAISE:
                    Log.d(TAG, "getr IMCMD_Praise msg:" + identifier + " name :" + nickname);
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onPraise(identifier, nickname,level);
                    break;
                case TCConstants.IMCMD_EnterLive:
                    Log.d(TAG, "getr IMCMD_EnterLive msg:" + identifier + " name :" + nickname);
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onMemberJoin(identifier, nickname, faceUrl,level);
                    break;
                case TCConstants.IMCMD_ExitLive:
                    Log.d(TAG, "getr IMCMD_ExitLive msg:" + identifier + " name :" + nickname);
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onMemberQuit(identifier, nickname);
                    break;
                case TCConstants.IMCMD_DANMU:
                    String s = json.getString(TCConstants.DANMU_TEXT);
                    JSONObject jsonObject = new JSONObject(s);
                    String textMsg = jsonObject.getString(TCConstants.TEXTMSG);
                    level = jsonObject.getString(TCConstants.LEVEL);
                    Log.d(TAG, "getr IMCMD_DANMU msg:" + identifier + " text :" + textMsg);
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onReceiveDanmu(textMsg, nickname, faceUrl,identifier,level);
                    break;
                case TCConstants.IMCMD_GIFT:
                    String giftMsg = json.getString(TCConstants.DANMU_TEXT);
                    if (null != mTCChatRoomListener){
                         mTCChatRoomListener.onGiftSend(giftMsg,identifier,nickname,faceUrl,level);
                    }
                    break;
                case TCConstants.IMCMD_ROBOT:
                    String nikeName = json.getString(TCConstants.ROBOT_NAME);
                    String bg_img_url = json.getString(TCConstants.ROB0T_FULLHEADIMG);
                    String user_id = json.getString(TCConstants.ROBOT_USER_ID);
                    String identity = json.getString(TCConstants.ROBOT_IDENTITY);
                    if (mTCChatRoomListener != null){
                        Robot robot = new Robot();
                        robot.setNickname(nikeName);
                        robot.setFull_head_image(bg_img_url);
                        robot.setUser_id(user_id);
                        robot.setIdentity(identity);
                        mTCChatRoomListener.onAddRobot(robot);
                    }
                    break;
                case TCConstants.IMCMD_ROBOT_ALL:
                    String robotsMsg = json.getString(TCConstants.ROBOT_ALL);
                    String size = json.getString(TCConstants.YETROBOTSIZE);
                    if (null != mTCChatRoomListener){
                        mTCChatRoomListener.onRobotAll(robotsMsg,size,identifier,nickname,faceUrl);
                    }
                    break;
                case TCConstants.IMCMD_ROBOT_PRAISE:
                    String nikeName1 = json.getString(TCConstants.ROBOT_NAME);
                    String bg_img_url1 = json.getString(TCConstants.ROB0T_FULLHEADIMG);
                    String user_id1 = json.getString(TCConstants.ROBOT_USER_ID);
                    String identity1 = json.getString(TCConstants.ROBOT_IDENTITY);
                    if (mTCChatRoomListener != null){
                        Robot robot = new Robot();
                        robot.setNickname(nikeName1);
                        robot.setFull_head_image(bg_img_url1);
                        robot.setUser_id(user_id1);
                        robot.setIdentity(identity1);
                        mTCChatRoomListener.onRobotPraise(robot);
                    }
                    break;
                case TCConstants.IMCMD_ROBOT_CHAT:
                    String nikeName2 = json.getString(TCConstants.ROBOT_NAME);
                    String bg_img_url2 = json.getString(TCConstants.ROB0T_FULLHEADIMG);
                    String user_id2 = json.getString(TCConstants.ROBOT_USER_ID);
                    String chat2 = json.getString(TCConstants.ROBOT_CHAT);
                    String identity2 = json.getString(TCConstants.ROBOT_IDENTITY);
                    if (mTCChatRoomListener != null){
                        Robot robot = new Robot();
                        robot.setNickname(nikeName2);
                        robot.setFull_head_image(bg_img_url2);
                        robot.setUser_id(user_id2);
                        robot.setLanguage(chat2);
                        robot.setIdentity(identity2);
                        mTCChatRoomListener.onRobotChat(robot);
                    }
                    break;
                case TCConstants.IMCMD_FOLLOW:
                    if (mTCChatRoomListener != null) {
                        mTCChatRoomListener.onFollow(identifier, nickname,level);
                    }
                    break;
                case TCConstants.IMCMD_SHUTUP:
                    if (mTCChatRoomListener != null){
                        String msg = json.getString(TCConstants.SHUT_UP);
                        mTCChatRoomListener.onShutUp(msg);
                    }
                    break;

                case  TCConstants.IMCMD_SHARE_MSG:
                    if (mTCChatRoomListener != null){
                        String share = json.getString(TCConstants.DANMU_TEXT);
                        mTCChatRoomListener.onShowShareMsg(identifier,nickname,level,share);
                    }
                    break;

                default:
                    break;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException ex) {
            // 异常处理代码
        }
    }


    /**
     * 文本消息解析
     *
     * @param elem 消息体
     * @param name 名称
     *             String msg = textElem.getText();
    if (msg.startsWith("#*#")){//普通
    String m = msg.replace("#*#","");
    mTCChatRoomListener.onReceiveTextMsg(m, name,jjId ,TCConstants.TEXT_TYPE);
    }else if (msg.startsWith("*#*")){//弹幕
    String n = msg.replace("*#*","");
    mTCChatRoomListener.onReceiveDanmu(n, name,faceurl,jjId);
    }
     */
    private void handleTextMessage(TIMElem elem, String name,String jjId) {

//        TIMTextElem textElem = (TIMTextElem) elem;

//        TCConstants.CMD_KEY, cmd);
//        sendJson.put(TCConstants.DANMU_TEXT

        String jsonString = ((TIMTextElem) elem).getText();
        Log.i(TAG, "text msg  " + jsonString);
        try {
            JSONTokener jsonParser = new JSONTokener(jsonString);
            JSONObject json = (JSONObject) jsonParser.nextValue();
//            int action = (int) json.get(TCConstants.CMD_KEY);
            String textMsg = json.getString(TCConstants.TEXTMSG);
            String level = json.getString(TCConstants.LEVEL);
            if (json.has(TCConstants.CMD_KEY)){
                int action = (int) json.get(TCConstants.CMD_KEY);
                switch (action){
                    case TCConstants.IMCMD_PAILN_TEXT:
                        if (mTCChatRoomListener != null) {
                            mTCChatRoomListener.onReceiveTextMsg(textMsg, name,jjId, level ,TCConstants.TEXT_TYPE);
                        }
                    break;
                }

            }else{
                if (mTCChatRoomListener != null) {
                    mTCChatRoomListener.onReceiveTextMsg(textMsg, name,jjId, level ,TCConstants.TEXT_TYPE);
                }
            }

        }catch (ClassCastException e) {
//                String senderId = timUserProfile.getIdentifier();
//                String nickname = timUserProfile.getNickName();
//                String headPic = timUserProfile.getFaceUrl();
//                nickname = TextUtils.isEmpty(nickname) ? senderId : nickname;
//                mTCChatRoomListener.onReceiveMsg(TCConstants.IMCMD_PAILN_TEXT,
//                        new TCSimpleUserInfo(senderId, nickname, headPic), ((TIMTextElem) elem).getText());
        }
        catch (JSONException e) {
            e.printStackTrace();
            // 异常处理代码
            if (mTCChatRoomListener != null) {
                mTCChatRoomListener.onReceiveTextMsg(jsonString, name,jjId, "1" ,TCConstants.TEXT_TYPE);
            }
        }

//        if (mTCChatRoomListener != null) {
//            mTCChatRoomListener.onReceiveTextMsg(textElem.getText(), name,jjId, "1" ,TCConstants.TEXT_TYPE);
//        }
    }

    /**
     * 消息循环监听类
     */
    public interface TCChatRoomListener {

        /**
         * 加入群组回调
         *
         * @param code 错误码，成功时返回0，失败时返回相应错误码
         * @param msg  返回信息，成功时返回群组Id，失败时返回相应错误信息
         */
        void onJoinGroupCallback(int code, String msg);

        /**
         * 退出群组回调，一般不需要对此回调做处理（Player端在回调时一般已经退出）
         *
         * @param code 错误码，成功时返回0，失败时返回相应错误码
         * @param msg  返回信息，成功时返回群组Id，失败时返回相应错误信息
         */
        void onQuitGroupCallback(int code, String msg);

        /**
         * 发送消息结果回调
         *
         * @param code       错误码，成功时返回0，失败时返回相应错误码
         * @param timMessage 发送的TIM消息
         */
        void onSendMsgCallback(int code,int msgType, TIMMessage timMessage,String parm);

        /**
         * 文本消息回调
         *
         * @param text 消息内容
         * @param name 发送者昵称
         * @param type 消息类型
         */
        void onReceiveTextMsg(String text, String name,String userId,String level, int type);

        /**
         * 成员加入回调，使用Custom消息实现（定制消息后台杀进程退出时不会发送消息）
         *
         * @param userId   加入成员ID
         * @param nickname 加入成员昵称，当昵称为空时昵称为成员ID
         * @param faceurl  加入成员头像链接
         */
        void onMemberJoin(String userId, String nickname, String faceurl,String level);

        /**
         * 成员退出回调，使用Custom消息实现（定制消息后台杀进程退出时不会发送消息）
         *
         * @param userId   退出成员ID
         * @param nickname 退出成员昵称
         */
        void onMemberQuit(String userId, String nickname);

        /**
         * 弹幕消息回调，使用Custom消息实现
         *
         * @param text     弹幕消息内容
         * @param nickname 发送者昵称
         * @param faceurl  发送者头像
         */
        void onReceiveDanmu(String text, String nickname, String faceurl,String identity,String level);

        /**
         * 群组删除回调，在主播群组解散时被调用
         */
        void onGroupDelete(String groupId);

        /**
         * 点赞消息回调，使用Custom消息实现
         *
         * @param userId   点赞人ID
         * @param nickname 点赞人昵称
         */
        void onPraise(String userId, String nickname,String level);

        /**
         * 礼物消息回调
         */
        void onGiftSend(String giftMsg,String id,String user_name,String user_avatar,String level);


        /**
         * 机器人消息回调
         */
        void onAddRobot(Robot robot);

        /**
         * 机器人发送消息状态回调
         * @param i
         * @param cmd
         * @param o
         * @param robot
         */
        void onSendRobotCallBack(int i, int cmd, Object o, Robot robot);

        /**
         * 发送所有的机器人给新来的用户
         * @param robotsMsg
         * @param identifier
         * @param nickname
         * @param faceUrl
         */
        void onRobotAll(String robotsMsg,String size, String identifier, String nickname, String faceUrl);

        /**
         *  机器人点赞
         * @param robot
         */
        void onRobotPraise(Robot robot);

        /**
         * 机器人说话
         * @param robot
         */
        void onRobotChat(Robot robot);

        /**
         * 关注主播
         * @param identifier
         * @param nickname
         */
        void onFollow(String identifier, String nickname,String level);

        /**
         * 禁言用户
         * @param msg
         */
        void onShutUp(String msg);

        /**
         * 系统通知，礼物飘屏
         * @param str
         */
        void onShowFlyGift(String str);

        /**
         * 分享主播后的消息提示
         * @param identifier
         * @param nickname
         * @param level
         * @param share
         */
        void onShowShareMsg(String identifier, String nickname, String level, String share);
    }
}
