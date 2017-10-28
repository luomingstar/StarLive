package com.a99live.zhibo.live.activity.imchatc2c;


import com.a99live.zhibo.live.model.CustomMessage;
import com.a99live.zhibo.live.model.FileMessage;
import com.a99live.zhibo.live.model.ImageMessage;
import com.a99live.zhibo.live.model.Message;
import com.a99live.zhibo.live.model.TextMessage;
import com.a99live.zhibo.live.model.VideoMessage;
import com.a99live.zhibo.live.model.VoiceMessage;
import com.tencent.TIMMessage;

/**
 * 消息工厂
 */
public class MessageFactory {

    private MessageFactory() {}


    /**
     * 消息工厂方法
     */
    public static Message getMessage(TIMMessage message){
        switch (message.getElement(0).getType()){
            case Text:
            case Face:
                return new TextMessage(message);
            case Image:
                return new ImageMessage(message);
            case Sound:
                return new VoiceMessage(message);
            case Video:
                return new VideoMessage(message);
//            case GroupTips:
//                return new GroupTipMessage(message);
            case File:
                return new FileMessage(message);
            case Custom:
                return new CustomMessage(message);
            default:
                return null;
        }
    }



}
