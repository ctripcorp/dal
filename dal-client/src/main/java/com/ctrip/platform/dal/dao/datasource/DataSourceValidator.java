package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DalExtendedPoolConfiguration;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.HostConnectionValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.DefaultHostConnection;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import com.ctrip.platform.dal.dao.helper.ConnectionUtils;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.MySqlConnectionHelper;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.dao.log.LogUtils;
import com.ctrip.platform.dal.exceptions.InvalidConnectionException;
import com.mysql.jdbc.MySQLConnection;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PooledConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataSourceValidator implements ValidatorProxy {
    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String CONNECTION_VALIDATE_CONNECTION_FORMAT = "Connection::validateConnection:%s";
    private static final String IS_VALID_RETURN_INFO = "isValid() returned false.";
    private String IS_VALID_FORMAT = "isValid: %s";
    private String VALIDATE_ERROR_FORMAT = "Connection validation error: %s";

    private PoolProperties poolProperties;
    private HostConnectionValidator clusterConnValidator;

    @Override
    public boolean validate(Connection connection, int validateAction) {
        long startTime = System.currentTimeMillis();
        boolean isValid = false;
        String transactionName = "";

        try {
            PoolConfiguration poolConfig = getPoolProperties();
            DataSourceIdentity dataSourceId = poolConfig instanceof DalExtendedPoolConfiguration ?
                    ((DalExtendedPoolConfiguration) poolConfig).getDataSourceId() : null;

            String connUrl = ConnectionUtils.getConnectionUrl(connection, poolConfig.getUrl());
            transactionName = String.format(CONNECTION_VALIDATE_CONNECTION_FORMAT, connUrl);

            tryValidateClusterConnection(connection, validateAction);
            isValid = validateConnection(connection, validateAction);

            LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, transactionName,
                    String.format(IS_VALID_FORMAT, isValid) + " Connection url: " + poolConfig.getUrl(),
                    LogUtils.buildPropertiesFromDataSourceId(dataSourceId), startTime);

            if (!isValid) {
                LOGGER.warn(IS_VALID_RETURN_INFO);
            }
        } catch (InvalidConnectionException e) {
            handleException(isValid, e, transactionName, startTime);
            throw e;
        } catch (Throwable t) {
            handleException(isValid, t, transactionName, startTime);
        }

        return isValid;
    }

    private void handleException(boolean isValid, Throwable t, String transactionName, long startTime) {
        StringBuilder sb = new StringBuilder();
        if (!isValid) {
            sb.append(IS_VALID_RETURN_INFO);
            sb.append(" ");
        }
        sb.append(String.format(VALIDATE_ERROR_FORMAT, t.getMessage()));
        LOGGER.warn(sb.toString());
        LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, transactionName, sb.toString(), t, startTime);
    }

    private boolean validateConnection(Connection connection, int validateAction) throws SQLException {
        QueryParameter queryParameter = getQueryParameter(validateAction);
        return isValid(connection, queryParameter);
    }

    private void tryValidateClusterConnection(Connection connection, int validateAction) {
        if (clusterConnValidator != null) {
            boolean isValid = true;
            try {
                HostConnection conn;
                if (poolProperties instanceof DalExtendedPoolConfiguration)
                    conn = new DefaultHostConnection(connection,
                            ((DalExtendedPoolConfiguration) poolProperties).getHost());
                else
                    conn = new DefaultHostConnection(connection, null);
                isValid = clusterConnValidator.validate(conn);
            } catch (Throwable t) {
                LOGGER.warn("tryValidateClusterConnection exception", t);
            }
            if (!isValid)
                throw new InvalidConnectionException("Borrowed connection is invalid");
        }
    }

    private QueryParameter getQueryParameter(int validateAction) {
        QueryParameter parameter = new QueryParameter();

        PoolProperties poolProperties = getPoolProperties();
        if (poolProperties == null) {
            parameter.setValidationQueryTimeout(DataSourceConfigureConstants.DEFAULT_VALIDATIONQUERYTIMEOUT);
            return parameter;
        }
        int validationQueryTimeout = poolProperties.getValidationQueryTimeout();
        if (validationQueryTimeout <= 0) {
            validationQueryTimeout = DataSourceConfigureConstants.DEFAULT_VALIDATIONQUERYTIMEOUT;
        }
        parameter.setValidationQueryTimeout(validationQueryTimeout);

        if (validateAction != PooledConnection.VALIDATE_INIT)
            return parameter;

        String query = poolProperties.getInitSQL();
        parameter.setQuery(query);

        return parameter;
    }

    private boolean isValid(Connection connection, QueryParameter parameter) throws SQLException {
        boolean isValid;
        String query = null;
        if (parameter != null) {
            query = parameter.getQuery();
        }

        if (query == null) {
            isValid = connectionIsValid(connection, parameter);
        } else {
            isValid = executeInitSQL(connection, parameter);
        }

        return isValid;
    }

    private boolean connectionIsValid(Connection connection, QueryParameter parameter) throws SQLException {
        boolean isValid;

        if (connection instanceof MySQLConnection) {
            MySQLConnection mySqlConnection = (MySQLConnection) connection;
            isValid = MySqlConnectionHelper.isValid(mySqlConnection, parameter.getValidationQueryTimeout());
        } else {
            isValid = connection.isValid(parameter.getValidationQueryTimeoutS());
        }

        return isValid;
    }

    private boolean executeInitSQL(Connection connection, QueryParameter parameter) throws SQLException {
        boolean isValid = false;
        String query = parameter.getQuery();
        int validationQueryTimeout = parameter.getValidationQueryTimeoutS();

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

    private class QueryParameter {
        private String query = null;
        private int validationQueryTimeout = DataSourceConfigureConstants.DEFAULT_VALIDATIONQUERYTIMEOUT;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public int getValidationQueryTimeout() {
            return Math.max(validationQueryTimeout, DataSourceConfigureConstants.MIN_VALIDATIONQUERYTIMEOUT);
        }

        public int getValidationQueryTimeoutS() {
            return validationQueryTimeout / 1000 + (validationQueryTimeout % 1000 > 0 ? 1 : 0);
        }

        public void setValidationQueryTimeout(int validationQueryTimeout) {
            this.validationQueryTimeout = validationQueryTimeout;
        }
    }

    private PoolProperties getPoolProperties() {
        return poolProperties;
    }

    @Override
    public void setPoolProperties(PoolProperties poolProperties) {
        this.poolProperties = poolProperties;
    }

    @Override
    public void setClusterConnValidator(HostConnectionValidator clusterConnValidator) {
        this.clusterConnValidator = clusterConnValidator;
    }

    public static void setILogger(ILogger logger) {
        DataSourceValidator.LOGGER = logger;
    }

}
