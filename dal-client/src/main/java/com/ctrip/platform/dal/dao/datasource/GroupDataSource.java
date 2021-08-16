package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class GroupDataSource extends AbstractDataSource {
    private static final Integer DEFAULT_SHARD = null;
    private static final String DUPLICATE_INIT = "%s:%d has already been initialized";
    private static final String UNINITIALIZED = "%s:%d has not been initialized";
    private static final String IGNORE_EXTERNAL_EXCEPTION = "ignoreExternalException";

    protected DataSource writeDataSource;
    protected Map<Database, DataSource> readDataSource;
    protected volatile boolean init = false;
    protected ClusterInfo clusterInfo;
    protected Integer shardIndex;
    protected String clusterName;
    protected Cluster cluster;

    public GroupDataSource(ClusterInfo clusterInfo) {
        this(clusterInfo, DEFAULT_SHARD);
    }

    public GroupDataSource(ClusterInfo clusterInfo, Integer shardIndex) {
        this.clusterInfo = clusterInfo;
        this.cluster = clusterInfo.getCluster();
        this.clusterName = cluster.getClusterName();
        this.shardIndex = shardIndex;

        init(clusterInfo, shardIndex);
    }

    protected synchronized void init(ClusterInfo clusterInfo, Integer shardIndex) {
        if (init)
            throw new DalRuntimeException(String.format(DUPLICATE_INIT, clusterName, shardIndex));

        //todo-lhj  初始化动态数据源

        this.init = true;
    }


    protected void checkInit() {
        if(!init)
            throw new DalRuntimeException(String.format(UNINITIALIZED, this.clusterName, this.shardIndex));
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(null, null);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }
}
