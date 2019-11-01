package com.ctrip.framework.db.cluster.enums;

/**
 * Created by shenjie on 2019/3/7.
 */
public enum HealthStatus {
    HEALTHY(0), UNHEALTHY(1);

    private int code;

    HealthStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
