package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.dao.datasource.log.ClusterDbSqlContext;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;

import java.util.Objects;

/**
 * @author c7ch23en
 */
public class DefaultClusterIdentity implements ClusterIdentity {

    private final Cluster cluster;

    public DefaultClusterIdentity(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public String getClusterName() {
        return cluster.getClusterName();
    }

    @Override
    public String getId() {
        return getClusterName();
    }

    @Override
    public SqlContext createSqlContext() {
        ClusterDbSqlContext context = new ClusterDbSqlContext(cluster, null, null);
        if (cluster != null && cluster.getLocalizationConfig() != null)
            context.populateDbZone(cluster.getLocalizationConfig().getZoneId());
        return context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultClusterIdentity that = (DefaultClusterIdentity) o;
        return Objects.equals(getClusterName().toLowerCase(), that.getClusterName().toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClusterName().toLowerCase());
    }

    @Override
    public String toString() {
        return "DefaultClusterIdentity{" +
                "clusterName='" + getClusterName() + '\'' +
                '}';
    }

}
