package com.ctrip.platform.dal.daogen.entity;

/**
 * Created by taochen on 2019/7/3.
 */
public class TitanResponse {
    private int status;

    private String message;

    private TitanKeyInfo titanKeyInfo;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TitanKeyInfo getTitanKeyInfo() {
        return titanKeyInfo;
    }

    public void setTitanKeyInfo(TitanKeyInfo titanKeyInfo) {
        this.titanKeyInfo = titanKeyInfo;
    }
}
