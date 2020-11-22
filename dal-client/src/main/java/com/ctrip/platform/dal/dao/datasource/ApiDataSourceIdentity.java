package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.util.ObjectHolder;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.log.NullSqlContext;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ApiDataSourceIdentity implements ClusterInfoDelegateIdentity {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String ID_FORMAT = "%s_api"; //dbName

    private ConnectionStringConfigureProvider provider;
    private String id;
    private final ObjectHolder<Cluster> clusterHolder = new ObjectHolder<>();

    public ApiDataSourceIdentity(ConnectionStringConfigureProvider provider) {
        this.provider = provider;
        init();
    }

    private void init() {
        clusterHolder.getOrCreate(() -> {
            try {
                DalConnectionStringConfigure connectionStringConfigure = provider.getConnectionString();
                id = String.format(ID_FORMAT, connectionStringConfigure.getName());
                if (connectionStringConfigure instanceof InvalidVariableConnectionString) {
                    throw new DalRuntimeException("connectionString invalid",
                            ((InvalidVariableConnectionString) connectionStringConfigure).getConnectionStringException());
                }
                // build cluster
            } catch (Exception e) {
                LOGGER.error("get connectionString from api failed!", e);
                throw new DalRuntimeException(e);
            }
        });
    }

    public ConnectionStringConfigureProvider getProvider() {
        return provider;
    }

    @Override
    public ClusterInfo getClusterInfo() {
        return null;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SqlContext createSqlContext() {
        return new NullSqlContext();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiDataSourceIdentity that = (ApiDataSourceIdentity) o;
        return Objects.equals(provider, that.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider);
    }

}
