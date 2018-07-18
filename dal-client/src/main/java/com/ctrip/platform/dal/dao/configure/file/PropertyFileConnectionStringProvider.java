package com.ctrip.platform.dal.dao.configure.file;

import com.ctrip.platform.dal.dao.configure.ConnectionString;
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
    private static final String COMMA = ",";

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
    public Map<String, ConnectionString> getConnectionStrings(Set<String> names) throws Exception {
        if (names == null || names.isEmpty())
            return null;

        Map<String, ConnectionString> map = new HashMap<>();
        for (String name : names) {
            StringBuilder sb = new StringBuilder();
            sb.append(properties.getProperty(name + USER_NAME));
            sb.append(COMMA);
            sb.append(properties.getProperty(name + PASSWORD));
            sb.append(COMMA);
            sb.append(properties.getProperty(name + CONNECTION_URL));
            sb.append(COMMA);
            sb.append(properties.getProperty(name + DRIVER_CLASS_NAME));
            ConnectionString connectionString = new ConnectionString(name, sb.toString(), sb.toString());
            map.put(name, connectionString);
        }

        return map;
    }

    @Override
    public void addConnectionStringChangedListener(String name, ConnectionStringChanged callback) {}

}
