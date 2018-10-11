package com.ctrip.framework.idgen.service.api;

public interface IdGenerateService {

    IdGenResponseType fetchIdPool(IdGenRequestType request);

    IdGenResponseType fetchId(IdGenRequestType request);

}
