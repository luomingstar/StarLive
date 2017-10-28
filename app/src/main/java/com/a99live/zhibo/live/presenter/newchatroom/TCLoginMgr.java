package com.a99live.zhibo.live.presenter.newchatroom;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.model.IMsigInfo;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.parser.JsonParser;
import com.a99live.zhibo.live.protocol.ImProtocol;
import com.a99live.zhibo.live.utils.TCConstants;
import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMUser;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/10/15.
 */
public class TCLoginMgr {

    private ImProtocol imProtocol = new ImProtocol();

    public TCLoginMgr() {
    }

    private static class TCLoginMgrHolder{
        private static TCLoginMgr instance = new TCLoginMgr();
    }

    public static TCLoginMgr getInstance(){return TCLoginMgrHolder.instance; }

    /**登录回调*/
    private TCLoginCallback mTCLoginCallback;

    public interface TCLoginCallback {

        /**
         * 登录成功
         */
        void onSuccess();

        /**
         * 登录失败
         * @param code 错误码
         * @param msg 错误信息
         */
        void onFailure(int code, String msg);

    }

    public void setTCLoginCallback(@NonNull TCLoginCallback tcLoginCallback) {
        this.mTCLoginCallback = tcLoginCallback;
    }

    public void removeTCLoginCallback() {
        this.mTCLoginCallback = null;
    }

    /**
     * 检查是否存在缓存，若存在则登录，反之回调onFailure
     */
    public boolean checkCacheAndLogin() {
        if (needLogin()) {
            return false;
        } else {
            getImSig();
        }
        return true;
    }

    /**
     * 检测是否需要执行登录操作
     * @return false不需要登录/true需要登录
     */
    public boolean needLogin() {
        if (TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())){
            return true;
        }else {
            return false;
        }
    }

    /**
     * imsdk登录接口
     * @param identify 用户id
     * @param userSig 用户签名
     */
    public void imLogin(@NonNull String identify,@NonNull String userSig) {
        TIMUser user = new TIMUser();
        user.setAccountType(String.valueOf(TCConstants.IMSDK_ACCOUNT_TYPE));
        user.setAppIdAt3rd(String.valueOf(TCConstants.IMSDK_APPID));
        user.setIdentifier(identify);
        TIMManager.getInstance().init(LiveZhiBoApplication.getApp());
        //发起登录请求
        TIMManager.getInstance().login(TCConstants.IMSDK_APPID, user, userSig, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                if(null != mTCLoginCallback)
                    mTCLoginCallback.onFailure(i, s);
            }

            @Override
            public void onSuccess() {
                if(null != mTCLoginCallback)
                    mTCLoginCallback.onSuccess();
            }
        });
    }

    /**
     * imsdk登出
     */
    private void imLogout() {
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e("livelog", "IMLogout fail ：" + i + " msg " + s);
            }

            @Override
            public void onSuccess() {
                Log.i("livelog", "IMLogout succ !");
            }
        });

    }

    /**IM签名*/
    private void getImSig(){
        LiveRequestParams params = new LiveRequestParams();

        imProtocol.getIMSign(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        IMsigInfo iMsigInfo = JsonParser.fromJson(s, IMsigInfo.class);
                        Log.e("livelog-Imsmig", "success-----" + s);
                        if (iMsigInfo.getCode() == 0) {
                            imLogin(iMsigInfo.getData().getUid(), iMsigInfo.getData().getSig());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("livelog-Imsmig","error----");
                    }
                });
    }

}
