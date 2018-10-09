package com.ctrip.framework.idgen.client.service;

import com.ctrip.platform.idgen.service.api.IdSegment;

import java.util.List;

public interface IServiceManager {

    List<IdSegment> fetchIdPool(String sequenceName, int requestSize, int timeoutMillis);

}
