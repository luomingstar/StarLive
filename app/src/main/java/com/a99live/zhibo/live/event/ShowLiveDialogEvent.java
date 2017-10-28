package com.a99live.zhibo.live.event;

/**
 * Created by JJGCW on 2016/10/24.
 */

public class ShowLiveDialogEvent {

    private String roomId;
    private String content;
    private String ucode;
    public ShowLiveDialogEvent(String roomId,String ucode,String content){
        this.roomId = roomId;
        this.content = content;
        this.ucode = ucode;
    }

    public String getUcode(){
        return ucode;
    }

    public String getRoomId(){
        return roomId;
    }

    public String getContent(){
        return  content;
    }


}
