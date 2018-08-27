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
        if (null == request) {
            throw new RuntimeException("request is empty");
        }

        String sequenceName = request.getSequenceName();
        int requestSize = request.getRequestSize();
        int timeoutMillis = request.getTimeoutMillis();

        IdWorker idWorker = IdFactory.getInstance().getOrCreateIdWorker(sequenceName);
        if (null == idWorker) {
            throw new RuntimeException("Unknown exception");
        }

        List<IdSegment> idSegments = idWorker.generateIdPool(requestSize, timeoutMillis);
        return new IdGenResponseType(idSegments);
    }

    @Override
    public IdGenResponseType fetchId(IdGenRequestType request) {
        if (null == request) {
            throw new RuntimeException("request is empty");
        }

        String sequenceName = request.getSequenceName();
        int timeoutMillis = request.getTimeoutMillis();
        request = new IdGenRequestType(sequenceName, 1, timeoutMillis);

        IdGenResponseType response = fetchIdPool(request);
        if (null == response) {
            throw new RuntimeException("Unknown exception");
        }
        List<IdSegment> idSegments = response.getIdSegments();
        if (null == idSegments || idSegments.isEmpty()) {
            throw new RuntimeException("Unknown exception");
        }

        return new IdGenResponseType(idSegments.get(0).getStart());
    }

}
