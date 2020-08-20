package com.ctrip.datasource.configure;

import java.util.Properties;

/**
 * @author c7ch23en
 */
public class CtripLocalSQLServerPropertiesParser extends CtripLocalDatabasePropertiesParser {

    protected static final String DB_CATEGORY = "sqlserver";
    protected static final String DEFAULT_HOST = "127.0.0.1";
    protected static final int DEFAULT_PORT = 1499;
    protected static final String DEFAULT_DB_NAME = "test";
    protected static final String DEFAULT_UID = "sa";
    protected static final String DEFAULT_PWD = "";
    protected static final String CONNECTION_STRING_FORMAT = "Data Source=%s,%s;UID=%s;password=%s;database=%s";

    public CtripLocalSQLServerPropertiesParser(Properties properties, String databaseKey) {
        super(properties, databaseKey);
    }

    @Override
    protected String getDefaultHost() {
        return DEFAULT_HOST;
    }

    @Override
    protected int getDefaultPort() {
        return DEFAULT_PORT;
    }

    @Override
    protected String getDefaultDbName() {
        return DEFAULT_DB_NAME;
    }

    @Override
    protected String getDefaultUid() {
        return DEFAULT_UID;
    }

    @Override
    protected String getDefaultPwd() {
        return DEFAULT_PWD;
    }

    @Override
    protected String getConnectionStringFormat() {
        return CONNECTION_STRING_FORMAT;
    }

}
