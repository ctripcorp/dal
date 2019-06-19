package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.tomcat.DalTomcatDataSource;
import com.ctrip.platform.dal.dao.helper.*;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.Validator;

import javax.sql.DataSource;

public class SingleDataSource implements DataSourceConfigureConstants {
    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();
    private String name;
    private DataSourceConfigure dataSourceConfigure;
    private DataSource dataSource;
    private DataSourceCreateTask task;
    private DataSourceCreatePoolListener listener;

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

    public DataSourceCreateTask getTask() {
        return task;
    }

    public void setTask(DataSourceCreateTask task) {
        this.task = task;
    }

    public SingleDataSource(String name, DataSourceConfigure dataSourceConfigure) {
        this(name, dataSourceConfigure, null);
        createPool(name, dataSourceConfigure);
    }

    public SingleDataSource(String name, DataSourceConfigure dataSourceConfigure, DataSourceCreateTask task) {
        if (dataSourceConfigure == null)
            throw new DalRuntimeException("Can not find any connection configure for " + name);

        this.name = name;
        this.dataSourceConfigure = dataSourceConfigure;
        this.task = task;

        createDataSource(name, dataSourceConfigure);
    }

    private void createDataSource(String name, DataSourceConfigure dataSourceConfigure) {
        try {
            PoolProperties poolProperties = poolPropertiesHelper.convert(dataSourceConfigure);
            setPoolPropertiesIntoValidator(poolProperties);

            final org.apache.tomcat.jdbc.pool.DataSource dataSource = new DalTomcatDataSource(poolProperties);
            this.dataSource = dataSource;
        } catch (Throwable e) {
            LOGGER.error(String.format("Error creating datasource for %s", name), e);
        }
    }

    public void createPool(String name, DataSourceConfigure dataSourceConfigure) {
        try {
            String message = String.format("Datasource[name=%s, Driver=%s] created,connection url:%s", name,
                    dataSourceConfigure.getDriverClass(), dataSourceConfigure.getConnectionUrl());
            long startTime = System.currentTimeMillis();
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).createPool();
            LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, String.format(DATASOURCE_CREATE_DATASOURCE, name), message, startTime);
            LOGGER.info(message);
            if (listener != null)
                listener.onCreatePoolSuccess();
        } catch (Throwable e) {
            LOGGER.error(String.format("Error creating pool for data source %s", name), e);
            if (listener != null)
                listener.onCreatePoolFail(e);
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

    public void setListener(DataSourceCreatePoolListener listener) {
        this.listener = listener;
    }

}
