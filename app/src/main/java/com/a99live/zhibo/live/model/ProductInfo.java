package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by fuyk on 2016/10/12.
 */
public class ProductInfo {


    /**
     * code : 0
     * msg : success
     * data : [{"id":"1","money":"10.00","v_amount":"20.00","v_type":"1","type":"1","g_number":"0","g_type":"0"},{"id":"3","money":"0.01","v_amount":"9999.00","v_type":"1","type":"3","g_number":"1000","g_type":"1"}]
     * append : {"Z":0}
     */

    private int code;
    private String msg;
    /**
     * Z : 0
     */

    private AppendBean append;
    /**
     * id : 1
     * money : 10.00
     * v_amount : 20.00
     * v_type : 1
     * type : 1
     * g_number : 0
     * g_type : 0
     */

    private List<DataBean> data;

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

    public AppendBean getAppend() {
        return append;
    }

    public void setAppend(AppendBean append) {
        this.append = append;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class AppendBean {
        private int Z;

        public int getZ() {
            return Z;
        }

        public void setZ(int Z) {
            this.Z = Z;
        }
    }

    public static class DataBean {
        private String id;
        private String money;
        private String v_amount;
        private String v_type;
        private String type;
        private String g_number;
        private String g_type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getV_amount() {
            return v_amount;
        }

        public void setV_amount(String v_amount) {
            this.v_amount = v_amount;
        }

        public String getV_type() {
            return v_type;
        }

        public void setV_type(String v_type) {
            this.v_type = v_type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getG_number() {
            return g_number;
        }

        public void setG_number(String g_number) {
            this.g_number = g_number;
        }

        public String getG_type() {
            return g_type;
        }

        public void setG_type(String g_type) {
            this.g_type = g_type;
        }
    }
}
