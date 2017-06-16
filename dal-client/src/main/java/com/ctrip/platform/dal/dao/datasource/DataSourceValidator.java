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
            String query = null;
            int validationQueryTimeout = -1;

            if (validateAction == PooledConnection.VALIDATE_INIT) {
                PoolProperties poolProperties = getInitSQL(connection);
                if (poolProperties != null) {
                    query = poolProperties.getInitSQL();
                    validationQueryTimeout = poolProperties.getValidationQueryTimeout();
                    if (validationQueryTimeout <= 0) {
                        validationQueryTimeout = DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS;
                    }
                }
            }

            if (query == null) {
                isValid = connection.isValid(DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS);
                if (!isValid) {
                    LOGGER.warn("isValid() returned false.");
                }
            } else {
                Statement stmt = null;
                try {
                    stmt = connection.createStatement();
                    stmt.setQueryTimeout(validationQueryTimeout);
                    stmt.execute(query);
                    isValid = true;
                } finally {
                    if (stmt != null)
                        try {
                            stmt.close();
                        } catch (Exception ignore2) {
                            /* NOOP */}
                }
            }
        } catch (Throwable ex) {
            LOGGER.warn("Datasource validation error", ex);
        }

        return isValid;
    }

    private PoolProperties getInitSQL(Connection connection) {
        String url = null;
        String userName = null;
        try {
            url = connection.getMetaData().getURL();
            userName = connection.getMetaData().getUserName();
        } catch (Throwable e) {
            LOGGER.warn("Datasource initSQL error", e);
            return null;
        }

        return DataSourceLocator.getPoolProperties(url, userName);
    }

}
