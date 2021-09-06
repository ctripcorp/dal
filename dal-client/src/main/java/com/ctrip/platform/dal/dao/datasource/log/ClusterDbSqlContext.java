package com.ctrip.platform.dal.dao.datasource.log;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterType;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.dal.cluster.client.config.LocalizationState;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;

import java.util.Map;

/**
 * @author c7ch23en
 */
public class ClusterDbSqlContext extends BaseSqlContext {

    protected static final String CLUSTER = "Cluster";
    protected static final String SHARD = "Shard";
    protected static final String ROLE = "Role";
    protected static final String DB_KEY = "DB";
    protected static final String CLUSTER_TYPE = "ClusterType";
    protected static final String UCS_STRATEGY = "UcsStrategy";

    private final Cluster cluster;
    private Integer shard;
    private DatabaseRole role;

    public ClusterDbSqlContext(Cluster cluster, Integer shard, DatabaseRole role) {
        this(cluster, shard, role, null);
    }

    public ClusterDbSqlContext(Cluster cluster, Integer shard, DatabaseRole role, String dbName) {
        super(dbName);
        this.cluster = cluster;
        this.shard = shard;
        this.role = role;
    }

    public ClusterDbSqlContext(Cluster cluster, Integer shard, DatabaseRole role,
                               String clientVersion, String clientZone, String dbName) {
        super(clientVersion, clientZone, dbName);
        this.cluster = cluster;
        this.shard = shard;
        this.role = role;
    }

    @Override
    protected Map<String, String> toMetricTags() {
        Map<String, String> tags = super.toMetricTags();
        addTag(tags, CLUSTER, cluster.getClusterName());
        addTag(tags, SHARD, shard);
        addTag(tags, ROLE, role);
        addTag(tags, DB_KEY, String.format("%s-%s-%s", cluster, shard, role));
        ClusterType type = cluster.getClusterType();
        if (type == ClusterType.DRC) {
            LocalizationConfig config = cluster.getLocalizationConfig();
            if (config != null && config.getLocalizationState() == LocalizationState.ACTIVE)
                tags.put(CLUSTER_TYPE, type.getValue() + "_" + config.getLocalizationState().getValue());
            else
                tags.put(CLUSTER_TYPE, type.getValue() + "_" + LocalizationState.PREPARED.getValue());
            if (config != null && config.getUnitStrategyId() != null)
                tags.put(UCS_STRATEGY, String.valueOf(config.getUnitStrategyId()));
        } else if (type != null)
            tags.put(CLUSTER_TYPE, type.getValue());
        return tags;
    }

    @Override
    protected void addTag(Map<String, String> tags, String tagName, Object tagValue) {
        super.addTag(tags, tagName, tagValue);
        if (tagValue instanceof Integer)
            tags.put(tagName, tagValue.toString());
        else if (tagValue instanceof DatabaseRole)
            tags.put(tagName, ((DatabaseRole) tagValue).getValue());
    }

    @Override
    public SqlContext fork() {
        ClusterDbSqlContext context = new ClusterDbSqlContext(cluster, shard, role,
                getClientVersion(), getClientZone(), getDbName());
        context.populateDbZone(getDbZone());
        context.populateDatabase(getDatabase());
        return context;
    }

    public String getClusterName() {
        return cluster.getClusterName();
    }

    public Integer getShard() {
        return shard;
    }

    public void populateShard(Integer shard) {
        this.shard = shard;
    }

    public void populateRole(DatabaseRole role) {
        this.role = role;
    }

}
