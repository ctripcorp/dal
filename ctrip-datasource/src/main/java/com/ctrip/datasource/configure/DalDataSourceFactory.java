package com.ctrip.datasource.configure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.datasource.titan.TitanDataSourceLocator;
import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.dao.configure.AbstractVariableDataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;

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
        return createDataSource(allInOneKey, svcUrl, appid, false);
    }

    public DataSource createVariableTypeDataSource(String dbName) throws Exception {
        return createVariableTypeDataSource(dbName, new CtripVariableDataSourceConfigureProvider());
    }

    public DataSource createVariableTypeDataSource(String dbName, AbstractVariableDataSourceConfigureProvider provider) throws Exception {
        Set<String> names = new HashSet<>();
        names.add(dbName);
        DataSourceConfigureManager.getInstance().setVariableConnectionStringProvider(provider);
        provider.setup(names);
        DataSourceLocator locator = new DataSourceLocator(provider);
        return locator.getDataSource(dbName);
    }

    public DataSource createDataSource(String allInOneKey, String svcUrl, String appid, boolean isForceInitialize) throws Exception {
        TitanProvider provider = new TitanProvider();
        Map<String, String> settings = new HashMap<>();
        settings.put(IGNORE_EXTERNAL_EXCEPTION, String.valueOf(isForceInitialize));
        provider.initialize(settings);
        provider.setSourceTypeByEnv();

        Set<String> names = new HashSet<>();
        ClusterInfo clusterInfo = provider.tryGetClusterInfo(allInOneKey);

        if (clusterInfo == null || !clusterInfo.isValid())
            names.add(allInOneKey);

        provider.setup(names);
        DataSourceLocator locator = new DataSourceLocator(provider, isForceInitialize);

        if (clusterInfo == null || !clusterInfo.isValid())
            return locator.getDataSource(allInOneKey);
        else
            return locator.getDataSource(clusterInfo);
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
