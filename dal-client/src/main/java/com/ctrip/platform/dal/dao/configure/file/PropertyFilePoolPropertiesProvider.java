package com.ctrip.platform.dal.dao.configure.file;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.PoolPropertiesConfigure;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesProvider;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class PropertyFilePoolPropertiesProvider implements PoolPropertiesProvider, DataSourceConfigureConstants {
    private static final String PROPERTY_FILE_NAME = "datasource.properties";
    private AtomicReference<Properties> propertiesReference = new AtomicReference<>();
    private Map<String, String> map = new ConcurrentHashMap<>();

    public PropertyFilePoolPropertiesProvider() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = PropertyFilePoolPropertiesProvider.class.getClassLoader();
        }

        URL url = classLoader.getResource(PROPERTY_FILE_NAME);
        if (url == null)
            return;

        Properties p = new Properties();
        try {
            p.load(new FileReader(new File(url.toURI())));
            propertiesReference.set(p);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PoolPropertiesConfigure getPoolProperties() {
        Properties p = propertiesReference.get();
        DataSourceConfigure configure = new DataSourceConfigure("", p);
        return configure;
    }

    @Override
    public void addPoolPropertiesChangedListener(PoolPropertiesChanged callback) {}

}
