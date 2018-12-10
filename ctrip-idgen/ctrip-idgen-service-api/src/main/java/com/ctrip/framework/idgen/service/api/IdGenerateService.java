package com.ctrip.framework.idgen.service.api;

import com.ctriposs.baiji.rpc.common.BaijiContract;

@BaijiContract(serviceName="IdGenerateService", serviceNamespace="http://soa.ctrip.com/framework/service/idgenerator/v2")
public interface IdGenerateService {

    IdGenResponseType fetchIdPool(IdGenRequestType request);

    IdGenResponseType fetchId(IdGenRequestType request);

}
