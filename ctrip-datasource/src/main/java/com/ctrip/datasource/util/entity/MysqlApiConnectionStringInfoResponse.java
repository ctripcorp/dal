package com.ctrip.datasource.util.entity;

public class MysqlApiConnectionStringInfoResponse {
    private String message;

    private MysqlApiConnectionStringInfo data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MysqlApiConnectionStringInfo getData() {
        return data;
    }

    public void setData(MysqlApiConnectionStringInfo data) {
        this.data = data;
    }
}
