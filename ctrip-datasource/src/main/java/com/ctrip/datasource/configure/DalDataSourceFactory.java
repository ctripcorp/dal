package com.ctrip.datasource.configure;

import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;

public class DalDataSourceFactory {
    private TitanProvider provider = new TitanProvider();

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
        provider.setup(names);

        DataSourceLocator loc = new DataSourceLocator(provider);
        return loc.getDataSource(allInOneKey);
    }
}
