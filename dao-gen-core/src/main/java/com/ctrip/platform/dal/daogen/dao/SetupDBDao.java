package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.daogen.utils.ResourceUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class SetupDBDao {
    private static final String DATA_BASE = "dao";

    public boolean executeSqlScript(String sqlScript) throws SQLException {
        if (sqlScript == null || sqlScript.length() == 0)
            return false;

        String[] array = sqlScript.split(";"); // toUpperCase().
        DalClient client = DalClientFactory.getClient(DATA_BASE);
        DalHints hints = DalHints.createIfAbsent(null);
        client.batchUpdate(array, hints);
        return true;
    }

    public Set<String> getCatalogTableNames(String catalog) throws Exception {
        Set<String> set = new HashSet<>();
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = DalClientFactory.getDalConfigure().getLocator().getConnection(DATA_BASE);
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            if (databaseMetaData == null)
                return set;
            resultSet = databaseMetaData.getTables(catalog, null, null, null);
            if (resultSet != null) {
                while (resultSet.next()) {
                    String tableName = resultSet.getString("TABLE_NAME");
                    if (tableName != null && tableName.length() > 0) {
                        set.add(tableName.toUpperCase());
                    }
                }
            }
        } finally {
            ResourceUtils.close(resultSet);
            ResourceUtils.close(connection);
        }
        return set;
    }

}
