package com.a99live.zhibo.live.model;

import java.util.List;

/**
 * Created by fuyk on 2016/10/13.
 */
public class OrderInfo {


    /**
     * code : 0
     * msg : success
     * data : {"user_id":"5","order_code":"GIFT_20161013163421_46866d19","bus_type":"gift","bus_id":1,"amount":"10.00","remark":"product 1","create_time":"2016-10-13 16:34:21","update_time":"2016-10-13 16:34:21","order_id":"7"}
     * append : []
     */

    private int code;
    private String msg;
    /**
     * user_id : 5
     * order_code : GIFT_20161013163421_46866d19
     * bus_type : gift
     * bus_id : 1
     * amount : 10.00
     * remark : product 1
     * create_time : 2016-10-13 16:34:21
     * update_time : 2016-10-13 16:34:21
     * order_id : 7
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
        private String user_id;
        private String order_code;
        private String bus_type;
        private int bus_id;
        private String amount;
        private String remark;
        private String create_time;
        private String update_time;
        private String order_id;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getOrder_code() {
            return order_code;
        }

        public void setOrder_code(String order_code) {
            this.order_code = order_code;
        }

        public String getBus_type() {
            return bus_type;
        }

        public void setBus_type(String bus_type) {
            this.bus_type = bus_type;
        }

        public int getBus_id() {
            return bus_id;
        }

        public void setBus_id(int bus_id) {
            this.bus_id = bus_id;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }
    }
}
