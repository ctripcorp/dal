package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.tomcat.DalTomcatDataSource;
import com.ctrip.platform.dal.dao.helper.*;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.Validator;

import javax.sql.DataSource;
import java.sql.SQLException;

public class SingleDataSource implements DataSourceConfigureConstants {
    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();
    private String name;
    private DataSourceConfigure dataSourceConfigure;
    private DataSource dataSource;

    private static final String DATASOURCE_CREATE_DATASOURCE = "DataSource::createDataSource:%s";

    public String getName() {
        return name;
    }

    public DataSourceConfigure getDataSourceConfigure() {
        return dataSourceConfigure;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public SingleDataSource(String name, DataSourceConfigure dataSourceConfigure) throws SQLException {
        if (dataSourceConfigure == null)
            throw new SQLException("Can not find any connection configure for " + name);

        createPool(name, dataSourceConfigure);
    }

    private void createPool(String name, DataSourceConfigure dataSourceConfigure) {
        try {
            this.name = name;
            this.dataSourceConfigure = dataSourceConfigure;

            PoolProperties poolProperties = poolPropertiesHelper.convert(dataSourceConfigure);
            setPoolPropertiesIntoValidator(poolProperties);

            final org.apache.tomcat.jdbc.pool.DataSource dataSource = new DalTomcatDataSource(poolProperties);
            this.dataSource = dataSource;

            String message = String.format("Datasource[name=%s, Driver=%s] created,connection url:%s", name,
                    poolProperties.getDriverClassName(), dataSourceConfigure.getConnectionUrl());
            long startTime = System.currentTimeMillis();
            dataSource.createPool();
            LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, String.format(DATASOURCE_CREATE_DATASOURCE, name), message, startTime);
            LOGGER.info(message);
        } catch (Throwable e) {
            LOGGER.error(String.format("Error creating pool for data source %s", name), e);
            throw new RuntimeException(e);
        }
    }

    private void setPoolPropertiesIntoValidator(PoolProperties poolProperties) {
        if (poolProperties == null)
            return;

        Validator validator = poolProperties.getValidator();
        if (validator == null)
            return;

        if (!(validator instanceof ValidatorProxy))
            return;

        ValidatorProxy dsValidator = (ValidatorProxy) validator;
        dsValidator.setPoolProperties(poolProperties);
    }

}
