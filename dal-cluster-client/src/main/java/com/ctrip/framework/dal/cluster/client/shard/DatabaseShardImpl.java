package com.ctrip.framework.dal.cluster.client.shard;

import com.ctrip.framework.dal.cluster.client.config.DatabaseShardConfigImpl;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;

import java.util.LinkedList;
import java.util.List;

/**
 * @author c7ch23en
 */
public class DatabaseShardImpl implements DatabaseShard {

    private DatabaseShardConfigImpl databaseShardConfig;
    private Database master;
    private List<Database> slaves = new LinkedList<>();

    public DatabaseShardImpl(DatabaseShardConfigImpl databaseShardConfig) {
        this.databaseShardConfig = databaseShardConfig;
    }

    @Override
    public int getShardIndex() {
        return databaseShardConfig.getShardIndex();
    }

    public void addDatabase(Database database) {
        if (database.isMaster()) {
            if (master != null)
                throw new ClusterRuntimeException("duplicated master");
            master = database;
        } else {
            slaves.add(database);
        }
    }

}
