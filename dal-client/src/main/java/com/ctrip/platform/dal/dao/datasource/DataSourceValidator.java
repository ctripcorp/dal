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
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataSourceValidator implements Validator {
    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final int DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS = 5;
    private static final String DAL = "DAL";
    private static final String CONNECTION_VALIDATE_CONNECTION_FORMAT = "Connection::validateConnection:%s";

    private ConnectionMetaDataManager metaDataManager = ConnectionMetaDataManager.getInstance();

    @Override
    public boolean validate(Connection connection, int validateAction) {
        LOGGER.info("Start to validate connection.");
        final AtomicBoolean tempIsValid = new AtomicBoolean(false);
        final Connection tempConnection = connection;
        final int tempValidateAction = validateAction;

        try {
            final ConnectionMetaData metaData = metaDataManager.get(connection);
            String connectionUrl = metaDataManager.getConnectionUrlByMetaData(metaData);
            LOGGER.logTransaction(DAL, String.format(CONNECTION_VALIDATE_CONNECTION_FORMAT, connectionUrl), "",
                    new Callback() {
                        @Override
                        public void execute() throws Exception {
                            String query = null;
                            int validationQueryTimeout = -1;
                            boolean isValid = false;

                            if (tempValidateAction == PooledConnection.VALIDATE_INIT) {
                                PoolProperties poolProperties = getPoolProperties(metaData);
                                if (poolProperties != null) {
                                    query = poolProperties.getInitSQL();
                                    validationQueryTimeout = poolProperties.getValidationQueryTimeout();
                                    if (validationQueryTimeout <= 0) {
                                        validationQueryTimeout = DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS;
                                    }
                                }
                            }

                            if (query == null) {
                                if (tempConnection instanceof MySQLConnection) {
                                    MySQLConnection mySqlConnection = (MySQLConnection) tempConnection;
                                    isValid = MySqlConnectionHelper.isValid(mySqlConnection,
                                            DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS);
                                } else {
                                    isValid = tempConnection.isValid(DEFAULT_VALIDATE_TIMEOUT_IN_SECONDS);
                                }

                                if (!isValid) {
                                    LOGGER.warn("isValid() returned false.");
                                }
                            } else {
                                Statement stmt = null;
                                try {
                                    stmt = tempConnection.createStatement();
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

                            tempIsValid.set(isValid);
                        }
                    });
        } catch (Throwable e) {
            LOGGER.warn(String.format("Connection validation error:%s", e));
        }

        LOGGER.info("Finish validating connection.");
        return tempIsValid.get();
    }

    private PoolProperties getPoolProperties(ConnectionMetaData metaData) {
        if (metaData == null)
            return null;

        return PoolPropertiesHolder.getInstance().getPoolProperties(metaData);
    }

}
