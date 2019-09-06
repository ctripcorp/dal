package com.ctrip.datasource.configure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.ctrip.datasource.titan.TitanDataSourceLocator;
import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;

public class DalDataSourceFactory {
    private static final String IGNORE_EXTERNAL_EXCEPTION = "ignoreExternalException";

    /**
     * Create DataSource for given name. The appid and titan url will be discoved by framework foundation
     * 
     * @param allInOneKey
     * @return DataSource
     * @throws Exception
     */
    public DataSource createDataSource(String allInOneKey) throws Exception {
        return createDataSource(allInOneKey, null, null);
    }

    public DataSource createDataSource(String allInOneKey, boolean isForceInitialize) throws Exception {
        return createDataSource(allInOneKey, null, null, isForceInitialize);
    }

    /**
     * Create DataSource for given name. In case user has clog or cat configured. The name will be same for both PROD
     * and DEV environment
     * 
     * @param allInOneKey
     * @param svcUrl
     * @return DataSource
     * @throws Exception
     */
    public DataSource createDataSource(String allInOneKey, String svcUrl) throws Exception {
        return createDataSource(allInOneKey, svcUrl, null);
    }

    /**
     * Create DataSource for given name. In case user has no clog or cat configured. The name will be same for both PROD
     * and DEV environment
     * 
     * @param allInOneKey
     * @param svcUrl
     * @param appid
     * @return DataSource
     * @throws Exception
     */
    public DataSource createDataSource(String allInOneKey, String svcUrl, String appid) throws Exception {
        Set<String> names = new HashSet<>();
        names.add(allInOneKey);

        TitanProvider provider = new TitanProvider();
        provider.setSourceTypeByEnv();
        provider.setup(names);

        DataSourceLocator loc = new DataSourceLocator(provider);
        String keyName = ConnectionStringKeyHelper.getKeyName(allInOneKey);
        return loc.getDataSource(keyName);
    }

    public DataSource createDataSource(String allInOneKey, String svcUrl, String appid, boolean isForceInitialize) throws Exception {
        Set<String> names = new HashSet<>();
        names.add(allInOneKey);
        Map<String, String> settings = new HashMap<>();
        settings.put(IGNORE_EXTERNAL_EXCEPTION, String.valueOf(isForceInitialize));

        TitanProvider provider = new TitanProvider();
        provider.initialize(settings);
        provider.setSourceTypeByEnv();
        provider.setup(names);

        DataSourceLocator loc = new DataSourceLocator(provider, isForceInitialize);
        String keyName = ConnectionStringKeyHelper.getKeyName(allInOneKey);
        return loc.getDataSource(keyName);
    }

    /**
     * This is only for cross environment usage
     * 
     * @param allInOneKey
     * @param svcUrl
     * @return
     * @throws Exception
     */
    public DataSource createTitanDataSource(String allInOneKey, String svcUrl) throws Exception {
        return new TitanDataSourceLocator().getDataSource(svcUrl, allInOneKey);
    }
}
