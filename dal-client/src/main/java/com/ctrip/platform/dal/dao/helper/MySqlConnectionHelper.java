package com.ctrip.platform.dal.dao.helper;

import com.mysql.jdbc.MySQLConnection;

public class MySqlConnectionHelper {

    public static boolean isValid(MySQLConnection connection, int timeout) {
        return pingInternal(connection, timeout);
    }

    private static boolean pingInternal(MySQLConnection connection, int timeout) {
        if (connection == null)
            return false;

        try {
            connection.pingInternal(false, timeout * 1000);
        } catch (Throwable t) {
            return false;
        }

        return true;
    }

}
