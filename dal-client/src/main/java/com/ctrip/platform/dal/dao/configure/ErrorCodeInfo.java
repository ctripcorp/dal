package com.ctrip.platform.dal.dao.configure;

public class ErrorCodeInfo {
    private int errorCode;
    private int intervalInSeconds;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getIntervalInSeconds() {
        return intervalInSeconds;
    }

    public void setIntervalInSeconds(int intervalInSeconds) {
        this.intervalInSeconds = intervalInSeconds;
    }
}
