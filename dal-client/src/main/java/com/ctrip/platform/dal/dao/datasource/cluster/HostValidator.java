package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;

public interface HostValidator {

    boolean available(HostSpec host);

    /**
     * when there is a host suspected problem triggers all hosts validate
     */
    void triggerValidate();

    void destroy();
}
