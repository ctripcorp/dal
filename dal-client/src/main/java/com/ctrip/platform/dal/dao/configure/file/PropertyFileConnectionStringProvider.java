package com.ctrip.platform.dal.dao.configure.file;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertyFileConnectionStringProvider implements ConnectionStringProvider {
    private static final String PROPERTY_FILE_NAME = "database.properties";
    private static final String USER_NAME = ".userName";
    private static final String PASSWORD = ".password";
    private static final String CONNECTION_URL = ".connectionUrl";
    private static final String DRIVER_CLASS_NAME = ".driverClassName";

    private Properties properties = new Properties();

    public PropertyFileConnectionStringProvider() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = PropertyFileConnectionStringProvider.class.getClassLoader();
        }

        URL url = classLoader.getResource(PROPERTY_FILE_NAME);
        if (url == null)
            return;

        try {
            properties.load(new FileReader(new File(url.toURI())));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, DataSourceConfigure> getConnectionStrings(Set<String> dbNames) throws Exception {
        if (dbNames == null || dbNames.isEmpty())
            return null;

        Map<String, DataSourceConfigure> map = new HashMap<>();
        for (String name : dbNames) {
            DataSourceConfigure configure = new DataSourceConfigure();
            configure.setUserName(properties.getProperty(name + USER_NAME));
            configure.setPassword(properties.getProperty(name + PASSWORD));
            configure.setConnectionUrl(properties.getProperty(name + CONNECTION_URL));
            configure.setDriverClass(properties.getProperty(name + DRIVER_CLASS_NAME));

            map.put(name, configure);
        }

        return map;
    }

    @Override
    public void addConnectionStringChangedListener(String name, ConnectionStringChanged callback) {}

}
