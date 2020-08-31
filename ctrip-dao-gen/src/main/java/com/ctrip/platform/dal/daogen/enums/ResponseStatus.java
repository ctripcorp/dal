package com.ctrip.platform.dal.daogen.enums;

public enum ResponseStatus {
    OK(200, "OK"),
    BAD_REQUEST(400, "BAD REQUEST"),
    FORBIDDEN(403, "REQUEST FORBIDDEN"),
    ERROR(500, "UNKNOWN ERROR");

    private int status;
    private String desc;

    ResponseStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean is(int status) {
        return this.status == status;
    }
}
