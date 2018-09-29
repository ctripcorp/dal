package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.datasource.AbstractConnectionListener;
import com.ctrip.platform.dal.dao.datasource.ConnectionListener;

import java.sql.Connection;

public class CtripConnectionListener extends AbstractConnectionListener implements ConnectionListener {
    private static final String DAL = "DAL";
    private static final String CONNECTION_CREATE_CONNECTION = "Connection::createConnection";
    private static final String CONNECTION_CREATE_CONNECTION_FAILED = "Connection::createConnectionFailed";
    private static final String CONNECTION_RELEASE_CONNECTION = "Connection::releaseConnection";
    private static final String CONNECTION_ABANDON_CONNECTION = "Connection::abandonConnection";

    @Override
    public void doOnCreateConnection(String poolDesc, Connection connection, long startTime) {
        super.doOnCreateConnection(poolDesc, connection, startTime);
        logCatTransaction(CONNECTION_CREATE_CONNECTION, poolDesc, connection, startTime);
    }

    @Override
    public void doOnCreateConnectionFailed(String poolDesc, String connDesc, Throwable exception, long startTime) {
        super.doOnCreateConnectionFailed(poolDesc, connDesc, exception, startTime);
        logCatTransaction(CONNECTION_CREATE_CONNECTION_FAILED, poolDesc, connDesc, exception, startTime);
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

    private void logCatTransaction(String typeName, String poolDesc, Connection connection) {
        logCatTransaction(typeName, poolDesc, connection, 0);
    }

    private void logCatTransaction(String typeName, String poolDesc, Connection connection, long startTime) {
        String connectionUrl = getConnectionUrl(connection);
        String transactionName = String.format("%s:%s", typeName, connectionUrl);
        String message = String.format("%s,%s", poolDesc, connectionUrl);

        LOGGER.logTransaction(DAL, transactionName, message, startTime);
    }

    private void logCatTransaction(String typeName, String poolDesc, String connDesc, Throwable exception,
            long startTime) {
        String transactionName = String.format("%s:%s", typeName, connDesc);
        String message = String.format("%s,%s", poolDesc, connDesc);
        LOGGER.logTransaction(DAL, transactionName, message, exception, startTime);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
