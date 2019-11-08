package com.ctrip.framework.db.cluster.entity;

/**
 * Created by taochen on 2019/11/5.
 */
public enum ResponseStatus {
    SUCCESS(0),FAIL(1);
    private int code;
    ResponseStatus(int code) {
        this.code = code;
    }
}
