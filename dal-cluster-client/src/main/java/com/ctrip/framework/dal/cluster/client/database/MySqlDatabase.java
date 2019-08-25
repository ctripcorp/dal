package com.ctrip.framework.dal.cluster.client.database;

import com.ctrip.framework.dal.cluster.client.config.DatabaseConfigImpl;

/**
 * @author c7ch23en
 */
public class MySqlDatabase extends AbstractDatabase {

    private static final String CONNECTION_URL_PATTERN = "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=%s";
    private static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

    public MySqlDatabase(DatabaseConfigImpl databaseConfig) {
        super(databaseConfig);
    }

    @Override
    protected String buildPrimaryConnectionUrl() {
        return String.format(CONNECTION_URL_PATTERN,
                getPrimaryHost(), getPrimaryPort(), getDatabaseConfig().getDbName(), getDatabaseConfig().getCharset());
    }

    @Override
    protected String buildFailOverConnectionUrl() {
        return String.format(CONNECTION_URL_PATTERN,
                getFailOverHost(), getFailOverPort(), getDatabaseConfig().getDbName(), getDatabaseConfig().getCharset());
    }

    @Override
    public String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

}
