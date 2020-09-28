package com.ctrip.platform.dal.dao.datasource.log;

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

    private final String cluster;
    private final Integer shard;
    private final DatabaseRole role;

    public ClusterDbSqlContext(String cluster, Integer shard, DatabaseRole role) {
        this(cluster, shard, role, null);
    }

    public ClusterDbSqlContext(String cluster, Integer shard, DatabaseRole role, String dbName) {
        super(dbName);
        this.cluster = cluster;
        this.shard = shard;
        this.role = role;
    }

    public ClusterDbSqlContext(String cluster, Integer shard, DatabaseRole role,
                               String clientVersion, String clientZone, String dbName) {
        super(clientVersion, clientZone, dbName);
        this.cluster = cluster;
        this.shard = shard;
        this.role = role;
    }

    @Override
    protected Map<String, String> toMetricTags() {
        Map<String, String> tags = super.toMetricTags();
        addTag(tags, CLUSTER, cluster);
        addTag(tags, SHARD, shard);
        addTag(tags, ROLE, role);
        addTag(tags, DB_KEY, String.format("%s-%s-%s", cluster, shard, role));
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
        return context;
    }

}
