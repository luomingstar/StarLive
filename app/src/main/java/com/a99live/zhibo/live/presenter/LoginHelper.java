package com.a99live.zhibo.live.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.a99live.zhibo.live.utils.TCConstants;
import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMUser;

/**
 * Created by fuyk on 2016/9/21.
 */
public class LoginHelper {

    private IMLoginCallback imLoginCallback;

    private static class TCLoginMgrHolder {
        private static LoginHelper instance = new LoginHelper();
    }

    public static LoginHelper getInstance() {
        return TCLoginMgrHolder.instance;
    }

    public void setIMLoginCallback(@NonNull IMLoginCallback imLoginCallback) {
        this.imLoginCallback = imLoginCallback;
    }

    public void removeIMLoginCallback() {
        this.imLoginCallback = null;
    }

    /**
     * imsdk登录接口
     * @param identify 用户id（tls验证成功返回id）
     * @param userSig 用户签名（由imsdk生成）
     */
    public void imLogin(@NonNull String identify,@NonNull String userSig) {
        TIMUser user = new TIMUser();
        user.setAccountType(String.valueOf(TCConstants.IMSDK_ACCOUNT_TYPE));
        user.setAppIdAt3rd(String.valueOf(TCConstants.IMSDK_APPID));
        user.setIdentifier(identify);
//        TIMManager.getInstance().init(LiveZhiBoApplication.getApp());
        //发起登录请求
        TIMManager.getInstance().login(TCConstants.IMSDK_APPID, user, userSig, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                if(null != imLoginCallback)
                    imLoginCallback.onIMLoginError(i, s);
            }

            @Override
            public void onSuccess() {
                if(null != imLoginCallback)
                    imLoginCallback.onIMLoginSuccess();
            }
        });
    }


    /**
     * IMSDK Login回调
     */
    public interface IMLoginCallback {

        /**
         * IMSDK登录成功
         */
        void onIMLoginSuccess();

        /**
         * IMSDK登录失败
         * @param code 错误码
         * @param msg 错误信息
         */
        void onIMLoginError(int code, String msg);

    }

    public void imLoginOut(){
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.d("livelog",""+s);
            }

            @Override
            public void onSuccess() {
                Log.d("livelog","success");
            }
        });
    }
}
