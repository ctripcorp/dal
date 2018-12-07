package com.ctrip.framework.idgen.client.service;

import com.ctrip.framework.cdubbo.helper.CDubboClient;
import com.ctrip.framework.idgen.client.log.CatConstants;
import com.ctrip.framework.idgen.service.api.IdGenRequestType;
import com.ctrip.framework.idgen.service.api.IdGenResponseType;
import com.ctrip.framework.idgen.service.api.IdGenerateService;
import com.ctrip.framework.idgen.service.api.IdSegment;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceManager implements IServiceManager {

    private final AtomicReference<IdGenerateService> serviceReference = new AtomicReference<>();

    @Override
    public List<IdSegment> fetchIdPool(String sequenceName, int requestSize, int timeoutMillis) {
        Transaction transaction = Cat.newTransaction(CatConstants.TYPE_CALL_SERVICE, sequenceName);
        try {
            IdGenRequestType request = new IdGenRequestType(sequenceName, requestSize, timeoutMillis);
            IdGenResponseType response = getOrCreateService().fetchIdPool(request);
            if (null == response) {
                throw new NullPointerException("Null response");
            }
            transaction.setStatus(Transaction.SUCCESS);
            return response.getIdSegments();
        } catch (Exception e) {
            transaction.setStatus(e);
            throw e;
        } finally {
            transaction.complete();
        }
    }

    private IdGenerateService getOrCreateService() {
        IdGenerateService service = serviceReference.get();
        if (null == service) {
            synchronized (serviceReference) {
                service = serviceReference.get();
                if (null == service) {
                    service = CDubboClient.newBuilder().build().getService(IdGenerateService.class);
                    serviceReference.set(service);
                }
            }
        }
        return service;
    }

}
