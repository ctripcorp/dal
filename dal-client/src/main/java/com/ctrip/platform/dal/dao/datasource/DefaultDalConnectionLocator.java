package com.ctrip.platform.dal.dao.datasource;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.DefaultDataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class DefaultDalConnectionLocator implements DalConnectionLocator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDalConnectionLocator.class);
    private static final String DATASOURCE_CONFIG_PROVIDER = "dataSourceConfigureProvider";
    private static final String DATASOURCE_GET_DATASOURCE = "DataSource::getDataSource";

    private DataSourceConfigureProvider provider;
    private DataSourceLocator locator;

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        provider = new DefaultDataSourceConfigureProvider();
        if (settings.containsKey(DATASOURCE_CONFIG_PROVIDER)) {
            provider =
                    (DataSourceConfigureProvider) Class.forName(settings.get(DATASOURCE_CONFIG_PROVIDER)).newInstance();
        }

        provider.initialize(settings);
        locator = new DataSourceLocator(provider);
    }

    @Override
    public void setup(Set<String> dbNames) {
        provider.setup(dbNames);
    }

    @Override
    public Connection getConnection(String name) throws Exception {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        LOGGER.info(String.format("%s:%s start.", DATASOURCE_GET_DATASOURCE, name));
        DataSource dataSource = locator.getDataSource(keyName);
        LOGGER.info(String.format("%s:%s end.", DATASOURCE_GET_DATASOURCE, name));
        return dataSource.getConnection();
    }

}
