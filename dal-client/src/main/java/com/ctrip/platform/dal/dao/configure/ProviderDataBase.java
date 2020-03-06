package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;

import java.lang.reflect.Constructor;

public class ProviderDataBase extends DefaultDataBase {

    private ConnectionStringConfigureProvider connectionStringProvider;

    public ProviderDataBase(DataBase dataBase,
                            String connectionStringProvider) throws Exception {
        super(dataBase.getName(), dataBase.isMaster(), dataBase.getSharding(), dataBase.getConnectionString());

        initConnectionStringProvider(connectionStringProvider);
    }

    private void initConnectionStringProvider(String connectionStringProvider) throws Exception {
        Class cls = Class.forName(connectionStringProvider);
        Constructor con = cls.getConstructor(String.class);
        this.connectionStringProvider = (ConnectionStringConfigureProvider) con.newInstance(getConnectionString());
    }

    public ConnectionStringConfigureProvider getConnectionStringProvider() {
        return connectionStringProvider;
    }
}
