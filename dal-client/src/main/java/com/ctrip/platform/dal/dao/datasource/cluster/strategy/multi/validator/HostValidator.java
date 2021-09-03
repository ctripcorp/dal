package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator;


import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.ConnectionFactoryAware;

public interface HostValidator extends HostConnectionValidator, ConnectionFactoryAware {

    boolean available(HostSpec host);

    /**
     * when there is a host suspected problem triggers all hosts validate
     */
    void triggerValidate();

    void destroy();
}
