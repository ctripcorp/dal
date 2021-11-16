package com.ctrip.framework.dal.cluster.client.database;

import com.ctrip.framework.dal.cluster.client.config.DatabaseConfigImpl;

/**
 * @Author limingdong
 * @create 2021/10/11
 */
public class DummyDatabase extends AbstractDatabase {

    public DummyDatabase(DatabaseConfigImpl databaseConfig) {
        super(databaseConfig);
    }

    @Override
    protected String buildPrimaryConnectionUrl() {
        return null;
    }

    @Override
    protected String buildFailOverConnectionUrl() {
        return null;
    }

    @Override
    public String getDriverClassName() {
        return null;
    }

    @Override
    public String getPrimaryConnectionUrl() {
        return getDatabaseConfig().getDatabaseShardConfig().getMasterDomain();
    }
}
