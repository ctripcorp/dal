package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;

import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public interface RouteStrategyLifecycle {

    void init(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties);

    void dispose();

}
