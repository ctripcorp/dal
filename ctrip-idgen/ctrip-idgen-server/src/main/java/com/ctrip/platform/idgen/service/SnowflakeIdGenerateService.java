package com.ctrip.platform.idgen.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.ctrip.platform.idgen.service.api.IdGenRequestType;
import com.ctrip.platform.idgen.service.api.IdGenResponseType;
import com.ctrip.platform.idgen.service.api.IdGenerateService;
import com.ctrip.platform.idgen.service.api.IdSegment;

import java.util.List;

@Service(parameters = {"serviceId", "framework.service.idgenerator.v1.idgenerateservice"})
public class SnowflakeIdGenerateService implements IdGenerateService {

    @Override
    public IdGenResponseType fetchIdPool(IdGenRequestType request) {
        if (null == request) {
            //return null;
            throw new RuntimeException("request is empty");
        }

        long requestSize = request.getRequestSize();
        String sequenceName = request.getSequenceName();
        int timeoutMillis = request.getTimeoutMillis();

        IdWorker idWorker = IdFactory.getIdWorker(sequenceName);
        if (null == idWorker) {
            //return null;
            throw new RuntimeException("invalid sequenceName");
        }

        List<IdSegment> idSegments = idWorker.generateIdPool(requestSize, timeoutMillis);
        return new IdGenResponseType(idSegments);
    }

    @Override
    public IdGenResponseType fetchId(IdGenRequestType request) {
        return new IdGenResponseType(1000);
    }

}
