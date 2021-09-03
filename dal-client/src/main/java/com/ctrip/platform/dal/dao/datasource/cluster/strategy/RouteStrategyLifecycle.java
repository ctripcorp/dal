package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;

import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public interface RouteStrategyLifecycle {

    void init(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties);

    void dispose();

}
