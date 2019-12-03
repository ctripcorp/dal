package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterSwitchedEvent;
import com.ctrip.platform.dal.dao.configure.*;

import javax.sql.DataSource;
import java.sql.SQLException;

public class ClusterDynamicDataSource extends ForceSwitchableDataSource implements DataSource {

    private ClusterInfo clusterInfo;
    private Cluster cluster;
    private DataSourceConfigureProvider provider;

    public ClusterDynamicDataSource(ClusterInfo clusterInfo, Cluster cluster, DataSourceConfigureProvider provider) {
        super(getDataSourceIdentity(clusterInfo), new ClusterDynamicDataSourceConfigProvider(clusterInfo, cluster, provider));
        this.cluster = cluster;
        this.clusterInfo = clusterInfo;
        this.provider = provider;
        registerListener();
    }

    private void registerListener() {
        cluster.addListener(new Listener<ClusterSwitchedEvent>() {
            @Override
            public void onChanged(ClusterSwitchedEvent event) {
                try {
                    refresh();
                } catch (Throwable t) {
                }
            }
        });
    }

    private void refresh() throws SQLException {
        DataSourceIdentity id = getDataSourceIdentity(clusterInfo, cluster);
        String name = id.getId();
        DataSourceConfigure oldConfigure = getSingleDataSource().getDataSourceConfigure();
        DataSourceConfigure newConfigure = provider.getDataSourceConfigure(id);
        DataSourceConfigureChangeEvent event = new DataSourceConfigureChangeEvent(name, newConfigure, oldConfigure);
        super.configChanged(event);
    }

    private static DataSourceIdentity getDataSourceIdentity(ClusterInfo clusterInfo) {
        return clusterInfo.toDataSourceIdentity();
    }

    private static DataSourceIdentity getDataSourceIdentity(ClusterInfo clusterInfo, Cluster cluster) {
        switch (clusterInfo.getRole()) {
            case MASTER:
                return new ClusterDataSourceIdentity(cluster.getMasterOnShard(clusterInfo.getShardIndex()));
            default:
                throw new UnsupportedOperationException(String.format("unsupported role '%s' for cluster '%s'",
                        clusterInfo.getRole().getValue(), clusterInfo.getClusterName()));
        }
    }

    static class ClusterDynamicDataSourceConfigProvider implements IDataSourceConfigureProvider {

        private ClusterInfo clusterInfo;
        private Cluster cluster;
        private DataSourceConfigureProvider provider;

        public ClusterDynamicDataSourceConfigProvider(ClusterInfo clusterInfo, Cluster cluster, DataSourceConfigureProvider provider) {
            this.clusterInfo = clusterInfo;
            this.cluster = cluster;
            this.provider = provider;
        }

        @Override
        public IDataSourceConfigure getDataSourceConfigure() {
            DataSourceIdentity id = getDataSourceIdentity(clusterInfo, cluster);
            return provider.getDataSourceConfigure(id);
        }

        @Override
        public IDataSourceConfigure forceLoadDataSourceConfigure() {
            return getDataSourceConfigure();
        }

    }

}
