package com.ctrip.platform.dal.dao.datasource.cluster;

public interface HostValidator {

    boolean available(HostSpec host);

    /**
     * when there is a host suspected problem triggers all hosts validate
     */
    void triggerValidate();

    void destroy();
}
