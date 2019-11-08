package com.ctrip.framework.db.cluster.entity;


/**
 * Created by taochen on 2019/11/5.
 */
public class ClusterResponse {
    private int status;

    private String message;

    private Cluster result;

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

    public Cluster getResult() {
        return result;
    }

    public void setResult(Cluster result) {
        this.result = result;
    }
}
