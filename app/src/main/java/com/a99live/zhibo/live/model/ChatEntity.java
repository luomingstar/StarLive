package com.a99live.zhibo.live.model;

import android.text.TextUtils;

/**
 * Created by fuyk on 2016/9/10.
 */
public class ChatEntity {
    private String SendName;
    private String context;
    private String identity;
    private String level;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        if (!TextUtils.isEmpty(level)) {
            this.level = level;
        }else{
            this.level = "1";
        }
    }

    private int type;

    public ChatEntity() {
        super();
        level = "1";
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getSendName() {
        return SendName;
    }

    public void setSendName(String sendName) {
        SendName = sendName;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatEntity)) return false;

        ChatEntity that = (ChatEntity) o;

        if (getType() != that.getType()) return false;
        if (SendName != null ? !SendName.equals(that.SendName) : that.SendName != null)
            return false;
        return getContext() != null ? getContext().equals(that.getContext()) : that.getContext() == null;

    }

    @Override
    public int hashCode() {
        int result = SendName != null ? SendName.hashCode() : 0;
        result = 31 * result + (getContext() != null ? getContext().hashCode() : 0);
        result = 31 * result + getType();
        return result;
    }
}
