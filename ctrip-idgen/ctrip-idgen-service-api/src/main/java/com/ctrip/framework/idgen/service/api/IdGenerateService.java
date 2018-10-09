package com.ctrip.framework.idgen.service.api;

public interface IdGenerateService {

    IdGenResponseType fetchIdPool(IdGenRequestType request) throws RuntimeException;

    IdGenResponseType fetchId(IdGenRequestType request) throws RuntimeException;

}
