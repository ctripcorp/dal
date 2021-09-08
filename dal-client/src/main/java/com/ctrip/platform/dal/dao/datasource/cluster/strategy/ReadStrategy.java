package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.platform.dal.dao.DalHints;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public interface ReadStrategy extends RouteStrategy {

    HostSpec pickRead(DalHints hints) throws HostNotExpectedException;
}