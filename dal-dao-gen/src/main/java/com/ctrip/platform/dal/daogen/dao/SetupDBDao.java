package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class SetupDBDao {
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public boolean executeSqlScript(String sqlScript) {
		boolean result = false;
		if (sqlScript == null || sqlScript.length() == 0) {
			return result;
		}

		try {
			String[] array = sqlScript.toUpperCase().split(";");
			this.jdbcTemplate.batchUpdate(array);
			result = true;
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	public Set<String> getCatalogTableNames(String catalog) {
		Set<String> set = new HashSet<String>();

		try {
			java.sql.DatabaseMetaData databaseMetaData = this.jdbcTemplate
					.getDataSource().getConnection().getMetaData();
			if (databaseMetaData == null) {
				return set;
			}
			ResultSet resultSet = databaseMetaData.getTables(catalog, null,
					null, null);
			if (resultSet != null) {
				while (resultSet.next()) {
					String tableName = resultSet.getString("TABLE_NAME");
					if (tableName != null && tableName.length() > 0) {
						set.add(tableName.toUpperCase());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return set;
	}
}
