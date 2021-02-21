package com.ctrip.framework.dal.cluster.client.shard;

import com.ctrip.framework.dal.cluster.client.config.DatabaseShardConfigImpl;
import com.ctrip.framework.dal.cluster.client.database.Database;

import java.util.LinkedList;
import java.util.List;

/**
 * @author c7ch23en
 */
public class DatabaseShardImpl implements DatabaseShard {

    private DatabaseShardConfigImpl databaseShardConfig;
    private List<Database> masters = new LinkedList<>();
    private List<Database> slaves = new LinkedList<>();

    public DatabaseShardImpl(DatabaseShardConfigImpl databaseShardConfig) {
        this.databaseShardConfig = databaseShardConfig;
    }

    @Override
    public int getShardIndex() {
        return databaseShardConfig.getShardIndex();
    }

    @Override
    public List<Database> getMasters() {
        return new LinkedList<>(masters);
    }

    @Override
    public List<Database> getSlaves() {
        return new LinkedList<>(slaves);
    }

    public void addDatabase(Database database) {
        if (database.isMaster())
            masters.add(database);
        else
            slaves.add(database);
    }

}
