package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.MySqlConnectionHelper;
import com.ctrip.platform.dal.dao.log.Callback;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.mysql.jdbc.MySQLConnection;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.apache.tomcat.jdbc.pool.Validator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataSourceValidator implements Validator {
    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final int DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS = 5;
    private static final String DAL = "DAL";
    private static final String CONNECTION_VALIDATE_CONNECTION_FORMAT = "Connection::validateConnection:%s";
    private static final String START_VALIDATE_CONNECTION = "Start to validate connection.";
    private static final String FINISH_VALIDATE_CONNECTION = "Finish validating connection.";
    private String VALIDATE_ERROR_FORMAT = "Connection validation error:%s";
    private static final String IS_VALID_RETURN_INFO = "isValid() returned false.";

    private ConnectionMetaDataManager metaDataManager = ConnectionMetaDataManager.getInstance();

    @Override
    public boolean validate(Connection connection, int validateAction) {
        LOGGER.info(START_VALIDATE_CONNECTION);
        final AtomicBoolean isValid = new AtomicBoolean(false);
        final Connection _connection = connection;
        final int _validateAction = validateAction;

        try {
            final ConnectionMetaData metaData = metaDataManager.get(connection);
            String connectionUrl = metaDataManager.getConnectionUrlByMetaData(metaData);
            _validate(connectionUrl, _validateAction, metaData, _connection, isValid);
        } catch (Throwable e) {
            LOGGER.warn(String.format(VALIDATE_ERROR_FORMAT, e.getMessage()));
        }

        LOGGER.info(FINISH_VALIDATE_CONNECTION);
        return isValid.get();
    }

    private void _validate(String connectionUrl, final int validateAction, final ConnectionMetaData metaData,
            final Connection connection, final AtomicBoolean atomicIsValid) {
        LOGGER.logTransaction(DAL, String.format(CONNECTION_VALIDATE_CONNECTION_FORMAT, connectionUrl), "",
                new Callback() {
                    @Override
                    public void execute() throws Exception {
                        String query = null;
                        int validationQueryTimeout = -1;
                        QueryParameter queryParameter = getQueryParameter(validateAction, metaData);
                        if (queryParameter != null) {
                            query = queryParameter.getQuery();
                            validationQueryTimeout = queryParameter.getValidationQueryTimeout();
                        }

                        boolean isValid = isValid(query, connection, validationQueryTimeout);
                        atomicIsValid.set(isValid);
                    }
                });
    }

    private QueryParameter getQueryParameter(final int validateAction, final ConnectionMetaData metaData) {
        QueryParameter parameter = new QueryParameter();
        String query = null;
        int validationQueryTimeout = -1;

        if (validateAction == PooledConnection.VALIDATE_INIT) {
            PoolProperties poolProperties = getPoolProperties(metaData);
            if (poolProperties != null) {
                query = poolProperties.getInitSQL();
                validationQueryTimeout = poolProperties.getValidationQueryTimeout();
                if (validationQueryTimeout <= 0) {
                    validationQueryTimeout = DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS;
                }
            }
        }

        parameter.setQuery(query);
        parameter.setValidationQueryTimeout(validationQueryTimeout);
        return parameter;
    }

    private boolean isValid(String query, final Connection connection, int validationQueryTimeout) throws SQLException {
        boolean isValid = false;

        if (query == null) {
            isValid = connectionIsValid(connection);
        } else {
            isValid = executeInitSQL(connection, validationQueryTimeout, query);
        }

        return isValid;
    }

    private boolean connectionIsValid(final Connection connection) throws SQLException {
        boolean isValid = false;

        if (connection instanceof MySQLConnection) {
            MySQLConnection mySqlConnection = (MySQLConnection) connection;
            isValid = MySqlConnectionHelper.isValid(mySqlConnection, DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS);
        } else {
            isValid = connection.isValid(DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS);
        }

        if (!isValid) {
            LOGGER.warn(IS_VALID_RETURN_INFO);
        }

        return isValid;
    }

    private boolean executeInitSQL(final Connection connection, int validationQueryTimeout, String query)
            throws SQLException {
        boolean isValid = false;
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

        return isValid;
    }

    private PoolProperties getPoolProperties(ConnectionMetaData metaData) {
        if (metaData == null)
            return null;

        return PoolPropertiesHolder.getInstance().getPoolProperties(metaData);
    }

    private class QueryParameter {
        private String query = null;
        private int validationQueryTimeout = -1;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public int getValidationQueryTimeout() {
            return validationQueryTimeout;
        }

        public void setValidationQueryTimeout(int validationQueryTimeout) {
            this.validationQueryTimeout = validationQueryTimeout;
        }
    }

    public static void setILogger(ILogger logger) {
        DataSourceValidator.LOGGER = logger;
    }

}
