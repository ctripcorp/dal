package com.ctrip.platform.dal.daogen.entity;

public class Response {
    private String status;
    private ResponseData[] data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ResponseData[] getData() {
        return data;
    }

    public void setData(ResponseData[] data) {
        this.data = data;
    }
}






