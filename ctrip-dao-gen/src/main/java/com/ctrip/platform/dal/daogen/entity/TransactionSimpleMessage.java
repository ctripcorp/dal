package com.ctrip.platform.dal.daogen.entity;

/**
 * Created by taochen on 2019/7/18.
 */
public class TransactionSimpleMessage {
    private String data;

    private long durationInMillis;

    private long durationInMicros;

    private boolean success;

    private long timestamp;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getDurationInMillis() {
        return durationInMillis;
    }

    public void setDurationInMillis(long durationInMillis) {
        this.durationInMillis = durationInMillis;
    }

    public long getDurationInMicros() {
        return durationInMicros;
    }

    public void setDurationInMicros(long durationInMicros) {
        this.durationInMicros = durationInMicros;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
