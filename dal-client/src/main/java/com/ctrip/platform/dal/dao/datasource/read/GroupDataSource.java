package com.ctrip.platform.dal.dao.datasource.read;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.framework.dal.cluster.client.shard.read.RouterType;
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
    private static final String UNINITIALIZED = "%s:%d has not been initialized";

    protected volatile DataSource writeDataSource;
    protected volatile Map<Database, DataSource> readDataSource = new ConcurrentHashMap<>();

    protected volatile boolean init = false;
    protected RouterType routerType;
    protected ClusterInfo clusterInfo;
    protected IntegratedConfigProvider provider;
    protected String clusterName;
    protected Cluster cluster;
    protected DataSourceLocator locator;

    public GroupDataSource(ClusterInfo clusterInfo, IntegratedConfigProvider provider, RouterType routerType) {
        this.clusterInfo = clusterInfo;
        this.cluster = clusterInfo.getCluster();
        this.clusterName = cluster.getClusterName();
        this.routerType = routerType;
        this.provider = provider;
        locator = new DataSourceLocator(this.provider);
        init();
        this.init = true;
    }

    protected void init() {
        if (routerType == RouterType.WRITE_ONLY) {
           createWriteDataSource();
            return;
        }

        createWriteDataSource();
        createReadDataSource();
    }

    protected DataSource createWriteDataSource() {
        ClusterInfo masterClusterInfo = clusterInfo.cloneMaster();
        writeDataSource = locator.getDataSource(masterClusterInfo);
        return writeDataSource;
    }

    protected void createReadDataSource() {
        int slaveIndex = 0;
        for (Database database : cluster.getSlavesOnShard(clusterInfo.getShardIndex())) {
            ClusterInfo slaveClusterInfo = clusterInfo.cloneSlaveWithIndex(slaveIndex);
            readDataSource.put(database, locator.getDataSource(slaveClusterInfo));
            slaveIndex++;
        }

        readDataSource.put(cluster.getMasterOnShard(clusterInfo.getShardIndex()), createWriteDataSource());
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
        return new GroupConnection(this);
    }
}
