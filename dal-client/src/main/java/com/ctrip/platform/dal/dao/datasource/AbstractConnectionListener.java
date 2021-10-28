package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

public abstract class AbstractConnectionListener implements ConnectionListener {
    protected static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private String ON_CREATE_CONNECTION_FORMAT = "[onCreateConnection]%s, %s";
    private String ON_RECREATE_CONNECTION_FORMAT = "[onRecreateConnection]%s, %s";
    private String ON_CREATE_CONNECTION_FAILED_FORMAT = "[onCreateConnectionFailed]%s, %s";
    private String ON_RELEASE_CONNECTION_FORMAT = "[onReleaseConnection]%s, %s";
    private String ON_ABANDON_CONNECTION_FORMAT = "[onAbandonConnection]%s, %s";
    private String ON_WAIT_CONNECTION_FORMAT="[onWaitConnection]%s, %s";

    @Override
    public void onCreateConnection(String poolDesc, Connection connection, DataSourceIdentity dataSourceId, long startTime) {
        if (connection == null)
            return;

        doOnCreateConnection(poolDesc, connection, dataSourceId, startTime);
    }

    @Override
    public void onRecreateConnection(String poolDesc, Connection connection) {
        if (connection == null)
            return;

        doOnRecreateConnection(poolDesc, connection);
    }

    protected void doOnCreateConnection(String poolDesc, Connection connection, DataSourceIdentity dataSourceId, long startTime) {
        logInfo(ON_CREATE_CONNECTION_FORMAT, poolDesc, connection);
    }

    protected void doOnRecreateConnection(String poolDesc, Connection connection) {
        logInfo(ON_RECREATE_CONNECTION_FORMAT, poolDesc, connection);
    }

    @Override
    public void onCreateConnectionFailed(String poolDesc, String connDesc, DataSourceIdentity dataSourceId, Throwable exception, long startTime) {
        if (exception == null)
            return;

        doOnCreateConnectionFailed(poolDesc, connDesc, dataSourceId, exception, startTime);
    }

    protected void doOnCreateConnectionFailed(String poolDesc, String connDesc, DataSourceIdentity dataSourceId, Throwable exception, long startTime) {
        logError(ON_CREATE_CONNECTION_FAILED_FORMAT, poolDesc, connDesc, exception);
    }

    @Override
    public void onReleaseConnection(String poolDesc, Connection connection) {
        if (connection != null) {
            doOnReleaseConnection(poolDesc, connection);
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

//    @Override
//    public void onBorrowConnection(String poolDesc, Connection connection) {
//        if (connection != null)
//            doOnBorrowConnection(poolDesc, connection);
//    }
//
//    protected void doOnBorrowConnection(String poolDesc, Connection connection) {
//        logInfo(ON_BORROW_CONNECTION_FORMAT, poolDesc, connection);
//    }

    @Override
    public void onWaitConnection(String poolDesc, Connection connection, long startTime) {
        if (connection != null)
            doOnWaitConnection(poolDesc, connection,startTime);
    }

    protected void doOnWaitConnection(String poolDesc, Connection connection,long startTime) {
        logInfo(ON_WAIT_CONNECTION_FORMAT, poolDesc, connection);
    }

    private void logInfo(String format, String poolDesc, Connection connection) {
        String connectionUrl = getConnectionUrl(connection);
        String msg = String.format(format, poolDesc, connectionUrl);
        LOGGER.info(msg);
    }

    private void logError(String format, String poolDesc, String connectionUrl, Throwable exception) {
        String msg = String.format(format, poolDesc, connectionUrl);
        LOGGER.error(msg, exception);
    }

    protected String getConnectionUrl(Connection connection) {
        String url = "";
        if (connection == null)
            return url;

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            if (metaData == null)
                return url;

            url = LoggerHelper.getSimplifiedDBUrl(metaData.getURL());
        } catch (Throwable e) {
            return url;
        }

        return url;
    }

    public static void setILogger(ILogger logger) {
        AbstractConnectionListener.LOGGER = logger;
    }

}
