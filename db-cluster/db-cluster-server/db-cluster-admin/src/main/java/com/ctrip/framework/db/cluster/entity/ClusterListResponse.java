package com.ctrip.framework.db.cluster.entity;

import java.util.List;

/**
 * Created by taochen on 2019/11/6.
 */
public class ClusterListResponse {
    private int status;

    private String message;

    private List<String> result;

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

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }
}
