package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi;

import com.ctrip.platform.dal.dao.datasource.cluster.strategy.ConnectionFactoryAware;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;

/**
 * @Author limingdong
 * @create 2021/8/26
 */
public interface MultiMasterStrategy extends RouteStrategy, ConnectionFactoryAware {

    String FAILOVER_TIME_MS = "failoverTimeMS";

    String BLACKLIST_TIMEOUT_MS = "blacklistTimeoutMS";

    String FIXED_VALIDATE_PERIOD_MS = "fixedValidatePeriodMS";

    String ZONES_PRIORITY = "zonesPriority";

    String MULTI_MASTER = "multiMaster";

}
