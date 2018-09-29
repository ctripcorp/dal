package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.ConnectionStringConfigure;
import com.ctrip.platform.dal.dao.configure.DalConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DefaultDataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.PropertiesWrapper;
import com.ctrip.platform.dal.exceptions.DalException;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

public class CtripDataSourceConfigureLocator extends DefaultDataSourceConfigureLocator {
    private static final String DATASOURCE_PROPERTIES_EXCEPTION_MESSAGE =
            "An error occured while getting datasource.properties from QConfig.";

    @Override
    public DataSourceConfigure mergeDataSourceConfigure(DalConnectionString connectionString) {
        ConnectionStringConfigure connectionStringConfigure = getConnectionStringConfigure(connectionString);
        if (connectionStringConfigure == null)
            return null;

        String name = connectionStringConfigure.getName();
        String logName = String.format(POOLPROPERTIES_MERGEPOOLPROPERTIES_FORMAT, name);
        Transaction transaction = Cat.newTransaction(DAL, logName);
        DataSourceConfigure c = null;

        try {
            c = _mergeDataSourceConfigure(connectionStringConfigure, connectionString, name, logName);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
            LOGGER.error(e.getMessage(), e);
        } finally {
            transaction.complete();
        }

        return c;
    }

    private DataSourceConfigure _mergeDataSourceConfigure(ConnectionStringConfigure connectionStringConfigure,
            DalConnectionString connectionString, String name, String logName) throws DalException {
        PropertiesWrapper wrapper = propertiesWrapperReference.get();
        if (wrapper == null)
            throw new DalException(DATASOURCE_PROPERTIES_EXCEPTION_MESSAGE);

        DataSourceConfigure c = cloneDataSourceConfigure(null);
        overrideProperties(connectionStringConfigure, connectionString, wrapper, c, name, logName);
        return c;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
