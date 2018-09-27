package com.ctrip.platform.dal.sharding.idgen;

import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.util.HashMap;
import java.util.Map;

public class IdGeneratorFactoryManager {

    private String defaultFactoryClassName;
    private Map<String, IIdGeneratorFactory> factoryCache = new HashMap<>();

    public IIdGeneratorFactory getOrCreateNullFactory() {
        return getOrCreateFactory(NullIdGeneratorFactory.class.getName());
    }

    public IIdGeneratorFactory getOrCreateDefaultFactory() {
        return getOrCreateFactory(getDefaultFactoryClassName());
    }

    public IIdGeneratorFactory getOrCreateFactory(String className) {
        if (null == className) {
            throw new NullPointerException("Factory classname is null");
        }

        IIdGeneratorFactory factory = factoryCache.get(className);
        if (factory != null) {
            return factory;
        }

        try {
            factory = (IIdGeneratorFactory) Class.forName(className).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == factory) {
            try {
                factory = (IIdGeneratorFactory) Class.forName(className).getDeclaredMethod("getInstance").invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (null == factory) {
            throw new DalRuntimeException(String.format("Failed to get or create factory '%s'", className));
        }
        factoryCache.put(className, factory);
        return factory;
    }

    private String getDefaultFactoryClassName() {
        return defaultFactoryClassName;
    }

}
