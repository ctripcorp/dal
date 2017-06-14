package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.enums.DatabaseType;

public class ConnectionStringUtil {
    private static String sqlServerConnectinoStringPattern = "Data Source=%s,%s;UID=%s;password=%s;database=%s;";
    private static String mySqlConnectionStringPattern = "Server=%s;port=%s;UID=%s;password=%s;database=%s;";

    public static String GetConnectionString(String providerName, String address, String port, String username,
            String password, String database) {
        String connectionString = null;
        if (providerName.equals(DatabaseType.SQLServer.getValue().toLowerCase())) {
            connectionString =
                    String.format(sqlServerConnectinoStringPattern, address, port, username, password, database);
        } else {
            connectionString = String.format(mySqlConnectionStringPattern, address, port, username, password, database);
        }
        return connectionString;
    }
}
