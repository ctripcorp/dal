package com.ctrip.platform.dal.dao.helper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * Created by taochen on 2019/10/10.
 */
public class ConnectionUtils {

    public static String getConnectionUrl(Connection connection) {
        return getConnectionUrl(connection, null);
    }

    public static String getConnectionUrl(Connection connection, String poolUrl) {
        if (connection != null) {
            try {
                DatabaseMetaData metaData = connection.getMetaData();
                if (metaData != null) {
                    return LoggerHelper.getSimplifiedDBUrl(metaData.getURL());
                }
            } catch (Throwable t) {
                // ignore
            }
        }
        return LoggerHelper.getSimplifiedDBUrl(poolUrl);
    }

}
