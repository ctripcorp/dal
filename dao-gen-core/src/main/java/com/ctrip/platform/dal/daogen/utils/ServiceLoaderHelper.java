package com.ctrip.platform.dal.daogen.utils;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ServiceLoaderHelper {
    public static <T> T getInstance(Class<T> clazz) {
        T instance = null;
        try {
            Iterator<T> iterator = getIterator(clazz);
            if (iterator.hasNext())
                return iterator.next();
        } catch (Throwable e) {
        }
        return instance;
    }

    private static <T> Iterator<T> getIterator(Class<T> clazz) {
        ServiceLoader<T> loader = ServiceLoader.load(clazz);
        return loader.iterator();
    }
}
