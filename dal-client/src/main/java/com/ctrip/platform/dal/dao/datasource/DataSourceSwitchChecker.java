package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.helper.ConnectionUtils;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by taochen on 2019/9/19.
 */
public class DataSourceSwitchChecker {
    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private static int defaultCheckerTimeout = 1; //second

    private static String SQLSERVER_SQL = "SELECT SERVERPROPERTY('ComputerNamePhysicalNetBIOS') AS ComputerNamePhysicalNetBIOS";

    private static String MYSQL_SQL = "show variables where Variable_name like 'hostname'";

    private static String DATASOURCE_SWITCH_DATASOURCE = "DataSource::switchDataSourceCheck:%s";

    public static String getDBServerName(Connection conn, DataSourceConfigure configure) {
        String executeSql;
        int columnIndex;
        String serverName = null;
        DatabaseCategory databaseCategory = configure.getDatabaseCategory();
        if (databaseCategory.equals(DatabaseCategory.MySql)) {
            executeSql = MYSQL_SQL;
            columnIndex = 2;
        }
        else if (databaseCategory.equals(DatabaseCategory.SqlServer)) {
            executeSql = SQLSERVER_SQL;
            columnIndex = 1;
        }
        else {
            return null;
        }
        Statement stmt = null;
        ResultSet rs = null;
        long startTime = System.currentTimeMillis();
        try {
            stmt = conn.createStatement();
            stmt.setQueryTimeout(defaultCheckerTimeout);
            rs = stmt.executeQuery(executeSql);
            while (rs.next()) {
                serverName = rs.getString(columnIndex);
            }
        } catch (Exception e) {
            LOGGER.error("check db server name failed! ", e);
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception ex) {
                //ignore
            }
        }
        String logName = configure.getName() != null ? configure.getName() : ConnectionUtils.getConnectionUrl(conn);
        LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, String.format(DATASOURCE_SWITCH_DATASOURCE, logName), serverName, startTime);
        return serverName;
    }
}
