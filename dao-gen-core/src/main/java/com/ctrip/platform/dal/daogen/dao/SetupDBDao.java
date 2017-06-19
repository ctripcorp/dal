package com.ctrip.platform.dal.daogen.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class SetupDBDao {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public boolean executeSqlScript(String sqlScript) {
        boolean result = false;
        if (sqlScript == null || sqlScript.length() == 0) {
            return result;
        }

        String[] array = sqlScript.split(";"); // toUpperCase().
        jdbcTemplate.batchUpdate(array);
        result = true;

        return result;
    }

    public Set<String> getCatalogTableNames(String catalog) throws Exception {
        Set<String> set = new HashSet<>();
        ResultSet resultSet = null;

        try {
            java.sql.DatabaseMetaData databaseMetaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
            if (databaseMetaData == null) {
                return set;
            }
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
            JdbcUtils.closeResultSet(resultSet);
        }
        return set;
    }
}
