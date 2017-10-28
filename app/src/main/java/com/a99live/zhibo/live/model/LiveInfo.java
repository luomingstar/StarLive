package com.a99live.zhibo.live.model;

/**
 * Created by fuyk on 2016/8/29.
 */
public class LiveInfo {

    private int creatTime;
    private String title;
    private String cover;
    private LBS lbs;
    private HOST host;
    private String chatRoomId;
    private int avRoomId;
    private int timeSpan;
    private int watchCount;

    public int getCreatTime() {
        return creatTime;
    }

    public String getTitle() {
        return title;
    }

    public String getCover() {
        return cover;
    }

    public LBS getLbs() {
        return lbs;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public HOST getHost() {
        return host;
    }

    public void setAvRoomId(int avRoomId) {
        this.avRoomId = avRoomId;
    }

    public int getAvRoomId() {
        return avRoomId;
    }

    public int getTimeSpan() {
        return timeSpan;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public class LBS{
        private double longitude;//经度
        private double latitue;//纬度
        private String address;

        public double getLongitude() {
            return longitude;
        }

        public double getLatitue() {
            return latitue;
        }

        public String getAddress() {
            return address;
        }

        @Override
        public String toString() {
            return "LBS{" +
                    "longitude=" + longitude +
                    ", latitue=" + latitue +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

    public class HOST{
        private String uid;
        private String avatar;
        private String username;

        public String getUid() {
            return uid;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return "HOST{" +
                    "uId='" + uid + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", username='" + username + '\'' +
                    '}';
        }
    }

}
