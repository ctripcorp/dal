package com.ctrip.platform.dal.dao.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DefaultConnectionListener extends AbstractConnectionListener implements ConnectionListener{

    private static Logger logger = LoggerFactory.getLogger(DefaultConnectionListener.class);

    @Override
    public void doOnCreateConnection(String poolDesc, Connection connection) {
        logger.info("[onCreateConnection]{}, {}", poolDesc, connection);
    }

    @Override
    public void doOnReleaseConnection(String poolDesc, Connection connection) {
        logger.info("[onReleaseConnection]{}, {}", poolDesc, connection);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
