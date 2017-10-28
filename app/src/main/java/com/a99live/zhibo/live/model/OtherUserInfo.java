package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by fuyk on 2016/10/18.
 */
public class OtherUserInfo {


    /**
     * code : 0
     * msg : success
     * data : {"user":{"user_id":"13","identity":"16678","mobile":"18311328380","nickname":"美人来了","avatar":"http://99live-10063116.image.myqcloud.com/1e30d6894d8ff19fdb355103bf8d6aad?imageMogr2/thumbnail/x200","gender":"女","birthday":"1971-05-01","region":"北京市市辖区朝阳区","sign":"成长就是踉踉跄跄中成长，跌跌撞撞中坚强","star":"双子","verify":"未认证","status":"正常","follow":"1","fans":"1","like":"0","is_default_avatar":"N","ucode":1092,"avatar_id":"1e30d6894d8ff19fdb355103bf8d6aad","tags":[]},"isFollow":0,"videoList":[{"id":"531","room_id":"970","create_time":"2016-10-18 17:58:53","room_title":"记录","online_num":"0","bg_img_url":"http://99live-10063116.image.myqcloud.com/1e30d6894d8ff19fdb355103bf8d6aad?imageMogr2/thumbnail/x750","video_url":"[\"http:\\/\\/200026474.vod.myqcloud.com\\/200026474_402b0b62c0ad450793cb98ce86826170.f0.flv\"]","in_total":"0","user_id":"13","nickname":"美人来了","ucode":1092,"is_live":0,"format_time":"13分钟前"},{"id":"522","room_id":"961","create_time":"2016-10-18 16:45:27","room_title":"99直播","online_num":"1","bg_img_url":"http://99live-10063116.image.myqcloud.com/1e30d6894d8ff19fdb355103bf8d6aad?imageMogr2/thumbnail/x750","video_url":"[\"http:\\/\\/200026474.vod.myqcloud.com\\/200026474_e79976e3541b451e84a909389e6b593c.f0.flv\"]","in_total":"2","user_id":"13","nickname":"美人来了","ucode":1092,"is_live":0,"format_time":"1小时前"},{"id":"471","room_id":"910","create_time":"2016-10-17 16:20:11","room_title":"99直播","online_num":"0","bg_img_url":"http://99live-10063116.image.myqcloud.com/1e30d6894d8ff19fdb355103bf8d6aad?imageMogr2/thumbnail/x750","video_url":"[\"http:\\/\\/200026474.vod.myqcloud.com\\/200026474_257ec3989b714233ba42b650c50cdad1.f0.flv\"]","in_total":"0","user_id":"13","nickname":"美人来了","ucode":1092,"is_live":0,"format_time":"1天前"}]}
     * append : []
     */

    private int code;
    private String msg;
    /**
     * user : {"user_id":"13","identity":"16678","mobile":"18311328380","nickname":"美人来了","avatar":"http://99live-10063116.image.myqcloud.com/1e30d6894d8ff19fdb355103bf8d6aad?imageMogr2/thumbnail/x200","gender":"女","birthday":"1971-05-01","region":"北京市市辖区朝阳区","sign":"成长就是踉踉跄跄中成长，跌跌撞撞中坚强","star":"双子","verify":"未认证","status":"正常","follow":"1","fans":"1","like":"0","is_default_avatar":"N","ucode":1092,"avatar_id":"1e30d6894d8ff19fdb355103bf8d6aad","tags":[]}
     * isFollow : 0
     * videoList : [{"id":"531","room_id":"970","create_time":"2016-10-18 17:58:53","room_title":"记录","online_num":"0","bg_img_url":"http://99live-10063116.image.myqcloud.com/1e30d6894d8ff19fdb355103bf8d6aad?imageMogr2/thumbnail/x750","video_url":"[\"http:\\/\\/200026474.vod.myqcloud.com\\/200026474_402b0b62c0ad450793cb98ce86826170.f0.flv\"]","in_total":"0","user_id":"13","nickname":"美人来了","ucode":1092,"is_live":0,"format_time":"13分钟前"},{"id":"522","room_id":"961","create_time":"2016-10-18 16:45:27","room_title":"99直播","online_num":"1","bg_img_url":"http://99live-10063116.image.myqcloud.com/1e30d6894d8ff19fdb355103bf8d6aad?imageMogr2/thumbnail/x750","video_url":"[\"http:\\/\\/200026474.vod.myqcloud.com\\/200026474_e79976e3541b451e84a909389e6b593c.f0.flv\"]","in_total":"2","user_id":"13","nickname":"美人来了","ucode":1092,"is_live":0,"format_time":"1小时前"},{"id":"471","room_id":"910","create_time":"2016-10-17 16:20:11","room_title":"99直播","online_num":"0","bg_img_url":"http://99live-10063116.image.myqcloud.com/1e30d6894d8ff19fdb355103bf8d6aad?imageMogr2/thumbnail/x750","video_url":"[\"http:\\/\\/200026474.vod.myqcloud.com\\/200026474_257ec3989b714233ba42b650c50cdad1.f0.flv\"]","in_total":"0","user_id":"13","nickname":"美人来了","ucode":1092,"is_live":0,"format_time":"1天前"}]
     */

    private DataBean data;
    private List<?> append;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public List<?> getAppend() {
        return append;
    }

