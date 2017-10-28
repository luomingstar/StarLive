package com.a99live.zhibo.live.model;

import android.content.Context;
import android.text.TextUtils;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.imchatc2c.ChatActivity;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友或群聊的会话
 */
public class NomalConversation extends Conversation {


    private TIMConversation conversation;



    //最后一条消息
    private Message lastMessage;
    private AvatarName avatarName;

    public NomalConversation(TIMConversation conversation){
        this.conversation = conversation;
        type = conversation.getType();
        identify = conversation.getPeer();
    }


    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setAvatarNameCallback(AvatarName avatarNameCallback){
        this.avatarName = avatarNameCallback;
    }


    @Override
    public String getAvatar() {
        switch (type){
            case C2C:
                if (!TextUtils.isEmpty(avatar)){
                    return avatar;
                }
                FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
                avatar = profile == null?"":profile.getAvatarUrl();
                return avatar;
//                return avatar;
//            case Group:
//                return R.drawable.head_group;
        }
        return "";
    }


    /**
     * 跳转到聊天界面或会话详情
     *
     * @param context 跳转上下文
     */
    @Override
    public void navToDetail(Context context) {
        ChatActivity.navToChat(context,identify,name,avatar,type);
    }

    /**
     * 获取最后一条消息摘要
     */
    @Override
    public String getLastMessageSummary(){
        if (conversation.hasDraft()){
            TextMessage textMessage = new TextMessage(conversation.getDraft());
            if (lastMessage == null || lastMessage.getMessage().timestamp() < conversation.getDraft().getTimestamp()){
                return LiveZhiBoApplication.getContext().getString(R.string.conversation_draft) + textMessage.getSummary();
            }else{
                return lastMessage.getSummary();
            }
        }else{
            if (lastMessage == null) return "";
            return lastMessage.getSummary();
        }
    }

    /**
     * 获取名称
     */
    @Override
    public String getName() {
//        if (type == TIMConversationType.Group){
//            name=GroupInfo.getInstance().getGroupName(identify);
//            if (name.equals("")) name = identify;
//        }else{
        if (!TextUtils.isEmpty(name)){
            return name;
        }
        FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
//            name=profile == null?identify:profile.getName();
        if (profile != null){
            name=profile == null?identify:profile.getName();
        }else{
            List<String> users = new ArrayList<String>();
            users.add(identify);
            TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>(){
                @Override
                public void onError(int code, String desc){
                    //错误码code和错误描述desc，可用于定位请求失败原因
                    //错误码code列表请参见错误码表
//                    Log.e(tag, "getUsersProfile failed: " + code + " desc");
                }

                @Override
                public void onSuccess(List<TIMUserProfile> result){
//                    Log.e(tag, "getUsersProfile succ");
                    for(TIMUserProfile res : result){
//                        Log.e(tag, "identifier: " + res.getIdentifier() + " nickName: " + res.getNickName()
//                                + " remark: " + res.getRemark());
                        name = res.getNickName();
                        avatar = res.getFaceUrl();
                    }
                    if (avatarName != null){
                        avatarName.refresh();
                    }
                }
            });
        }

//        }
        return name;
    }



    /**
     * 获取未读消息数量
     */
    @Override
    public long getUnreadNum(){
        if (conversation == null) return 0;
        return conversation.getUnreadMessageNum();
    }

    /**
     * 将所有消息标记为已读
     */
    @Override
    public void readAllMessage(){
        if (conversation != null){
            conversation.setReadMessage();
        }
    }


    /**
     * 获取最后一条消息的时间
     */
    @Override
    public long getLastMessageTime() {
        if (conversation.hasDraft()){
            if (lastMessage == null || lastMessage.getMessage().timestamp() < conversation.getDraft().getTimestamp()){
                return conversation.getDraft().getTimestamp();
            }else{
                return lastMessage.getMessage().timestamp();
            }
        }
        if (lastMessage == null) return 0;
        return lastMessage.getMessage().timestamp();
    }

    /**
     * 获取会话类型
     */
    public TIMConversationType getType(){
        return conversation.getType();
    }
}
