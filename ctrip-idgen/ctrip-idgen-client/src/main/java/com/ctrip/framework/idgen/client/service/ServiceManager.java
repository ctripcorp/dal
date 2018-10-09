package com.ctrip.framework.idgen.client.service;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.framework.idgen.client.log.CatConstants;
import com.ctrip.platform.idgen.service.api.IdGenRequestType;
import com.ctrip.platform.idgen.service.api.IdGenResponseType;
import com.ctrip.platform.idgen.service.api.IdGenerateService;
import com.ctrip.platform.idgen.service.api.IdSegment;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceManager implements IServiceManager {

    private static final String REGISTRY_ID_VALUE = "artemis";
    private static final String REGISTRY_PROTOCOL_VALUE = "artemis";
    private static final String REFERENCE_SERVICE_ID_KEY = "serviceId";
    private static final String REFERENCE_SERVICE_ID_VALUE = "framework.service.idgenerator.v2.idgenerateservice";
    private static final int TIMEOUT_DEFAULT_VALUE = 100;
    private static final int RETRIES_DEFAULT_VALUE = 2;

    private final AtomicReference<ReferenceConfig<IdGenerateService>> refConfigReference = new AtomicReference<>();
    private final AtomicReference<IdGenerateService> serviceReference = new AtomicReference<>();

    @Override
    public List<IdSegment> fetchIdPool(String sequenceName, int requestSize, int timeoutMillis) {
        Transaction transaction = Cat.newTransaction(CatConstants.TYPE_ROOT,
                CatConstants.NAME_CALL_SERVICE + ":fetchIdPool");
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
                    service = getOrCreateRefConfig().get();
                    serviceReference.set(service);
                }
            }
        }
        return service;
    }

    private ReferenceConfig<IdGenerateService> getOrCreateRefConfig() {
        ReferenceConfig<IdGenerateService> refConfig = refConfigReference.get();
        if (null == refConfig) {
            synchronized (refConfigReference) {
                refConfig = refConfigReference.get();
                if (null == refConfig) {
                    refConfig = createRefConfig();
                    refConfigReference.set(refConfig);
                }
            }
        }
        return refConfig;
    }

    private ReferenceConfig<IdGenerateService> createRefConfig() {
        ReferenceConfig<IdGenerateService> refConfig = new ReferenceConfig<>();
        refConfig.setApplication(createAppConfig());
        refConfig.setRegistry(createRegConfig());
        refConfig.setTimeout(TIMEOUT_DEFAULT_VALUE);
        refConfig.setRetries(RETRIES_DEFAULT_VALUE);
        refConfig.setInterface(IdGenerateService.class);
        Map<String, String> params = new HashMap<>();
        params.put(REFERENCE_SERVICE_ID_KEY, REFERENCE_SERVICE_ID_VALUE);
        refConfig.setParameters(params);
        refConfig.setInit(true);
        return refConfig;
    }

    private ApplicationConfig createAppConfig() {
        ApplicationConfig appConfig = new ApplicationConfig();
        appConfig.setName(Foundation.app().getAppId());
        return appConfig;
    }

    private RegistryConfig createRegConfig() {
        RegistryConfig regConfig = new RegistryConfig();
        regConfig.setId(REGISTRY_ID_VALUE);
        regConfig.setProtocol(REGISTRY_PROTOCOL_VALUE);
        return regConfig;
    }

}
