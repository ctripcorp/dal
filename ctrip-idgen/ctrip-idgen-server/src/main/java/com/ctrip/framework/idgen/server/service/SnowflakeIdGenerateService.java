package com.ctrip.framework.idgen.server.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.ctrip.platform.idgen.service.api.IdGenRequestType;
import com.ctrip.platform.idgen.service.api.IdGenResponseType;
import com.ctrip.platform.idgen.service.api.IdGenerateService;
import com.ctrip.platform.idgen.service.api.IdSegment;

import java.util.List;

@Service(parameters = {"serviceId", "framework.service.idgenerator.v2.idgenerateservice"})
public class SnowflakeIdGenerateService implements IdGenerateService {

    @Override
    public IdGenResponseType fetchIdPool(IdGenRequestType request) {
        validate(request);
        IdWorker idWorker = IdFactory.getInstance().getOrCreateIdWorker(request.getSequenceName());
        List<IdSegment> idSegments = idWorker.generateIdPool(request.getRequestSize(), request.getTimeoutMillis());
        return new IdGenResponseType(idSegments);
    }

    @Override
    public IdGenResponseType fetchId(IdGenRequestType request) {
        validate(request);
        IdWorker idWorker = IdFactory.getInstance().getOrCreateIdWorker(request.getSequenceName());
        List<IdSegment> idSegments = idWorker.generateIdPool(1, request.getTimeoutMillis());
        return new IdGenResponseType(idSegments.get(0).getEnd());
    }

    private void validate(IdGenRequestType request) {
        if (null == request) {
            throw new IllegalArgumentException("Null request");
        }
    }

}
