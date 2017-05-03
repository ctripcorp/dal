package com.ctrip.platform.dal.dao.datasource;

import org.apache.tomcat.jdbc.pool.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DataSourceValidator implements Validator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceValidator.class);
    private static final int DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS = 5;

    @Override
    public boolean validate(Connection connection, int validateAction) {
        boolean isValid = false;
        try {
            isValid = connection.isValid(DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS);
        } catch (Throwable ex) {
            LOGGER.warn("Datasource validation error", ex);
        }

        return isValid;
    }
}
