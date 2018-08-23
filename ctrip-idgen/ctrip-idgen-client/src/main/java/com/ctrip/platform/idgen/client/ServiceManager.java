package com.ctrip.platform.idgen.client;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.ctrip.platform.idgen.service.api.IdGenerateService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceManager {

    private static final AtomicReference<ReferenceConfig<IdGenerateService>> refConfigReference =
            new AtomicReference<>();
    private static final AtomicReference<IdGenerateService> idGenServiceReference = new AtomicReference<>();

    public static IdGenerateService getIdGenServiceInstance() {
        IdGenerateService idGenService = idGenServiceReference.get();

        if (null != idGenService) {
            return idGenService;
        } else {
            IdGenerateService newIdGenService = getRefConfigInstance().get();
            idGenServiceReference.compareAndSet(idGenService, newIdGenService);
            return idGenServiceReference.get();
        }
    }

    private static ReferenceConfig<IdGenerateService> getRefConfigInstance() {
        ReferenceConfig<IdGenerateService> refConfig = refConfigReference.get();

        if (null != refConfig) {
            return refConfig;
        } else {
            ApplicationConfig appConfig = new ApplicationConfig();
            appConfig.setName("idgen-client");

            RegistryConfig regConfig = new RegistryConfig();
            regConfig.setId("artemis");
            regConfig.setProtocol("artemis");

            ReferenceConfig<IdGenerateService> newRefConfig = new ReferenceConfig<IdGenerateService>();
            newRefConfig.setApplication(appConfig);
            newRefConfig.setRegistry(regConfig);
            newRefConfig.setInterface(IdGenerateService.class);
            Map<String, String> params = new HashMap<String, String>();
            params.put("serviceId", "framework.service.idgenerator.v2.idgenerateservice");
            newRefConfig.setParameters(params);
            // newRefConfig.setUrl("dubbo://localhost:20880/");
            newRefConfig.setUrl("dubbo://10.5.108.18:20880/");
            newRefConfig.setInit(true);

            refConfigReference.compareAndSet(refConfig, newRefConfig);
            return refConfigReference.get();
        }
    }

}
