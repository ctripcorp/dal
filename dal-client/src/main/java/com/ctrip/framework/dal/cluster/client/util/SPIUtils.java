package com.ctrip.framework.dal.cluster.client.util;

import com.ctrip.framework.dal.cluster.client.base.ComponentOrdered;
import com.ctrip.framework.dal.cluster.client.base.ComponentOrderedComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SPIUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SPIUtils.class);

    private static Map<Class<?>, Object> allServices = new ConcurrentHashMap<>();

    public static <T> T getInstance(Class<T> clazz) {
        T instance = null;
        try {
            Iterator<T> iterator = getIterator(clazz);

            if (!ComponentOrdered.class.isAssignableFrom(clazz)) {
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
                Collections.sort(sortServices, (Comparator<? super T>) new ComponentOrderedComparator());
                return sortServices.get(0);
            }
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
        return instance;
    }

    private static <T> Iterator<T> getIterator(Class<T> clazz) {
        ServiceLoader<T> loader = ServiceLoader.load(clazz, SPIUtils.class.getClassLoader());
        return loader.iterator();
    }

}
