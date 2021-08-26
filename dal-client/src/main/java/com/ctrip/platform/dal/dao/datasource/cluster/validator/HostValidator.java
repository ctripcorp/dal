package com.ctrip.platform.dal.dao.datasource.cluster.validator;


import com.ctrip.platform.dal.cluster.base.HostSpec;

public interface HostValidator extends ConnectionValidator {

    boolean available(HostSpec host);

    /**
     * when there is a host suspected problem triggers all hosts validate
     */
    void triggerValidate();

    void destroy();
}
