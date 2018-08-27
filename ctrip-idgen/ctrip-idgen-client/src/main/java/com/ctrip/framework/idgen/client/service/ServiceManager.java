package com.ctrip.framework.idgen.client.service;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.idgen.service.api.IdGenerateService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceManager {

    private volatile static ServiceManager manager = null;
    private static final Object lock = new Object();

    private final AtomicReference<ReferenceConfig<IdGenerateService>> refConfigReference = new AtomicReference<>();
    private final AtomicReference<IdGenerateService> idGenServiceReference = new AtomicReference<>();

    private static final String REGISTRY_ID_VALUE = "artemis";
    private static final String REGISTRY_PROTOCOL_VALUE = "artemis";
    private static final String REFERENCE_SERVICEID_KEY = "serviceId";
    private static final String IDGENERATESERVICE_SERVICEID_VALUE = "framework.service.idgenerator.v2.idgenerateservice";
    private static final int TIMEOUT_DEFAULT_VALUE = 100;
    private static final int RETRIES_DEFAULT_VALUE = 2;

    private ServiceManager() {}

    public static ServiceManager getInstance() {
        if (null == manager) {
            synchronized (lock) {
                if (null == manager) {
                    manager = new ServiceManager();
                }
            }
        }
        return manager;
    }

    public IdGenerateService getIdGenServiceInstance() {
        IdGenerateService idGenService = idGenServiceReference.get();
        if (null == idGenService) {
            synchronized (this) {
                idGenService = idGenServiceReference.get();
                if (null == idGenService) {
                    idGenService = getRefConfigInstance().get();
                    idGenServiceReference.set(idGenService);
                }
            }
        }
        return idGenService;
    }

    private ReferenceConfig<IdGenerateService> getRefConfigInstance() {
        ReferenceConfig<IdGenerateService> refConfig = refConfigReference.get();
        if (null == refConfig) {
            synchronized (this) {
                refConfig = refConfigReference.get();
                if (null == refConfig) {
                    refConfig = createReferenceConfig();
                    refConfigReference.set(refConfig);
                }
            }
        }
        return refConfig;
    }

    private ReferenceConfig<IdGenerateService> createReferenceConfig() {
        ApplicationConfig appConfig = new ApplicationConfig();
        appConfig.setName(Foundation.app().getAppId());

        RegistryConfig regConfig = new RegistryConfig();
        regConfig.setId(REGISTRY_ID_VALUE);
        regConfig.setProtocol(REGISTRY_PROTOCOL_VALUE);

        ReferenceConfig<IdGenerateService> refConfig = new ReferenceConfig<>();
        refConfig.setApplication(appConfig);
        refConfig.setRegistry(regConfig);
        refConfig.setTimeout(TIMEOUT_DEFAULT_VALUE);
        refConfig.setRetries(RETRIES_DEFAULT_VALUE);
        refConfig.setInterface(IdGenerateService.class);
        Map<String, String> params = new HashMap<>();
        params.put(REFERENCE_SERVICEID_KEY, IDGENERATESERVICE_SERVICEID_VALUE);
        refConfig.setParameters(params);
        refConfig.setInit(true);

        return refConfig;
    }

}
