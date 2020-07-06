package com.ctrip.datasource.datasource;

import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.MySQLConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by taochen on 2019/12/16.
 */
public class PingConnection extends ConnectionImpl {
    public int timeout = 0;

    private Connection connection = null;

    public PingConnection (Connection connection) {
        this.connection = connection;
    }

    @Override
    public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException {
        timeout = timeoutMillis;
        ((MySQLConnection) connection).pingInternal(false, timeoutMillis);
    }

    @Override
    public java.sql.DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }
}
