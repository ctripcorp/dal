package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.sql.Connection;

public abstract class AbstractConnectionListener implements ConnectionListener {
    protected static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private String BEFORE_CREATE_CONNECTION_FORMAT = "[beforeCreateConnection]%s";
    private String ON_CREATE_CONNECTION_FORMAT = "[onCreateConnection]%s, %s";
    private String ON_RELEASE_CONNECTION_FORMAT = "[onReleaseConnection]%s, %s";
    private String ON_ABANDON_CONNECTION_FORMAT = "[onAbandonConnection]%s, %s";

    private ConnectionMetaDataManager metaDataManager = ConnectionMetaDataManager.getInstance();

    @Override
    public void onCreateConnection(String poolDesc, CreateConnectionCallback callback) {
        if (callback == null)
            return;

        doOnCreateConnection(poolDesc, callback);
    }

    protected void doOnCreateConnection(String poolDesc, CreateConnectionCallback callback) {
        try {
            logInfo(BEFORE_CREATE_CONNECTION_FORMAT, poolDesc);
            Connection connection = callback.createConnection();
            putConnectionMetaDataToCache(connection);
            logInfo(ON_CREATE_CONNECTION_FORMAT, poolDesc, connection);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void onReleaseConnection(String poolDesc, Connection connection) {
        if (connection != null) {
            doOnReleaseConnection(poolDesc, connection);
            removeConnectionUrlFromCache(connection);
        }
    }

    protected void doOnReleaseConnection(String poolDesc, Connection connection) {
        logInfo(ON_RELEASE_CONNECTION_FORMAT, poolDesc, connection);
    }

    @Override
    public void onAbandonConnection(String poolDesc, Connection connection) {
        if (connection != null)
            doOnAbandonConnection(poolDesc, connection);
    }

    protected void doOnAbandonConnection(String poolDesc, Connection connection) {
        logInfo(ON_ABANDON_CONNECTION_FORMAT, poolDesc, connection);
    }

    private void logInfo(String format, String poolDesc) {
        LOGGER.info(String.format(format, poolDesc));
    }

    private void logInfo(String format, String poolDesc, Connection connection) {
        String connDesc = connectionDesc(connection);
        LOGGER.info(String.format(format, poolDesc, connDesc));
    }

    protected String connectionDesc(Connection connection) {
        return metaDataManager.getConnectionUrl(connection);
    }

    private void putConnectionMetaDataToCache(Connection connection) {
        metaDataManager.put(connection);
    }

    private void removeConnectionUrlFromCache(Connection connection) {
        metaDataManager.remove(connection);
    }

    public static void setILogger(ILogger logger) {
        AbstractConnectionListener.LOGGER = logger;
    }

}
