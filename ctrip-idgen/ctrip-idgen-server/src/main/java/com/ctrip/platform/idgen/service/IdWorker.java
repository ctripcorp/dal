package com.ctrip.platform.idgen.service;

import com.ctrip.platform.idgen.service.api.IdSegment;

import java.util.List;

public interface IdWorker {

    List<IdSegment> generateIdPool(long requestSize, int timeoutMillis);

}
