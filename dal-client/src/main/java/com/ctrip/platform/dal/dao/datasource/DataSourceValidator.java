package com.ctrip.platform.dal.dao.datasource;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.apache.tomcat.jdbc.pool.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;

public class DataSourceValidator implements Validator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceValidator.class);
    private static final int DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS = 5;

    @Override
    public boolean validate(Connection connection, int validateAction) {
        boolean isValid = false;
        try {
            checkInitSQL(connection, validateAction);
            isValid = connection.isValid(DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS);
        } catch (Throwable ex) {
            LOGGER.warn("Datasource validation error", ex);
        }

        return isValid;
    }

    private void checkInitSQL(Connection connection, int validateAction) {
        if (validateAction != PooledConnection.VALIDATE_INIT) {
            return;
        }

        String url = null;
        String userName = null;
        try {

            url = connection.getMetaData().getURL();
            userName = connection.getMetaData().getUserName();
        } catch (Throwable e) {
            LOGGER.warn("Datasource initSQL error", e);
            return;
        }

        PoolProperties poolProperties = DataSourceLocator.getPoolProperties(url, userName);
        if (poolProperties == null)
            return;

        String query = poolProperties.getInitSQL();

        if (query == null) {
            return;
        }

        Statement stmt = null;
        try {
            stmt = connection.createStatement();

            int validationQueryTimeout = poolProperties.getValidationQueryTimeout();
            if (validationQueryTimeout > 0) {
                stmt.setQueryTimeout(validationQueryTimeout);
            }

            stmt.execute(query);
            stmt.close();
        } catch (Throwable ex) {
            if (poolProperties.getLogValidationErrors()) {
                LOGGER.warn("SQL Validation error", ex);
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unable to validate object:", ex);
            }
            if (stmt != null)
                try {
                    stmt.close();
                } catch (Throwable ignore2) {
                    /* NOOP */}
        }
    }

}
