package com.ctrip.platform.dal.sharding.idgen;

import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class IdGeneratorFactoryManager {

    private static final String CONFIG_ROOT_PATH = "META-INF/services/";

    private String defaultFactoryClassName = null;
    private Map<String, IIdGeneratorFactory> factoryCache = new HashMap<>();

    public IIdGeneratorFactory getOrCreateNullFactory() {
        return getOrCreateFactory(NullIdGeneratorFactory.class.getName());
    }

    public IIdGeneratorFactory getOrCreateDefaultFactory() {
        if (defaultFactoryClassName != null) {
            return getOrCreateFactory(defaultFactoryClassName);
        }
        IIdGeneratorFactory factory = null;
        List<String> classNames = getFactoryClassNames();
        for (String className : classNames) {
            try {
                factory = getOrCreateFactory(className);
                if (factory != null) {
                    defaultFactoryClassName = className;
                    break;
                }
            } catch (Exception e) {
            }
        }
        return factory;
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
        }
        if (null == factory) {
            try {
                factory = (IIdGeneratorFactory) Class.forName(className).getMethod("getInstance").invoke(null);
            } catch (Exception e) {
            }
        }

        if (null == factory) {
            throw new DalRuntimeException(String.format("Failed to get or create factory '%s'", className));
        }
        factoryCache.put(className, factory);
        return factory;
    }

    public List<String> getFactoryClassNames() {
        String configFilePath = CONFIG_ROOT_PATH + IIdGeneratorFactory.class.getName();
        List<String> classNames = new ArrayList<>();
        Enumeration<URL> urls = null;
        try {
            urls = IdGeneratorFactoryManager.class.getClassLoader().getResources(configFilePath);
        } catch (IOException e) {
            return classNames;
        }
        if (null == urls) {
            return classNames;
        }
        while (urls.hasMoreElements()) {
            try {
                URL url = urls.nextElement();
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    classNames.add(line);
                }
            } catch (Exception e) {
            }
        }
        return classNames;
    }

}
