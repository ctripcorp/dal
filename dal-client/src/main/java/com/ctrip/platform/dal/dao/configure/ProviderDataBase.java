package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.datasource.ApiDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.lang.reflect.Constructor;

public class ProviderDataBase extends DefaultDataBase {

    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private ConnectionStringConfigureProvider connectionStringProvider;
    private ApiDataSourceIdentity dataSourceIdentity;

    public ProviderDataBase(DataBase dataBase,
                            String connectionStringProvider) throws Exception {
        super(dataBase.getName(), dataBase.isMaster(), dataBase.getSharding(), dataBase.getConnectionString());
        initConnectionStringProvider(connectionStringProvider);
        dataSourceIdentity = new ApiDataSourceIdentity(this.connectionStringProvider);
    }

    private void initConnectionStringProvider(String connectionStringProvider) throws Exception {
        Class cls = Class.forName(connectionStringProvider);
        Constructor con = null;
        try {
            con = cls.getConstructor(String.class);
            this.connectionStringProvider = (ConnectionStringConfigureProvider) con.newInstance(getConnectionString());
        } catch (NoSuchMethodException e) {
            try {
                con = cls.getConstructor();
                this.connectionStringProvider = (ConnectionStringConfigureProvider) con.newInstance();
            } catch (Exception ex) {
                LOGGER.error("custom connection string provider is illegal.Must have a public constructor with no arguments or a string.", e);
                throw ex;
            }
        }
    }

    @Override
    public DataSourceIdentity getDataSourceIdentity() {
        return dataSourceIdentity;
    }

}
