package com.ctrip.platform.dal.dao.configure;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalClientFactory;

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

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        location = settings.get(PATH);
        if (location != null) {
            properties.load(new FileReader(location));
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
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String dbName) {
        DataSourceConfigure configure = DataSourceConfigureHolder.getInstance().getDataSourceConfigure(dbName);
        DataSourceConfigure dataSourceConfigure = configure == null ? new DataSourceConfigure(dbName)
                : new DataSourceConfigure(dbName, configure.getMap());

        dataSourceConfigure.setUserName(properties.getProperty(dbName + USER_NAME));
        dataSourceConfigure.setPassword(properties.getProperty(dbName + PASSWORD));
        dataSourceConfigure.setConnectionUrl(properties.getProperty(dbName + CONNECTION_URL));
        dataSourceConfigure.setDriverClass(properties.getProperty(dbName + DRIVER));

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
