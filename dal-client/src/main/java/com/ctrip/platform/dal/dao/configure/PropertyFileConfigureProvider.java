package com.ctrip.platform.dal.dao.configure;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;

public class PropertyFileConfigureProvider implements DataSourceConfigureProvider {
    private final String CONFIG_NAME = "database.properties";
    private final String PATH = "path";

    private final String USER_NAME = ".userName";
    private final String PASSWORD = ".password";
    private final String CONNECTION_URL = ".connectionUrl";
    private final String DRIVER = ".driverClassName";

    private final String[] MUST_HAVES = new String[] {USER_NAME, PASSWORD, CONNECTION_URL, DRIVER};

    private String location;
    private Properties properties = new Properties();
    private Map<String, String> map = new HashMap<>();

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        DataSourceConfigureParser.getInstance();
        location = settings.get(PATH);
        if (location != null) {
            properties.load(new FileReader(location));
            convertPropertiesToMap(properties);
            return;
        }

        // check classpath
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = DalClientFactory.class.getClassLoader();
        }

        URL configUrl = classLoader.getResource(CONFIG_NAME);

        if (configUrl == null)
            throw new IllegalStateException(
                    "Can not find " + CONFIG_NAME + " to initilize database configure provider");

        properties.load(new FileReader(new File(configUrl.toURI())));
        convertPropertiesToMap(properties);
    }

    private void convertPropertiesToMap(Properties properties) {
        map.clear();

        if (properties == null)
            return;

        Set<String> names = properties.stringPropertyNames();
        for (String temp : names) {
            String name = ConnectionStringKeyHelper.getKeyName(temp);
            map.put(name, properties.getProperty(temp));
        }
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String dbName) {
        PoolPropertiesConfigure configure =
                DataSourceConfigureLocatorManager.getInstance().getUserPoolPropertiesConfigure(dbName);
        DataSourceConfigure dataSourceConfigure = configure == null ? new DataSourceConfigure(dbName)
                : new DataSourceConfigure(dbName, configure.getProperties());

        String userName = ConnectionStringKeyHelper.getKeyName(dbName + USER_NAME);
        String password = ConnectionStringKeyHelper.getKeyName(dbName + PASSWORD);
        String connectionUrl = ConnectionStringKeyHelper.getKeyName(dbName + CONNECTION_URL);
        String driver = ConnectionStringKeyHelper.getKeyName(dbName + DRIVER);
        dataSourceConfigure.setUserName(map.get(userName));
        dataSourceConfigure.setPassword(map.get(password));
        dataSourceConfigure.setConnectionUrl(map.get(connectionUrl));
        dataSourceConfigure.setDriverClass(map.get(driver));

        return dataSourceConfigure;
    }

    @Override
    public void setup(Set<String> dbNames) {
        for (String name : dbNames) {
            for (String item : MUST_HAVES) {
                if (properties.getProperty(name + item) == null)
                    throw new IllegalStateException(
                            "Can not find " + name + item + " to initilize database configure provider");
            }
        }
    }

    @Override
    public void register(String dbName, DataSourceConfigureChangeListener listener) {}

}
