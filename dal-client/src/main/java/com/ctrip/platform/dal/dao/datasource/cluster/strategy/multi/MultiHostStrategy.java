package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi;

import com.ctrip.platform.dal.dao.datasource.cluster.strategy.ConnectionFactoryAware;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;

/**
 * @Author limingdong
 * @create 2021/8/26
 */
public interface MultiHostStrategy extends RouteStrategy, ConnectionFactoryAware {

    String FAIL_OVER_TIME_MS = "failoverTimeMS";

    String BLACK_LIST_TIMEOUT_MS = "blacklistTimeoutMS";

    String VALIDATE_PERIOD_MS = "fixedValidatePeriodMS";

    String ZONE_PRIORITY = "zonesPriority";
}
