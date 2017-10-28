package com.a99live.zhibo.live.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fuyk on 2016/10/13.
 */
public class WechatEntity {


    /**
     * code : 0
     * msg : success
     * data : {"appid":"wx8af34fd3de90a8f9","partnerid":"1398711902","prepayid":"wx20161014110950e013e851300130645936","package":"Sign=WXPay","noncestr":"iax0e8hBu52rf4FF","timestamp":1476414590,"sign":"88B7B7766EB2152F72542F86DC253AAE"}
     * append : {"appid":"wx8af34fd3de90a8f9","device_info":"00000000178db4f50a3737f70033c587","mch_id":"1398711902","nonce_str":"iax0e8hBu52rf4FF","prepay_id":"wx20161014110950e013e851300130645936","result_code":"SUCCESS","return_code":"SUCCESS","return_msg":"OK","sign":"D6E20AF14485EED7741FC154036300DC","trade_type":"APP"}
     */

    private int code;
    private String msg;
    /**
     * appid : wx8af34fd3de90a8f9
     * partnerid : 1398711902
     * prepayid : wx20161014110950e013e851300130645936
     * package : Sign=WXPay
     * noncestr : iax0e8hBu52rf4FF
     * timestamp : 1476414590
     * sign : 88B7B7766EB2152F72542F86DC253AAE
     */

    private DataBean data;
    /**
     * appid : wx8af34fd3de90a8f9
     * device_info : 00000000178db4f50a3737f70033c587
     * mch_id : 1398711902
     * nonce_str : iax0e8hBu52rf4FF
     * prepay_id : wx20161014110950e013e851300130645936
     * result_code : SUCCESS
     * return_code : SUCCESS
     * return_msg : OK
     * sign : D6E20AF14485EED7741FC154036300DC
     * trade_type : APP
     */

    private AppendBean append;

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

    public AppendBean getAppend() {
        return append;
    }

    public void setAppend(AppendBean append) {
        this.append = append;
    }

    public static class DataBean {
        private String appid;
        private String partnerid;
        private String prepayid;
        @SerializedName("package")
        private String packageX;
        private String noncestr;
        private int timestamp;
        private String sign;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }

    public static class AppendBean {
        private String appid;
        private String device_info;
        private String mch_id;
        private String nonce_str;
        private String prepay_id;
        private String result_code;
        private String return_code;
        private String return_msg;
        private String sign;
        private String trade_type;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getDevice_info() {
            return device_info;
        }

        public void setDevice_info(String device_info) {
            this.device_info = device_info;
        }

        public String getMch_id() {
            return mch_id;
        }

        public void setMch_id(String mch_id) {
            this.mch_id = mch_id;
        }

        public String getNonce_str() {
            return nonce_str;
        }

        public void setNonce_str(String nonce_str) {
            this.nonce_str = nonce_str;
        }

        public String getPrepay_id() {
            return prepay_id;
        }

        public void setPrepay_id(String prepay_id) {
            this.prepay_id = prepay_id;
        }

        public String getResult_code() {
            return result_code;
        }

        public void setResult_code(String result_code) {
            this.result_code = result_code;
        }

        public String getReturn_code() {
            return return_code;
        }

        public void setReturn_code(String return_code) {
            this.return_code = return_code;
        }

        public String getReturn_msg() {
            return return_msg;
        }

        public void setReturn_msg(String return_msg) {
            this.return_msg = return_msg;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTrade_type() {
            return trade_type;
        }

        public void setTrade_type(String trade_type) {
            this.trade_type = trade_type;
        }
    }
}
