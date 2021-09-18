package com.ctrip.platform.dal.dao.datasource;


import com.ctrip.framework.dal.cluster.client.database.ConnectionString;
import com.ctrip.framework.dal.cluster.client.database.Database;

public class TagDataSourceIdentity extends TraceableClusterDataSourceIdentity {

    private String id;

    private static final String ID_FORMAT = "%s-%d-%s-%s-%s"; // cluster-shard-role-host-tag
    private static final String MASTER = "master";
    private static final String SLAVE = "slave";

    public TagDataSourceIdentity(Database database, String tag) {
        super(database);
        init(database, tag);
    }

    private void init(Database database, String tag) {
        String role = database.isMaster() ? MASTER : SLAVE;
        ConnectionString connString = database.getConnectionString();
        id = String.format(ID_FORMAT, database.getClusterName(), database.getShardIndex(), role,
                connString.getPrimaryHost(), tag);
    }

    @Override
    public String getId() {
        return id;
    }
}
