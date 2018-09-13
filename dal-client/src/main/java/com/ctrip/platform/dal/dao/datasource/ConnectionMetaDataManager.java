package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConnectionMetaDataManager {
    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static volatile ConnectionMetaDataManager instance = null;
    private static final Object LOCK = new Object();
    private static final String NULL = "null";

    private ConcurrentMap<String, ConnectionMetaData> metaDataMap = new ConcurrentHashMap<>();

    public static ConnectionMetaDataManager getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ConnectionMetaDataManager();
                }
            }
        }

        return instance;
    }

    public void put(Connection connection) {
        if (connection == null)
            return;

        try {
            String connectionId = connection.toString();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String url = LoggerHelper.getSimplifiedDBUrl(databaseMetaData.getURL());
            String userName = databaseMetaData.getUserName();

            ConnectionMetaData connectionMetaData = new ConnectionMetaData();
            connectionMetaData.setConnectionUrl(url);
            connectionMetaData.setUserName(userName);

            metaDataMap.put(connectionId, connectionMetaData);
            LOGGER.info(String.format("%s put to connection metadata.", connectionId));
        } catch (Throwable e) {
        }
    }

    public void remove(Connection connection) {
        if (connection == null)
            return;

        try {
            String connectionId = connection.toString();
            metaDataMap.remove(connectionId);
            LOGGER.info(String.format("%s removed from connection metadata.", connectionId));
        } catch (Throwable e) {
        }
    }

    public ConnectionMetaData get(Connection connection) {
        if (connection == null)
            return null;

        String connectionId = connection.toString();
        return metaDataMap.get(connectionId);
    }

    public String getConnectionUrl(Connection connection) {
        ConnectionMetaData metaData = get(connection);
        return getConnectionUrlByMetaData(metaData);
    }

    public String getConnectionUrlByMetaData(ConnectionMetaData metaData) {
        return metaData == null ? NULL : metaData.getConnectionUrl();
    }

}
