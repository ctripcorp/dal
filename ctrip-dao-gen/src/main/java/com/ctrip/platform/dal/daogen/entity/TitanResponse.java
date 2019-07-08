package com.ctrip.platform.dal.daogen.entity;

/**
 * Created by taochen on 2019/7/3.
 */
public class TitanResponse {
    private int status;

    private String message;

    private TitanKeyInfo data;

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

    public TitanKeyInfo getData() {
        return data;
    }

    public void setData(TitanKeyInfo data) {
        this.data = data;
    }
}
