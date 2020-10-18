package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionValidator;
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
    private static final String DATASOURCE_CREATE_POOL = "DataSource::createPool:%s";
    private static final String DATASOURCE_REGISTER_DATASOURCE = "registerDataSource:%s";
    private static final String DATASOURCE_UNREGISTER_DATASOURCE = "unregisterDataSource:%s";

    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();
    private volatile String name;
    private volatile DataSourceConfigure dataSourceConfigure;
    private AtomicReference<DataSource> dataSourceRef = new AtomicReference<>();
    private DataSourceCreatePoolListener listener;
    private volatile boolean createPoolTaskCancelled = false;
    private AtomicInteger referenceCount = new AtomicInteger(0);
    private volatile boolean closed = false;

    private final ConnectionValidator clusterConnValidator;

    // sync create pool
    public SingleDataSource(String name, DataSourceConfigure dataSourceConfigure) {
        this(name, dataSourceConfigure, (ConnectionValidator) null);
    }

    public SingleDataSource(String name, DataSourceConfigure dataSourceConfigure,
                            ConnectionValidator clusterConnValidator) {
        this(name, dataSourceConfigure, null, clusterConnValidator);
        createPool();
    }

    // async create pool
    public SingleDataSource(String name, DataSourceConfigure dataSourceConfigure,
                            DataSourceCreatePoolListener listener) {
        this(name, dataSourceConfigure, listener, null);
    }

    public SingleDataSource(String name, DataSourceConfigure dataSourceConfigure,
                            DataSourceCreatePoolListener listener, ConnectionValidator clusterConnValidator) {
        if (dataSourceConfigure == null)
            throw new DalRuntimeException("Can not find any connection configure for " + name);
        this.name = name;
        this.dataSourceConfigure = dataSourceConfigure;
        this.listener = listener;
        this.clusterConnValidator = clusterConnValidator;
        dataSourceRef.set(createDataSource());
    }

    private DataSource createDataSource() {
        long startTime = System.currentTimeMillis();
        try {
            String message = String.format("Datasource[name=%s, Driver=%s] created,connection url:%s", name,
                    dataSourceConfigure.getDriverClass(), dataSourceConfigure.getConnectionUrl());
            PoolProperties poolProperties = poolPropertiesHelper.convert(dataSourceConfigure);
            preHandleValidator(poolProperties, clusterConnValidator);
            DalTomcatDataSource ds =  new DalTomcatDataSource(poolProperties, clusterConnValidator);
            LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, String.format(DATASOURCE_CREATE_DATASOURCE, name), message, startTime);
            LOGGER.info(message);
            return ds;
        } catch (Throwable e) {
            String message = String.format("Error creating datasource for %s", name);
            LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, String.format(DATASOURCE_CREATE_DATASOURCE, name), message, e, startTime);
            LOGGER.error(message, e);
            return null;
        }
    }

    public boolean createPool() {
        long startTime = System.currentTimeMillis();
        try {
            String message = String.format("Datasource[name=%s, Driver=%s] pool created,connection url:%s", name,
                    dataSourceConfigure.getDriverClass(), dataSourceConfigure.getConnectionUrl());
            ((org.apache.tomcat.jdbc.pool.DataSource) getDataSource()).createPool();
            LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, String.format(DATASOURCE_CREATE_POOL, name), message, startTime);
            LOGGER.info(message);
            return true;
        } catch (Throwable e) {
            String message = String.format("Error creating pool for datasource %s", name);
            LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, String.format(DATASOURCE_CREATE_POOL, name), message, e, startTime);
            LOGGER.error(message, e);
            if (listener != null)
                listener.onCreatePoolFail(e);
            return false;
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

    private void preHandleValidator(PoolProperties poolProperties, ConnectionValidator clusterConnValidator) {
        if (poolProperties == null)
            return;
        Validator validator = poolProperties.getValidator();
        if (validator instanceof ValidatorProxy) {
            ValidatorProxy dsValidator = (ValidatorProxy) validator;
            dsValidator.setPoolProperties(poolProperties);
            dsValidator.setClusterConnValidator(clusterConnValidator);
        }
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
        int refCount = referenceCount.incrementAndGet();
        String message = String.format("Datasource [name=%s, driver=%s] registered, connection url=%s, ref count=%d",
                name, dataSourceConfigure.getDriverClass(), dataSourceConfigure.getConnectionUrl(), refCount);
        LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format(DATASOURCE_REGISTER_DATASOURCE, name), message);
        LOGGER.info(message);
        return refCount;
    }

    public int unregister() {
        int refCount = referenceCount.decrementAndGet();
        String message = String.format("Datasource [name=%s, driver=%s] unregistered, connection url=%s, ref count=%d",
                name, dataSourceConfigure.getDriverClass(), dataSourceConfigure.getConnectionUrl(), refCount);
        LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format(DATASOURCE_UNREGISTER_DATASOURCE, name), message);
        LOGGER.info(message);
        return refCount;
    }

    public int getReferenceCount() {
        return referenceCount.get();
    }

    public void setClosed() {
        closed = true;
    }

}
