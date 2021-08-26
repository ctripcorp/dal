package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.database.Database;
import com.ctrip.platform.dal.dao.datasource.log.ClusterDbSqlContext;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;

/**
 * @author c7ch23en
 */
public class TraceableClusterDataSourceIdentity extends ClusterDataSourceIdentity {

    public TraceableClusterDataSourceIdentity(Database database) {
        super(database);
    }

    @Override
    public SqlContext createSqlContext() {
        Cluster cluster = getDatabase().getCluster();
        ClusterDbSqlContext context = new ClusterDbSqlContext(cluster, getShardIndex(), getDatabaseRole());
        if (cluster != null && cluster.getLocalizationConfig() != null)
            context.populateDbZone(cluster.getLocalizationConfig().getZoneId());
        return context;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TraceableClusterDataSourceIdentity)
            return super.equals(obj);
        return false;
    }

}
