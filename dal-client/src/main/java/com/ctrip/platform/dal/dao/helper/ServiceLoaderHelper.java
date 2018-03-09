package com.ctrip.platform.dal.dao.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ServiceLoaderHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLoaderHelper.class);

    public static <T> T getInstance(Class<T> clazz) {
        T instance = null;
        try {
            Iterator<T> iterator = getIterator(clazz);
            if (iterator.hasNext())
                return iterator.next();
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
        return instance;
    }

    private static <T> Iterator<T> getIterator(Class<T> clazz) {
        ServiceLoader<T> loader = ServiceLoader.load(clazz);
        return loader.iterator();
    }
}
