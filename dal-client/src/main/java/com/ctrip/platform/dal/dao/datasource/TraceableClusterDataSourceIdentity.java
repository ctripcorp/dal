package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.database.Database;
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
        ClusterDbSqlContext context = new ClusterDbSqlContext(getClusterName(), getShardIndex(), getDatabaseRole());
        Cluster cluster = getDatabase().getCluster();
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
