package com.a99live.zhibo.live.protocol;

import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.base.BaseProtocol;

import rx.Observable;

/**
 * Created by fuyk on 2016/9/1.
 */
public class UserProtocol extends BaseProtocol {
    @Override
    protected String getPath() {
        return "/v1/user/";
    }

    /**
     * 获取短信验证码
     * 手机号
     */
    public Observable<String> getVerifySms(LiveRequestParams params){
        String uri = getPath() + "code/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    /**
     * 用户登录
     * 手机号 ；验证码
     */
    public Observable<String> getUserLogin(LiveRequestParams params){
        String uri = getPath() + "login/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 用户登出
     */
    public Observable<String> getUserLoginOut(LiveRequestParams params){
        String uri = getPath() + "logout/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 修改个人资料
     *  avatar : 头像地址 （可选）
        nickname: 昵称 （可选）
        gender: 性别  1-男 2-女 （可选）
        birthday: 出生年月   1987-08-05  （可选）
        star: 星座   狮子 （可选）
        region： 地区 （可选）
        sign：签名（可选）
     */
    public Observable<String> SetUserInfo(LiveRequestParams params){
        String uri = getPath() + "set/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    /**
     * 个人主页
     * 参数：用户id
     */
    public Observable<String> MyHomeInfo(LiveRequestParams params){
        String uri = getPath() + "uc/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }


    /**
     * 他人99主页
     * 参数：他人id
     */
    public Observable<String> OtherHomeInfo(LiveRequestParams params){
        String uri = getPath() + "home/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    /**
     * 我的直播列表
     * 无参数
     */
    public Observable<String> MyLiveInfo(LiveRequestParams params){
        String uri = getPath() + "myvideo";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    /**
     * 我的关注列表
     * 无参数
     */
    public Observable<String> MyAttentionList(LiveRequestParams params){
        String uri = getPath() + "myfollow";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    /**
     * 我的粉丝列表
     * 无参数
     */
    public Observable<String> MyFansList(LiveRequestParams params){
        String uri = getPath() + "myfans";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 点赞用户列表（暂不用）
     */
    public Observable<String> MyLike(LiveRequestParams params){
        String uri = getPath() + "mylikefans";
        return createObservable(uri, LiveHttpClient.METHOD_GET,params);
    }

    /**
     * 关注接口
     * 参数：他人用户id
     */
    public Observable<String> getFollow(LiveRequestParams params){
        String uri = getPath() + "follow/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 取消关注接口
     * 参数：他人用户id
     */
    public Observable<String> getUnFollow(LiveRequestParams params){
        String uri = getPath() + "cancelFollow/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 用户认证
     * 参数：姓名：name
     *      身份证号：id_card
     *      身份证图片地址：id_img
     */
    public Observable<String> getUserApprove(LiveRequestParams params){
        String uri = getPath() + "verify/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 提现接口
     */
    public Observable<String> getWithdraw(LiveRequestParams params){
        String uri = getPath() + "withdraws/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 提现记录接口
     */
    public Observable<String> getWithDrawslist(LiveRequestParams params){
        String uri = getPath() + "withdrawslist/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    /**
     * 绑定账户
     * 参数：mobile
     *      sms
     *      account
     *      name
     */
    public Observable<String> bindingAccount(LiveRequestParams params){
        String uri = getPath() + "account/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 我的等级
     */
    public Observable<String> getMyGrades(LiveRequestParams params){
        String uri = getPath() + "myLevel/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 推荐标签
     * 参数：tags
     */
    public Observable<String> getInterest(LiveRequestParams params){
        String uri = getPath() + "interest/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 批量关注
     * 参数：fids
     */
    public Observable<String> getFollowBatch(LiveRequestParams params){
        String uri = getPath() + "followbatch/";
        return createObservable(uri, LiveHttpClient.METHOD_POST, params);
    }

    /**
     * 我的预约v1/user/myReserve
     */
    public Observable<String> getMyReserve(LiveRequestParams params){
        String uri = getPath() + "myReserve/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

    public Observable<String> getMyTip(LiveRequestParams params){
        String uri = getPath() + "tip/";
        return createObservable(uri, LiveHttpClient.METHOD_POST,params);
    }

}
