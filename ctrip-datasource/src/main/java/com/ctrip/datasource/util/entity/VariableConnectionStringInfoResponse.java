package com.ctrip.datasource.util.entity;

public class VariableConnectionStringInfoResponse {
    private String message;

    private VariableConnectionStringInfo data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VariableConnectionStringInfo getData() {
        return data;
    }

    public void setData(VariableConnectionStringInfo data) {
        this.data = data;
    }
}
