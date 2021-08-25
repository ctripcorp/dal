package com.ctrip.platform.dal.cluster.shard;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.dao.DalHints;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public interface ReadStrategy extends RouteStrategyLifecycle, ExceptionInterceptor {

    HostSpec pickRead(DalHints hints);
}