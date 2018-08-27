package com.ctrip.framework.idgen.server.service;

import com.ctrip.platform.idgen.service.api.IdSegment;

import java.util.List;

public interface IdWorker {

    List<IdSegment> generateIdPool(int requestSize, int timeoutMillis);

}
