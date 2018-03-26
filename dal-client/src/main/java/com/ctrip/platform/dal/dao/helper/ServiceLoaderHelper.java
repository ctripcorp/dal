package com.ctrip.platform.dal.dao.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceLoaderHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLoaderHelper.class);

    private static Map<Class<?>, Object> allServices = new ConcurrentHashMap<>();


    public static <T> T getInstance(Class<T> clazz) {
        T instance = null;
        try {
            Iterator<T> iterator = getIterator(clazz);

            if (!Ordered.class.isAssignableFrom(clazz)) {
                if (iterator.hasNext())
                    return iterator.next();
            } else {
                List<T> sortServices = new LinkedList<>();
                while (iterator.hasNext()) {
                    T service = iterator.next();
                    sortServices.add(service);
                }
                if (sortServices.size() == 0) {
                    return null;
                }
                Collections.sort(sortServices, (Comparator<? super T>) new OrderedComparator());
                return sortServices.get(0);
            }
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
