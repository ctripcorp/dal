package com.ctrip.platform.dal.dao.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DefaultConnectionListener extends AbstractConnectionListener implements ConnectionListener {

    private static Logger logger = LoggerFactory.getLogger(DefaultConnectionListener.class);

    @Override
    public void doOnCreateConnection(String poolDesc, Connection connection) {
        logInfo("[onCreateConnection]{}, {}", poolDesc, connection);
    }

    @Override
    public void doOnReleaseConnection(String poolDesc, Connection connection) {
        logInfo("[onReleaseConnection]{}, {}", poolDesc, connection);
    }

    @Override
    protected void doOnAbandonConnection(String poolDesc, Connection connection) {
        logInfo("[onAbandonConnection]{}, {}", poolDesc, connection);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    private void logInfo(String format, String poolDesc, Connection connection) {
        String connDesc = connectionDesc(connection);
        logger.info(format, poolDesc, connDesc);
    }

}
