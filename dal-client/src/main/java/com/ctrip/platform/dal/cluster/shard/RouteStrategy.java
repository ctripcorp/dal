package com.ctrip.platform.dal.cluster.shard;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.exception.HostNotExpectedException;
import com.ctrip.platform.dal.dao.DalHints;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public interface RouteStrategy extends RouteStrategyLifecycle, ExceptionInterceptor {

    HostSpec pickNode(DalHints hints) throws HostNotExpectedException;
}