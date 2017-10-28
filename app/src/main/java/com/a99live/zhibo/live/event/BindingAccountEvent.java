package com.a99live.zhibo.live.event;

/**
 * Created by fuyk on 2016/12/13.
 */

public class BindingAccountEvent {
    private String alipayAccount;

    public BindingAccountEvent(String alipayAccount) {
        this.alipayAccount = alipayAccount;
    }

    public String getAlipayAccount() {
        return alipayAccount;
    }
}
