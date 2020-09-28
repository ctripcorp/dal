package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocatorManager;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.DataSourceName;
import com.ctrip.platform.dal.dao.datasource.IClusterDataSourceIdentity;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import com.ctrip.platform.dal.dao.log.ILogger;

public class DbMeta {
    private static Pattern hostRegxPattern = null;
    private DataSourceConfigureLocator configureLocator = DataSourceConfigureLocatorManager.getInstance();
    private static ILogger ilogger = DalElementFactory.DEFAULT.getILogger();
    private String databaseName;
    private DatabaseCategory dbCategory;
    private String dataBaseKeyName;
    private String userName;
    private String url;
    private String simplifiedUrl;
    private String host;
    private String databaseId;

    static {
        String regEx = "(?<=://)[\\w\\-_]+(\\.[\\w\\-_]+)+(?=[,|:|;])";
        hostRegxPattern = Pattern.compile(regEx);
    }

    private DbMeta(Connection conn, String dataBaseKeyName, DatabaseCategory dbCategory) throws SQLException {
        this.dataBaseKeyName = dataBaseKeyName;
        init(conn, new DataSourceName(dataBaseKeyName), dbCategory);
    }

    private DbMeta(Connection conn, DataSourceIdentity id, DatabaseCategory dbCategory) throws SQLException {
        this.dataBaseKeyName = id.getId();
        init(conn, id, dbCategory);
    }

    private void init(Connection conn, DataSourceIdentity id, DatabaseCategory dbCategory) {
        this.dbCategory = dbCategory;
        try {
            DatabaseMetaData meta = conn.getMetaData();
            databaseName = conn.getCatalog();
            url = meta.getURL();
            simplifiedUrl = LoggerHelper.getSimplifiedDBUrl(url);
            host = parseHostFromDBURL(url);
            DataSourceConfigure configure = configureLocator.getDataSourceConfigure(id);
            if (configure != null) {
                userName = configure.getUserName();
            }
            if (id instanceof IClusterDataSourceIdentity) {
                IClusterDataSourceIdentity clusterId = (IClusterDataSourceIdentity) id;
                databaseId = String.format("%s-%s-%s", clusterId.getClusterName(),
                        clusterId.getShardIndex(), clusterId.getDatabaseRole().getValue());
            } else {
                databaseId = id.getId();
            }
        } catch (Throwable e) {
            ilogger.error(e.getMessage(),e);
        }

    }

    public void populate(LogEntry entry) {
        entry.setDatabaseName(databaseName);
        entry.setServerAddress(host);
        entry.setDbUrl(simplifiedUrl);
        entry.setUserName(userName);
        entry.setDataBaseKeyName(databaseId);
    }

    public static DbMeta createIfAbsent(String realDbName, DatabaseCategory dbCategory, Connection conn)
            throws SQLException {
        return new DbMeta(conn, realDbName, dbCategory);
    }

    public static DbMeta createIfAbsent(DataSourceIdentity id, DatabaseCategory dbCategory, Connection conn)
            throws SQLException {
        return new DbMeta(conn, id, dbCategory);
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDataBaseKeyName() {
        return dataBaseKeyName;
    }

    public DatabaseCategory getDatabaseCategory() {
        return dbCategory;
    }

    private String parseHostFromDBURL(String url) {
        Matcher m = hostRegxPattern.matcher(url);
        String host = "NA";
        while (m.find()) {
            host = m.group();
            break;
        }
        return host;
    }

}