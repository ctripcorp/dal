package com.ctrip.framework.db.cluster.entity.enums;

public enum Deleted {

    un_deleted(0),
    deleted(1),;

    Deleted(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }
}
