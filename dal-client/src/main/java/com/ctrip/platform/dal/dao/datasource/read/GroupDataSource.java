package com.ctrip.platform.dal.dao.datasource.read;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.datasource.AbstractDataSource;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class GroupDataSource extends AbstractDataSource {
    private static final Integer DEFAULT_SHARD = null;
    private static final String DUPLICATE_INIT = "%s:%d has already been initialized";
    private static final String UNINITIALIZED = "%s:%d has not been initialized";
    private static final String IGNORE_EXTERNAL_EXCEPTION = "ignoreExternalException";

    private DataSource writeDataSource;
    private DataSource

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
        return new GroupConnection(this.clusterInfo, this.shardIndex);
    }
}
