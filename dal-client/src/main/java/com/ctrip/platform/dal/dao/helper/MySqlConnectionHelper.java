package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DataSourceValidatorException;
import com.mysql.jdbc.MySQLConnection;

public class MySqlConnectionHelper {
    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String PING_INTERNAL_EXCEPTION_MESSAGE =
            "An error occurred while invoking pingInternal method of MySqlConnection.";
    private static final String VALIDATION_EXCEPTION_HELP_MESSAGE =
            "Connection validation failed. Refer to http://conf.ctripcorp.com/pages/viewpage.action?pageId=231813020 for more explanation";

    public static boolean isValid(MySQLConnection connection, int timeout) {
        return pingInternal(connection, timeout);
    }

    private static boolean pingInternal(MySQLConnection connection, int timeout) {
        if (connection == null)
            return false;

        try {
            connection.pingInternal(false, timeout);
        } catch (Throwable e) {
            LOGGER.warn(VALIDATION_EXCEPTION_HELP_MESSAGE,
                    new DataSourceValidatorException(PING_INTERNAL_EXCEPTION_MESSAGE, e));
            return false;
        }

        return true;
    }

}
