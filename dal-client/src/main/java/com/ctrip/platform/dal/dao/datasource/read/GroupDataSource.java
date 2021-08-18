package com.ctrip.platform.dal.dao.datasource.read;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.IntegratedConfigProvider;
import com.ctrip.platform.dal.dao.datasource.AbstractDataSource;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupDataSource extends AbstractDataSource {
    private static final Integer DEFAULT_SHARD = null;
    private static final String DUPLICATE_INIT = "%s:%d has already been initialized";
    private static final String UNINITIALIZED = "%s:%d has not been initialized";
    private static final String IGNORE_EXTERNAL_EXCEPTION = "ignoreExternalException";

    private DataSource writeDataSource;
    private Map<Database, DataSource> readDataSource = new ConcurrentHashMap<>();

    protected volatile boolean init = false;
    protected ClusterInfo clusterInfo;
    protected IntegratedConfigProvider provider;
    protected String clusterName;
    protected Cluster cluster;
    DataSourceLocator locator;

    public GroupDataSource(ClusterInfo clusterInfo, IntegratedConfigProvider provider) {
        this.clusterInfo = clusterInfo;
        this.cluster = clusterInfo.getCluster();
        this.clusterName = cluster.getClusterName();
        this.provider = provider;
        locator = new DataSourceLocator(this.provider);
        init();
    }

    private void init() {
        writeDataSource = createWriteDataSource();
        createReadDataSource();
        init = true;
    }

    private DataSource createWriteDataSource() {
        ClusterInfo masterClusterInfo = clusterInfo.defineRoleClone(DatabaseRole.MASTER, null);
        return locator.getDataSource(masterClusterInfo);
    }

    private void createReadDataSource() {
        int slaveIndex = 0;
        for (Database database : cluster.getSlavesOnShard(clusterInfo.getShardIndex())) {
            ClusterInfo slaveClusterInfo = clusterInfo.defineRoleClone(DatabaseRole.SLAVES, slaveIndex);
            readDataSource.put(database, locator.getDataSource(slaveClusterInfo));
            slaveIndex++;
        }
    }


    protected void checkInit() {
        if(!init)
            throw new DalRuntimeException(String.format(UNINITIALIZED, this.clusterName, this.clusterInfo.getShardIndex()));
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(null, null);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        checkInit();
        return new GroupConnection(this.clusterInfo, writeDataSource, readDataSource);
    }
}
