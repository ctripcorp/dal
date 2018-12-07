package com.ctrip.framework.idgen.server.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.ctrip.framework.idgen.service.api.IdGenRequestType;
import com.ctrip.framework.idgen.service.api.IdGenResponseType;
import com.ctrip.framework.idgen.service.api.IdGenerateService;
import com.ctrip.framework.idgen.service.api.IdSegment;

import java.util.List;

@Service(timeout=260, retries=2)
public class SnowflakeIdGenerateService implements IdGenerateService {

    @Override
    public IdGenResponseType fetchIdPool(IdGenRequestType request) {
        IdWorker idWorker = IdFactory.getInstance().getOrCreateIdWorker(request.getSequenceName());
        List<IdSegment> idSegments = idWorker.generateIdPool(request.getRequestSize(), request.getTimeoutMillis());
        return new IdGenResponseType(idSegments);
    }

    @Override
    public IdGenResponseType fetchId(IdGenRequestType request) {
        IdWorker idWorker = IdFactory.getInstance().getOrCreateIdWorker(request.getSequenceName());
        List<IdSegment> idSegments = idWorker.generateIdPool(1, request.getTimeoutMillis());
        return new IdGenResponseType(idSegments.get(0).getEnd());
    }

}
