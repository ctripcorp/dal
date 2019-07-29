package com.ctrip.platform.dal.daogen.entity;

/**
 * Created by taochen on 2019/7/18.
 */
public class TransactionSimple {
    private String ipAddress;

    private TransactionSimpleMessage message;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public TransactionSimpleMessage getMessage() {
        return message;
    }

    public void setMessage(TransactionSimpleMessage message) {
        this.message = message;
    }
}
