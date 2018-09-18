package com.ctrip.datasource.datasource.ConnectionListener;

import com.ctrip.datasource.datasource.CtripConnectionListener;
import com.ctrip.datasource.log.ILogger.MockILoggerImpl;
import com.ctrip.platform.dal.dao.datasource.AbstractConnectionListener;
import com.ctrip.platform.dal.dao.datasource.ConnectionListener;
import com.ctrip.platform.dal.dao.datasource.ConnectionMetaDataManager;
import com.ctrip.platform.dal.dao.datasource.CreateConnectionCallback;
import com.ctrip.platform.dal.dao.log.ILogger;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CtripConnectionListenerTest {
    private static ConnectionListener connectionListener = new CtripConnectionListener();
    private static ILogger MOCK_LOGGER = new MockILoggerImpl();

    private static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String CONNECTION_URL =
            "jdbc:mysql://DST56614:3306/dao_test?useUnicode=true&characterEncoding=UTF-8";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "!QAZ@WSX1qaz2wsx";

    private static final String POOL_DESC = "Pool desc";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        AbstractConnectionListener.setILogger(MOCK_LOGGER);
    }

    @Test
    public void testCreateConnection() throws SQLException {
        connectionListener.onCreateConnection(POOL_DESC, new CreateConnectionCallback() {
            @Override
            public Connection createConnection() throws Exception {
                return getConnection();
            }
        });
    }

    @Test
    public void testReleaseConnection() throws SQLException {
        Connection connection = getConnection();
        ConnectionMetaDataManager.getInstance().put(connection);
        connectionListener.onReleaseConnection(POOL_DESC, connection);
    }

    @Test
    public void testAbandomConnection() throws SQLException {
        Connection connection = getConnection();
        ConnectionMetaDataManager.getInstance().put(connection);
        connectionListener.onAbandonConnection(POOL_DESC, connection);
    }

    private Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(DRIVER_CLASS_NAME);
            connection = DriverManager.getConnection(CONNECTION_URL, USER_NAME, PASSWORD);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }

        return connection;
    }

}
