package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;

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
        client.batchUpdate(array, null);
        return true;
    }

    public Set<String> getCatalogTableNames(String catalog) throws Exception {
        Set<String> set = new HashSet<>();
        ResultSet resultSet = null;

        try {
            DatabaseMetaData databaseMetaData =
                    DalClientFactory.getDalConfigure().getLocator().getConnection(DATA_BASE).getMetaData();

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
            if (resultSet != null)
                resultSet.close();
        }
        return set;
    }

}
