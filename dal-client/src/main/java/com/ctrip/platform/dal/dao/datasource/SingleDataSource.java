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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SingleDataSource implements DataSourceConfigureConstants, DataSourceCreatePoolTask {

    private static final String DATASOURCE_CREATE_DATASOURCE = "DataSource::createDataSource:%s";

    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();
    private volatile String name;
    private volatile DataSourceConfigure dataSourceConfigure;
    private AtomicReference<DataSource> dataSourceRef = new AtomicReference<>();
    private DataSourceCreatePoolListener listener;
    private volatile boolean createPoolTaskCancelled = false;
    private AtomicInteger referenceCount = new AtomicInteger(0);
    private volatile boolean closed = false;

    // sync create pool
    public SingleDataSource(String name, DataSourceConfigure dataSourceConfigure) {
        this(name, dataSourceConfigure, null);
        createPool();
    }

    // async create pool
    public SingleDataSource(String name, DataSourceConfigure dataSourceConfigure, DataSourceCreatePoolListener listener) {
        if (dataSourceConfigure == null)
            throw new DalRuntimeException("Can not find any connection configure for " + name);
        this.name = name;
        this.dataSourceConfigure = dataSourceConfigure;
        this.listener = listener;
        dataSourceRef.set(createDataSource());
    }

    private DataSource createDataSource() {
        try {
            PoolProperties poolProperties = poolPropertiesHelper.convert(dataSourceConfigure);
            setPoolPropertiesIntoValidator(poolProperties);
            return new DalTomcatDataSource(poolProperties);
        } catch (Throwable e) {
            LOGGER.error(String.format("Error creating datasource for %s", name), e);
            return null;
        }
    }

    private void createPool() {
        try {
            String message = String.format("Datasource[name=%s, Driver=%s] created,connection url:%s", name,
                    dataSourceConfigure.getDriverClass(), dataSourceConfigure.getConnectionUrl());
            long startTime = System.currentTimeMillis();
            ((org.apache.tomcat.jdbc.pool.DataSource) getDataSource()).createPool();
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

    @Override
    public void run() {
        if (!createPoolTaskCancelled)
            createPool();
    }

    @Override
    public void cancelTask() {
        createPoolTaskCancelled = true;
    }

    public String getName() {
        return name;
    }

    public DataSourceConfigure getDataSourceConfigure() {
        return dataSourceConfigure;
    }

    public DataSource getDataSource() {
        if (closed) {
            reCreateDataSource();
            createPool();
        }
        return dataSourceRef.get();
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

    public void reCreateDataSource() {
        DataSource newDs = createDataSource();
        if (newDs != null) {
            DataSource oldDs = dataSourceRef.getAndSet(newDs);
            closed = false;
            DataSourceTerminator.getInstance().close(name, oldDs, dataSourceConfigure);
        }
    }

    public int register() {
        return referenceCount.incrementAndGet();
    }

    public int unRegister() {
        return referenceCount.decrementAndGet();
    }

    public int getReferenceCount() {
        return referenceCount.get();
    }

    public void setClosed() {
        closed = true;
    }

}
