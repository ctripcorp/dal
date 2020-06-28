package com.ctrip.platform.dal.daogen.entity;

/**
 * Created by taochen on 2019/7/26.
 */
public class TitanKeyPluginsResponse {
    private int status;

    private String message;

    private TitanKeyData data;

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

    public TitanKeyData getData() {
        return data;
    }

    public void setData(TitanKeyData data) {
        this.data = data;
    }
}
