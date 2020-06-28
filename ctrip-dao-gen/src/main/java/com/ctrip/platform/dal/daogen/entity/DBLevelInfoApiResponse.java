package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

public class DBLevelInfoApiResponse {
    private List<DBLevelInfo> data;

    private boolean success;

    public List<DBLevelInfo> getData() {
        return data;
    }

    public void setData(List<DBLevelInfo> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
