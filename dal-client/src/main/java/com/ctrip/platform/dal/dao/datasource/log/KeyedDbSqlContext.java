package com.ctrip.platform.dal.dao.datasource.log;

import java.util.Map;

/**
 * @author c7ch23en
 */
public class KeyedDbSqlContext extends BaseSqlContext {

    protected static final String DB_KEY = "DB";

    private final String databaseKey;

    public KeyedDbSqlContext(String databaseKey) {
        this(databaseKey, null);
    }

    public KeyedDbSqlContext(String databaseKey, String dbName) {
        super(dbName);
        this.databaseKey = databaseKey;
    }

    public KeyedDbSqlContext(String databaseKey, String clientVersion, String clientZone, String dbName) {
        super(clientVersion, clientZone, dbName);
        this.databaseKey = databaseKey;
    }

    @Override
    protected Map<String, String> toMetricTags() {
        Map<String, String> tags = super.toMetricTags();
        addTag(tags, DB_KEY, databaseKey);
        return tags;
    }

    @Override
    public SqlContext fork() {
        KeyedDbSqlContext context = new KeyedDbSqlContext(databaseKey, getClientVersion(), getClientZone(), getDbName());
        context.populateDbZone(getDbZone());
        context.populateDatabase(getDatabase());
        return context;
    }

}
