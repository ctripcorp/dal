package com.ctrip.datasource.configure;

import com.ctrip.framework.dal.cluster.client.util.PropertiesUtils;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.DatabasePropertiesParser;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.util.Properties;

/**
 * @author c7ch23en
 */
public abstract class CtripLocalDatabasePropertiesParser implements DatabasePropertiesParser {

    public static final String FILE_LOCAL_DATABASES = "local-databases.properties";
    protected static final String PROPERTY_DB_CATEGORY = "dbCategory";
    protected static final String PROPERTY_HOST = "host";
    protected static final String PROPERTY_PORT = "port";
    protected static final String PROPERTY_DB_NAME = "dbName";
    protected static final String PROPERTY_UID = "uid";
    protected static final String PROPERTY_PWD = "pwd";
    protected static final String TITAN_KEY_SUFFIX = "(_w|_w_sh|_r|_r_sh|_s|_s_sh|_read|_read_sh|_sh)$";

    private final Properties properties;
    private final String databaseKey;

    public static CtripLocalDatabasePropertiesParser newInstance(Properties properties) {
        return newInstance(properties, null);
    }

    public static CtripLocalDatabasePropertiesParser newInstance(Properties properties, String databaseKey) {
        String dbCategory = PropertiesUtils.getProperty(properties, PROPERTY_DB_CATEGORY, null);
        if (StringUtils.isEmpty(dbCategory) ||
                CtripLocalMySQLPropertiesParser.DB_CATEGORY.equalsIgnoreCase(dbCategory))
            return new CtripLocalMySQLPropertiesParser(properties, databaseKey);
        else if (CtripLocalSQLServerPropertiesParser.DB_CATEGORY.equalsIgnoreCase(dbCategory))
            return new CtripLocalSQLServerPropertiesParser(properties, databaseKey);
        throw new DalRuntimeException("Invalid dbCategory");
    }

    public CtripLocalDatabasePropertiesParser(Properties properties, String databaseKey) {
        this.properties = properties;
        this.databaseKey = databaseKey;
    }

    protected abstract String getDefaultHost();

    protected abstract int getDefaultPort();

    protected abstract String getDefaultDbName();

    protected abstract String getDefaultUid();

    protected abstract String getDefaultPwd();

    @Override
    public String getHost() {
        return PropertiesUtils.getPropertyNotEmpty(properties, PROPERTY_HOST, getDefaultHost());
    }

    @Override
    public int getPort() {
        return PropertiesUtils.getIntPropertyNotEmpty(properties, PROPERTY_PORT, getDefaultPort());
    }

    @Override
    public String getDbName() {
        String dbName = PropertiesUtils.getProperty(properties, PROPERTY_DB_NAME);
        if (StringUtils.isTrimmedEmpty(dbName))
            dbName = parseDefaultDbName();
        if (StringUtils.isTrimmedEmpty(dbName))
            dbName = getDefaultDbName();
        return dbName;
    }

    @Override
    public String getUid() {
        return PropertiesUtils.getPropertyNotEmpty(properties, PROPERTY_UID, getDefaultUid());
    }

    @Override
    public String getPwd() {
        return PropertiesUtils.getProperty(properties, PROPERTY_PWD, getDefaultPwd());
    }

    @Override
    public String getProperty(String key) {
        return PropertiesUtils.getProperty(properties, key);
    }

    protected abstract String getConnectionStringFormat();

    public String buildConnectionString() {
        return String.format(getConnectionStringFormat(), getHost(), getPort(), getUid(), getPwd(), getDbName());
    }

    protected String parseDefaultDbName() {
        if (StringUtils.isTrimmedEmpty(databaseKey))
            return databaseKey;
        String lowerCasedKey = databaseKey.toLowerCase();
        String parsedDbName = tryParseDalCluster(lowerCasedKey);
        if (parsedDbName == null)
            parsedDbName = tryParseTitanKey(lowerCasedKey);
        return parsedDbName;
    }

    protected String tryParseDalCluster(String clusterName) {
        int end = clusterName.lastIndexOf("shardbasedb_dalcluster");
        if (end > 0)
            return clusterName.substring(0, end) + "db";
        if (end == 0)
            return "shardbasedb";
        end = clusterName.lastIndexOf("_dalcluster");
        if (end > 0)
            return clusterName.substring(0, end);
        if (end == 0)
            return "";
        return null;
    }

    protected String tryParseTitanKey(String keyName) {
        int end = keyName.lastIndexOf("db_");
        if (end >= 0)
            return keyName.substring(0, end + 2);
        return keyName.replaceFirst(TITAN_KEY_SUFFIX, "");
    }

}
