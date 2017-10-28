package com.a99live.zhibo.live.event;

/**
 * Created by fuyk on 2016/12/5.
 */
public class TagEvent {

    private String name;
    private String tagId;
    private String content;

    public TagEvent(String name, String tagId, String content) {
        this.name = name;
        this.tagId = tagId;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getTagId() {
        return tagId;
    }

    public String getContent() {
        return content;
    }
}
