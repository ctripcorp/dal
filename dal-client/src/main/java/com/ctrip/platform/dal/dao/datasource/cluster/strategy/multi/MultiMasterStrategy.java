package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi;

import com.ctrip.platform.dal.dao.datasource.cluster.strategy.ConnectionFactoryAware;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;

/**
 * @Author limingdong
 * @create 2021/8/26
 */
public interface MultiMasterStrategy extends RouteStrategy, ConnectionFactoryAware {

    default boolean multiMaster() {
        return true;
    }

    String FAILOVER_TIME_MS = "failoverTimeMS";
    long DEFAULT_FAILOVER_TIME_MS_VALUE = 10000;

    String BLACKLIST_TIMEOUT_MS = "blacklistTimeoutMS";
    long DEFAULT_BLACKLIST_TIMEOUT_MS_VALUE = 10000;

    String FIXED_VALIDATE_PERIOD_MS = "fixedValidatePeriodMS";
    long DEFAULT_FIXED_VALIDATE_PERIOD_MS_VALUE = 30000;

    String ZONES_PRIORITY = "zonesPriority";
    String DEFAULT_ZONES_PRIORITY_VALUE = "sharb,shaxy,shafq,shajq,shaoy";

    String MULTI_MASTER = "multiMaster";
    boolean DEFAULT_MULTI_MASTER_VALUE = true;

    String CAT_TYPE = "catType";

}