    public void setAppend(List<?> append) {
        this.append = append;
    }

    public static class DataBean {
        /**
         * user_id : 13
         * identity : 16678
         * mobile : 18311328380
         * nickname : 美人来了
         * avatar : http://99live-10063116.image.myqcloud.com/1e30d6894d8ff19fdb355103bf8d6aad?imageMogr2/thumbnail/x200
         * gender : 女
         * birthday : 1971-05-01
         * region : 北京市市辖区朝阳区
         * sign : 成长就是踉踉跄跄中成长，跌跌撞撞中坚强
         * star : 双子
         * verify : 未认证
         * status : 正常
         * follow : 1
         * fans : 1
         * like : 0
         * is_default_avatar : N
         * ucode : 1092
         * avatar_id : 1e30d6894d8ff19fdb355103bf8d6aad
         * tags : []
         */

        private UserBean user;
        private int isFollow;
        /**
         * id : 531
         * room_id : 970
         * create_time : 2016-10-18 17:58:53
         * room_title : 记录
         * online_num : 0
         * bg_img_url : http://99live-10063116.image.myqcloud.com/1e30d6894d8ff19fdb355103bf8d6aad?imageMogr2/thumbnail/x750
         * video_url : ["http:\/\/200026474.vod.myqcloud.com\/200026474_402b0b62c0ad450793cb98ce86826170.f0.flv"]
         * in_total : 0
         * user_id : 13
         * nickname : 美人来了
         * ucode : 1092
         * is_live : 0
         * format_time : 13分钟前
         */

        private List<VideoListBean> videoList;

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public int getIsFollow() {
            return isFollow;
        }

        public void setIsFollow(int isFollow) {
            this.isFollow = isFollow;
        }

        public List<VideoListBean> getVideoList() {
            return videoList;
        }

        public void setVideoList(List<VideoListBean> videoList) {
            this.videoList = videoList;
        }

        public static class UserBean {
            private String user_id;
            private String identity;
            private String mobile;
            private String nickname;
            private String avatar;
            private String gender;
            private String birthday;
            private String region;
            private String sign;
            private String star;
            private String verify;
            private String status;
            private String follow;
            private String fans;
            private String like;
            private String is_default_avatar;
            private int ucode;
            private String avatar_id;
            private List<?> tags;

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getIdentity() {
                return identity;
            }

            public void setIdentity(String identity) {
                this.identity = identity;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public String getRegion() {
                return region;
            }

            public void setRegion(String region) {
                this.region = region;
            }

            public String getSign() {
                return sign;
            }

            public void setSign(String sign) {
                this.sign = sign;
            }

            public String getStar() {
                return star;
            }

            public void setStar(String star) {
                this.star = star;
            }

            public String getVerify() {
                return verify;
            }

            public void setVerify(String verify) {
                this.verify = verify;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getFollow() {
                return follow;
            }

            public void setFollow(String follow) {
                this.follow = follow;
            }

            public String getFans() {
                return fans;
            }

            public void setFans(String fans) {
                this.fans = fans;
            }

            public String getLike() {
                return like;
            }

            public void setLike(String like) {
                this.like = like;
            }

            public String getIs_default_avatar() {
                return is_default_avatar;
            }

            public void setIs_default_avatar(String is_default_avatar) {
                this.is_default_avatar = is_default_avatar;
            }

            public int getUcode() {
                return ucode;
            }

            public void setUcode(int ucode) {
                this.ucode = ucode;
            }

            public String getAvatar_id() {
                return avatar_id;
            }

            public void setAvatar_id(String avatar_id) {
                this.avatar_id = avatar_id;
            }

            public List<?> getTags() {
                return tags;
            }

            public void setTags(List<?> tags) {
                this.tags = tags;
            }
        }

        public static class VideoListBean {
            private String id;
            private String room_id;
            private String create_time;
            private String room_title;
            private String online_num;
            private String bg_img_url;
            private String video_url;
            private String in_total;
            private String user_id;
            private String nickname;
            private int ucode;
            private int is_live;
            private String format_time;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getRoom_id() {
                return room_id;
            }

            public void setRoom_id(String room_id) {
                this.room_id = room_id;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getRoom_title() {
                return room_title;
            }

            public void setRoom_title(String room_title) {
                this.room_title = room_title;
            }

            public String getOnline_num() {
                return online_num;
            }

            public void setOnline_num(String online_num) {
                this.online_num = online_num;
            }

            public String getBg_img_url() {
                return bg_img_url;
            }

            public void setBg_img_url(String bg_img_url) {
                this.bg_img_url = bg_img_url;
            }

            public String getVideo_url() {
                return video_url;
            }

            public void setVideo_url(String video_url) {
                this.video_url = video_url;
            }

            public String getIn_total() {
                return in_total;
            }

            public void setIn_total(String in_total) {
                this.in_total = in_total;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public int getUcode() {
                return ucode;
            }

            public void setUcode(int ucode) {
                this.ucode = ucode;
            }

            public int getIs_live() {
                return is_live;
            }

            public void setIs_live(int is_live) {
                this.is_live = is_live;
            }

            public String getFormat_time() {
                return format_time;
            }

            public void setFormat_time(String format_time) {
                this.format_time = format_time;
            }
        }
    }
}
