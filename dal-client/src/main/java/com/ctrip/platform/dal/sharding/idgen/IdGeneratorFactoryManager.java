package com.ctrip.platform.dal.sharding.idgen;

import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.util.*;

public class IdGeneratorFactoryManager {

    private IIdGeneratorFactory defaultFactory = null;
    private Map<String, IIdGeneratorFactory> factoryCache = new HashMap<>();

    public IIdGeneratorFactory getOrCreateNullFactory() {
        return getOrCreateFactory(NullIdGeneratorFactory.class.getName());
    }

    public IIdGeneratorFactory getOrCreateDefaultFactory() {
        if (defaultFactory != null) {
            return defaultFactory;
        }
        defaultFactory = ServiceLoaderHelper.getInstanceWithDalServiceLoader(IIdGeneratorFactory.class);
        if (null == defaultFactory) {
            throw new DalRuntimeException("Failed to get default factory");
        }
        String className = defaultFactory.getClass().getName();
        IIdGeneratorFactory factory = factoryCache.get(className);
        if (factory != null) {
            defaultFactory = factory;
        } else {
            factoryCache.put(className, defaultFactory);
        }
        return defaultFactory;
    }

    public IIdGeneratorFactory getOrCreateFactory(String className) {
        if (null == className) {
            throw new NullPointerException("Factory classname is null");
        }
        className = className.trim();
        IIdGeneratorFactory factory = factoryCache.get(className);
        if (factory != null) {
            return factory;
        }
        factory = createFactory(className);
        factoryCache.put(className, factory);
        return factory;
    }

    private IIdGeneratorFactory createFactory(String className) {
        IIdGeneratorFactory factory = null;
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (Throwable t) {
            throw new DalRuntimeException("Failed to load class: " + className, t);
        }
        try {
            factory = (IIdGeneratorFactory) clazz.newInstance();
        } catch (Throwable t1) {
            try {
                factory = (IIdGeneratorFactory) clazz.getMethod("getInstance").invoke(null);
            } catch (Throwable t2) {
                String msg = String.format("Failed to create factory: %s. Cause 1: %s; cause 2: %s",
                        className, t1.getMessage(), t2.getMessage());
                throw new DalRuntimeException(msg);
            }
        }
        if (null == factory) {
            throw new DalRuntimeException(String.format("The created factory '%s' is null", className));
        }
        return factory;
    }

}
