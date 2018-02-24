package com.ctrip.platform.dal.dao.configure.file;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesProvider;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyFilePoolPropertiesProvider implements PoolPropertiesProvider, DataSourceConfigureConstants {
    private static final String PROPERTY_FILE_NAME = "datasource.properties";
    private Map<String, String> map = new ConcurrentHashMap<>();

    public PropertyFilePoolPropertiesProvider() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = PropertyFilePoolPropertiesProvider.class.getClassLoader();
        }

        URL url = classLoader.getResource(PROPERTY_FILE_NAME);
        if (url == null)
            return;

        Properties properties = new Properties();
        try {
            properties.load(new FileReader(new File(url.toURI())));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        loadMap(properties);
    }

    @Override
    public Map<String, String> getPoolProperties() {
        return map;
    }

    private void loadMap(Properties properties) {
        if (properties == null)
            return;

        map.put(TESTWHILEIDLE, properties.getProperty(TESTWHILEIDLE, String.valueOf(DEFAULT_TESTWHILEIDLE)));
        map.put(TESTONBORROW, properties.getProperty(TESTONBORROW, String.valueOf(DEFAULT_TESTONBORROW)));
        map.put(TESTONRETURN, properties.getProperty(TESTONRETURN, String.valueOf(DEFAULT_TESTONRETURN)));
        map.put(VALIDATIONQUERY, properties.getProperty(VALIDATIONQUERY, DEFAULT_VALIDATIONQUERY));
        map.put(VALIDATIONINTERVAL,
                properties.getProperty(VALIDATIONINTERVAL, String.valueOf(DEFAULT_VALIDATIONINTERVAL)));
        map.put(VALIDATORCLASSNAME, properties.getProperty(VALIDATORCLASSNAME, DEFAULT_VALIDATORCLASSNAME));
        map.put(TIMEBETWEENEVICTIONRUNSMILLIS, properties.getProperty(TIMEBETWEENEVICTIONRUNSMILLIS,
                String.valueOf(DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS)));
        map.put(MAXACTIVE, properties.getProperty(MAXACTIVE, String.valueOf(DEFAULT_MAXACTIVE)));
        map.put(MINIDLE, properties.getProperty(MINIDLE, String.valueOf(DEFAULT_MINIDLE)));
        map.put(MAXWAIT, properties.getProperty(MAXWAIT, String.valueOf(DEFAULT_MAXWAIT)));
        map.put(MAX_AGE, properties.getProperty(MAX_AGE, String.valueOf(DEFAULT_MAXAGE)));
        map.put(INITIALSIZE, properties.getProperty(INITIALSIZE, String.valueOf(DEFAULT_INITIALSIZE)));
        map.put(REMOVEABANDONEDTIMEOUT,
                properties.getProperty(REMOVEABANDONEDTIMEOUT, String.valueOf(DEFAULT_REMOVEABANDONEDTIMEOUT)));
        map.put(REMOVEABANDONED, properties.getProperty(REMOVEABANDONED, String.valueOf(DEFAULT_REMOVEABANDONED)));
        map.put(LOGABANDONED, properties.getProperty(LOGABANDONED, String.valueOf(DEFAULT_LOGABANDONED)));
        map.put(MINEVICTABLEIDLETIMEMILLIS,
                properties.getProperty(MINEVICTABLEIDLETIMEMILLIS, String.valueOf(DEFAULT_MINEVICTABLEIDLETIMEMILLIS)));
        map.put(CONNECTIONPROPERTIES, properties.getProperty(CONNECTIONPROPERTIES, DEFAULT_CONNECTIONPROPERTIES));
        map.put(JDBC_INTERCEPTORS, properties.getProperty(JDBC_INTERCEPTORS, DEFAULT_JDBCINTERCEPTORS));
    }

    @Override
    public void addPoolPropertiesChangedListener(PoolPropertiesChanged callback) {}

}
