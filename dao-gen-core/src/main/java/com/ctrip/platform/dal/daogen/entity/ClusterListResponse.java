package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

public class ClusterListResponse {

    private int status;

    private String message;

    private List<ClusterBrief> result;

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

    public List<ClusterBrief> getResult() {
        return result;
    }

    public void setResult(List<ClusterBrief> result) {
        this.result = result;
    }
}
