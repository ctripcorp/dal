package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.datasource.AbstractConnectionListener;
import com.ctrip.platform.dal.dao.datasource.ConnectionListener;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.LogUtils;

import java.sql.Connection;

public class CtripConnectionListener extends AbstractConnectionListener implements ConnectionListener {

    private static final String CONNECTION_CREATE_CONNECTION = "Connection::createConnection";
    private static final String CONNECTION_CREATE_CONNECTION_FAILED = "Connection::createConnectionFailed";
    private static final String CONNECTION_RELEASE_CONNECTION = "Connection::releaseConnection";
    private static final String CONNECTION_ABANDON_CONNECTION = "Connection::abandonConnection";
    private static final String CONNECTION_WAIT_CONNECTION = "Connection::waitConnection";

    @Override
    public void doOnCreateConnection(String poolDesc, Connection connection, DataSourceIdentity dataSourceId, long startTime) {
        super.doOnCreateConnection(poolDesc, connection, dataSourceId, startTime);
        logCatTransaction(CONNECTION_CREATE_CONNECTION, poolDesc, connection, dataSourceId, startTime);
    }

    @Override
    public void doOnCreateConnectionFailed(String poolDesc, String connDesc, DataSourceIdentity dataSourceId, Throwable exception, long startTime) {
        super.doOnCreateConnectionFailed(poolDesc, connDesc, dataSourceId, exception, startTime);
        logCatTransaction(CONNECTION_CREATE_CONNECTION_FAILED, poolDesc, connDesc, dataSourceId, exception, startTime);
    }

    @Override
    public void doOnReleaseConnection(String poolDesc, Connection connection) {
        super.doOnReleaseConnection(poolDesc, connection);
        logCatTransaction(CONNECTION_RELEASE_CONNECTION, poolDesc, connection);
    }

    @Override
    protected void doOnAbandonConnection(String poolDesc, Connection connection) {
        super.doOnAbandonConnection(poolDesc, connection);
        logCatTransaction(CONNECTION_ABANDON_CONNECTION, poolDesc, connection);
    }

    @Override
    public void doOnWaitConnection(String poolDesc, Connection connection, long startTime) {
        super.doOnWaitConnection(poolDesc, connection, startTime);
        String connectionUrl = getConnectionUrl(connection);
        String transactionName = String.format("%s:%s", CONNECTION_WAIT_CONNECTION, connectionUrl);
        String message = String.format("%s,%s", poolDesc, connectionUrl);
        LOGGER.logTransaction(DalLogTypes.DAL_ALERT_POOL_WAIT, transactionName, message, startTime);
    }


    private void logCatTransaction(String typeName, String poolDesc, Connection connection) {
        logCatTransaction(typeName, poolDesc, connection, 0);
    }

    private void logCatTransaction(String typeName, String poolDesc, Connection connection, long startTime) {
        logCatTransaction(typeName, poolDesc, connection, null, startTime);
    }

    private void logCatTransaction(String typeName, String poolDesc, String connDesc, Throwable exception,
            long startTime) {
        logCatTransaction(typeName, poolDesc, connDesc, null, exception, startTime);
    }

    private void logCatTransaction(String typeName, String poolDesc, Connection connection, DataSourceIdentity dataSourceId, long startTime) {
        String connectionUrl = getConnectionUrl(connection);
        String transactionName = String.format("%s:%s", typeName, connectionUrl);
        String message = String.format("%s,%s", poolDesc, connectionUrl);
        LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, transactionName, message,
                LogUtils.buildPropertiesFromDataSourceId(dataSourceId), startTime);
    }

    private void logCatTransaction(String typeName, String poolDesc, String connDesc, DataSourceIdentity dataSourceId, Throwable exception,
                                   long startTime) {
        String transactionName = String.format("%s:%s", typeName, connDesc);
        String message = String.format("%s,%s", poolDesc, connDesc);
        LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, transactionName, message,
                LogUtils.buildPropertiesFromDataSourceId(dataSourceId), exception, startTime);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
