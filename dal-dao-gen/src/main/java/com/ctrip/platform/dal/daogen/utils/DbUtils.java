package com.ctrip.platform.dal.daogen.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.cs.CSharpParameterHost;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.java.JavaParameterHost;
import com.ctrip.platform.dal.datasource.LocalDataSourceLocator;

public class DbUtils {
	private static List<Integer> validMode;

	static {
		validMode = new ArrayList<Integer>();
		validMode.add(DatabaseMetaData.procedureColumnIn);
		validMode.add(DatabaseMetaData.procedureColumnInOut);
		validMode.add(DatabaseMetaData.procedureColumnOut);
	}

	public static boolean tableExists(String dbName, String tableName) {

		boolean result = false;
		ResultSet rs = null;
		Connection connection = null;
		try {
			DataSource ds = LocalDataSourceLocator.newInstance().getDataSource(
					dbName);
			connection = ds.getConnection();

			String dbType = null;
			if (Consts.databaseType.containsKey(dbName)) {
				dbType = Consts.databaseType.get(dbName);
			} else {
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);
			}

			if (dbType.equals("Microsoft SQL Server")) {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

				String sql = String
						.format("select Name from sysobjects where xtype  = 'u' and status>=0 and Name=?",
								dbName);
				result = jdbcTemplate.query(sql, new Object[] { tableName },
						new RowMapper<String>() {
							public String mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return rs.getString(1);
							}
						}).size() > 0;
			} else {
				String[] types = { "TABLE" };
				rs = connection.getMetaData().getTables(null, null,
						tableName, types);
				result = rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeConnection(connection);
		}

		return result;
	}

	/**
	 * 获取所有表名
	 * 
	 * @param server
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public static List<String> getAllTableNames(String dbName) throws Exception {
		DataSource ds = LocalDataSourceLocator.newInstance().getDataSource(dbName);

		List<String> results = new ArrayList<String>();
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = ds.getConnection();
			String dbType = null;
			if (Consts.databaseType.containsKey(dbName)) {
				dbType = Consts.databaseType.get(dbName);
			} else {
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);
			}

			// 如果是Sql Server，通过Sql语句获取所有视图的名称
			if (dbType.equals("Microsoft SQL Server")) {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

				String sql = String
						.format("select Name from sysobjects where xtype ='u' and status>=0",
								dbName);
				results = jdbcTemplate.query(sql, new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getString(1);
					}
				});
			} else {
				String[] types = { "TABLE" };

				rs = connection.getMetaData().getTables(null, null, "%",
						types);
				while (rs.next()) {
					results.add(rs.getString("TABLE_NAME"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeConnection(connection);
		}

		return results;
	}

	public static boolean viewExists(String dbName, String viewName) {
		boolean result = false;
		Connection connection = null;
		ResultSet rs = null;
		try {
			DataSource ds = LocalDataSourceLocator.newInstance().getDataSource(
					dbName);
			connection = ds.getConnection();
			String dbType = null;
			if (Consts.databaseType.containsKey(dbName)) {
				dbType = Consts.databaseType.get(dbName);
			} else {
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);
			}

			if (dbType.equals("Microsoft SQL Server")) {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

				String sql = String
						.format(" select Name from sysobjects where xtype ='v' and status>=0 and Name = ?",
								dbName);
				result = jdbcTemplate.query(sql, new Object[] { viewName },
						new RowMapper<String>() {
							public String mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return rs.getString(1);
							}
						}).size() > 0;
			} else {
				String[] types = { "VIEW" };
				rs = connection.getMetaData().getTables(null, null, viewName,
						types);
				result = rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeConnection(connection);
		}

		return result;
	}

	/**
	 * 获取所有视图
	 * 
	 * @param server
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public static List<String> getAllViewNames(String dbName) throws Exception {
		DataSource ds = LocalDataSourceLocator.newInstance().getDataSource(dbName);

		List<String> results = new ArrayList<String>();
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = ds.getConnection();
			String dbType = null;
			if (Consts.databaseType.containsKey(dbName)) {
				dbType = Consts.databaseType.get(dbName);
			} else {
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);
			}

			// 如果是Sql Server，通过Sql语句获取所有视图的名称
			if (dbType.equals("Microsoft SQL Server")) {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

				String sql = String
						.format("select Name from sysobjects where xtype ='v' and status>=0",
								dbName);
				results = jdbcTemplate.query(sql, new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getString(1);
					}
				});
			} else {
				String[] types = { "VIEW" };

				rs = connection.getMetaData().getTables(null, null, "%",
						types);
				while (rs.next()) {
					results.add(rs.getString("TABLE_NAME"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeConnection(connection);
		}

		return results;
	}

	public static boolean spExists(String dbName, final StoredProcedure sp) {

		boolean result = false;
		Connection connection = null;
		try {
			DataSource ds = LocalDataSourceLocator.newInstance().getDataSource(
					dbName);

			connection = ds.getConnection();
			String dbType = null;
			if (Consts.databaseType.containsKey(dbName)) {
				dbType = Consts.databaseType.get(dbName);
			} else {
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);
			}
			// 如果是Sql Server，通过Sql语句获取所有表和视图的名称
			if (dbType.equals("Microsoft SQL Server")) {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

				String sql = "select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE' and SPECIFIC_SCHEMA=? and SPECIFIC_NAME=?";
				result = jdbcTemplate.query(sql,
						new Object[] { sp.getSchema(), sp.getName() },
						new RowMapper<StoredProcedure>() {
							public StoredProcedure mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								StoredProcedure sp = new StoredProcedure();
								sp.setSchema(rs.getString(1));
								sp.setName(rs.getString(2));
								return sp;
							}
						}).size() > 0;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			JdbcUtils.closeConnection(connection);
		}

		return result;
	}

	/**
	 * 
	 * @param server
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public static List<StoredProcedure> getAllSpNames(String dbName)
			throws Exception {

		DataSource ds = LocalDataSourceLocator.newInstance().getDataSource(dbName);

		List<StoredProcedure> results = new ArrayList<StoredProcedure>();
		Connection connection = null;
		try {
			connection = ds.getConnection();
			String dbType = null;
			if (Consts.databaseType.containsKey(dbName)) {
				dbType = Consts.databaseType.get(dbName);
			} else {
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);
			}

			// 如果是Sql Server，通过Sql语句获取所有视图的名称
			if (dbType.equals("Microsoft SQL Server")) {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

				String sql = String
						.format("select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE'",
								dbName);
				results = jdbcTemplate.query(sql,
						new RowMapper<StoredProcedure>() {
							public StoredProcedure mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								StoredProcedure sp = new StoredProcedure();
								sp.setSchema(rs.getString(1));
								sp.setName(rs.getString(2));
								return sp;
							}
						});
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeConnection(connection);
		}
		return results;
	}

	/**
	 * 获取存储过程的所有参数
	 * 
	 * @param server
	 * @param dbName
	 * @param sp
	 * @return
	 */
	public static List<AbstractParameterHost> getSpParams(String dbName,
			StoredProcedure sp, CurrentLanguage language) {

		Connection connection = null;
		List<AbstractParameterHost> parameters = new ArrayList<AbstractParameterHost>();
		try {
			DataSource ds = LocalDataSourceLocator.newInstance().getDataSource(
					dbName);
			connection = ds.getConnection();

			ResultSet spParams = connection.getMetaData().getProcedureColumns(
					null, sp.getSchema(), sp.getName(), null);

			if (language == CurrentLanguage.CSharp) {
				while (spParams.next()) {
					int paramMode = spParams.getShort("COLUMN_TYPE");

					if (!validMode.contains(paramMode)) {
						continue;
					}

					CSharpParameterHost host = new CSharpParameterHost();
					DbType dbType = DbType.getDbTypeFromJdbcType(spParams
							.getInt("DATA_TYPE"));
					host.setDbType(dbType);

					if (paramMode == DatabaseMetaData.procedureColumnIn) {
						host.setDirection(ParameterDirection.Input);
					} else if (paramMode == DatabaseMetaData.procedureColumnInOut) {
						host.setDirection(ParameterDirection.InputOutput);
					} else {
						host.setDirection(ParameterDirection.Output);
					}

					host.setName(spParams.getString("COLUMN_NAME"));
					host.setType(DbType.getCSharpType(host.getDbType()));

					if (host.getType() == null) {
						host.setType("string");
						host.setDbType(DbType.AnsiString);
					}

					parameters.add(host);
				}
			} else // TODO replace with CurrentLanguage
			if (language == CurrentLanguage.Java) {
				while (spParams.next()) {
					int paramMode = spParams.getShort("COLUMN_TYPE");
					// for (int i = 1; i<=
					// spParams.getMetaData().getColumnCount(); i++) {
					// System.out.println(spParams.getMetaData().getColumnName(i));
					// }
					// For My Sql, there is no ORDINAL_POSITION
					// int paramIndex = spParams.getInt("ORDINAL_POSITION");
					if (!validMode.contains(paramMode)) {
						continue;
					}

					JavaParameterHost host = new JavaParameterHost();
					// host.setIndex(paramIndex);
					host.setSqlType(spParams.getInt("DATA_TYPE"));

					if (paramMode == DatabaseMetaData.procedureColumnIn) {
						host.setDirection(ParameterDirection.Input);
					} else if (paramMode == DatabaseMetaData.procedureColumnInOut) {
						host.setDirection(ParameterDirection.InputOutput);
					} else {
						host.setDirection(ParameterDirection.Output);
					}

					host.setName(spParams.getString("COLUMN_NAME"));
					host.setJavaClass(Consts.jdbcSqlTypeToJavaClass.get(host
							.getSqlType()));

					parameters.add(host);
				}
			}
			return parameters;
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeConnection(connection);
		}

		return null;

	}

	/**
	 * 由调用者负责Connection的生命周期！！！！
	 * 
	 * @param connection
	 * @return
	 */
	public static List<String> getPrimaryKeyNames(String dbName,
			String tableName) {

		Connection connection = null;
		// 获取所有主键
		ResultSet primaryKeyRs = null;
		List<String> primaryKeys = new ArrayList<String>();
		try {
			DataSource ds = LocalDataSourceLocator.newInstance().getDataSource(
					dbName);
			connection = ds.getConnection();
			primaryKeyRs = connection.getMetaData().getPrimaryKeys(null,
					null, tableName);

			while (primaryKeyRs.next()) {
				primaryKeys.add(primaryKeyRs.getString("COLUMN_NAME"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(primaryKeyRs);
			JdbcUtils.closeConnection(connection);
		}

		return primaryKeys;
	}

	/**
	 * 由调用者负责Connection的生命周期！！！！
	 * 
	 * @param connection
	 * @return
	 */
	public static List<AbstractParameterHost> getAllColumnNames(String dbName,
			String tableName, CurrentLanguage language) {
		Connection connection = null;
		// 获取所有列
		ResultSet allColumnsRs = null;
		List<AbstractParameterHost> allColumns = new ArrayList<AbstractParameterHost>();
		try {
			DataSource ds = LocalDataSourceLocator.newInstance().getDataSource(
					dbName);
			connection = ds.getConnection();

			// 首先检查表是否存在

			allColumnsRs = connection.getMetaData().getColumns(null, null,
					tableName, null);

			if (language == CurrentLanguage.CSharp) {
				while (allColumnsRs.next()) {
					CSharpParameterHost host = new CSharpParameterHost();
					int dataType = allColumnsRs.getInt("DATA_TYPE");

					DbType dbType = DbType.getDbTypeFromJdbcType(dataType);

					host.setDbType(dbType);
					host.setName(CommonUtils.normalizeVariable(allColumnsRs.getString("COLUMN_NAME")));
					host.setType(DbType.getCSharpType(host.getDbType()));
					host.setIdentity(allColumnsRs.getString("IS_AUTOINCREMENT")
							.equalsIgnoreCase("YES"));
					host.setNullable(allColumnsRs.getShort("NULLABLE") == DatabaseMetaData.columnNullable);
					host.setValueType(Consts.CSharpValueTypes.contains(host
							.getType()));
					// 仅获取String类型的长度
					// if (host.getType().equalsIgnoreCase("string"))
					host.setLength(allColumnsRs.getInt("COLUMN_SIZE"));

					// COLUMN_SIZE

					allColumns.add(host);
				}
			} else if (language == CurrentLanguage.Java) {
				while (allColumnsRs.next()) {
					JavaParameterHost host = new JavaParameterHost();

					host.setSqlType(allColumnsRs.getInt("DATA_TYPE"));
					host.setName(allColumnsRs.getString("COLUMN_NAME"));
					host.setJavaClass(Consts.jdbcSqlTypeToJavaClass.get(host
							.getSqlType()));
					host.setIndex(allColumnsRs.getInt("ORDINAL_POSITION"));
					host.setIdentity(allColumnsRs.getString("IS_AUTOINCREMENT")
							.equalsIgnoreCase("YES"));

					allColumns.add(host);
				}
			}

			return allColumns;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(allColumnsRs);
			JdbcUtils.closeConnection(connection);
		}

		return null;
	}

	/**
	 * 测试查询SQL是否合法
	 * 
	 * @param server
	 * @param dbName
	 * @param sql
	 * @param params
	 * @return
	 */
	public static List<AbstractParameterHost> testAQuerySql(String dbName, String sql,
			String params,CurrentLanguage language , boolean justTest) {
		String[] parameters = params.split(";");

		Connection connection = null;
		ResultSet rs = null;
		try {
			DataSource ds = LocalDataSourceLocator.newInstance().getDataSource(
					dbName);

			String replacedSql = sql.replaceAll("[@:]\\w+", "?");

			connection = ds.getConnection();

			// connection.setCatalog(dbName);

			PreparedStatement ps = connection.prepareStatement(replacedSql);

			int index = 0;
			for (String param : parameters) {
				if (param != null && !param.isEmpty()) {
					String[] tuple = param.split(",");

					try {
						index = Integer.valueOf(tuple[0]);
					} catch (NumberFormatException ex) {
						index++;
					}
					ps.setObject(index, mockATest(Integer.valueOf(tuple[1])),
							Integer.valueOf(tuple[1]));
				}
			}

			rs = ps.executeQuery();
			
			if(justTest){
				return new ArrayList<AbstractParameterHost>();
			}

			ResultSetMetaData rsMeta = rs.getMetaData();
			if(language == CurrentLanguage.CSharp){
				List<AbstractParameterHost> pHosts = new ArrayList<AbstractParameterHost>();
				for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
					CSharpParameterHost pHost = new CSharpParameterHost();
					pHost.setName(rsMeta.getColumnName(i));
					pHost.setDbType(DbType.getDbTypeFromJdbcType(rsMeta
							.getColumnType(i)));
					pHost.setType(DbType.getCSharpType(pHost.getDbType()));
					pHost.setIdentity(false);
					pHost.setNullable(false);
					pHost.setPrimary(false);
					pHost.setLength(rsMeta.getColumnDisplaySize(i));
					pHosts.add(pHost);
				}
				
				return pHosts;

			}else{
				List<AbstractParameterHost> paramHosts = new ArrayList<AbstractParameterHost>();
				for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
					JavaParameterHost paramHost = new JavaParameterHost();
					paramHost.setName(rsMeta.getColumnName(i));
					paramHost.setSqlType(rsMeta.getColumnType(i));
					paramHost.setJavaClass(Consts.jdbcSqlTypeToJavaClass.get(paramHost.getSqlType()));
					paramHost.setIdentity(false);
					paramHost.setNullable(false);
					paramHost.setPrimary(false);
					paramHost.setLength(rsMeta.getColumnDisplaySize(i));
					paramHosts.add(paramHost);
				}
				
				return paramHosts;
			}
			
			
			//return rs.getMetaData();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeConnection(connection);
		}
		
		return null;

	}

	public static Object mockATest(int javaSqlTypes) {
		switch (javaSqlTypes) {
		case java.sql.Types.BIT:
			return true;
		case java.sql.Types.TINYINT:
		case java.sql.Types.SMALLINT:
		case java.sql.Types.INTEGER:
		case java.sql.Types.BIGINT:
			return 1;
		case java.sql.Types.REAL:
		case java.sql.Types.DOUBLE:
		case java.sql.Types.DECIMAL:
			return 1.0;
		case java.sql.Types.CHAR:
			return 't';
		case java.sql.Types.DATE:
			return "2012-01-01";
		case java.sql.Types.TIME:
			return "10:00:00";
		case java.sql.Types.TIMESTAMP:
			return "2012-01-01 10:00:00";
		default:
			return "test";
		}
	}

	public static String getDbType(String dbName) {

		String dbType = null;
		if (Consts.databaseType.containsKey(dbName)) {
			dbType = Consts.databaseType.get(dbName);
		} else {
			Connection connection = null;
			try {
				DataSource ds = LocalDataSourceLocator.newInstance().getDataSource(
						dbName);

				connection = ds.getConnection();

				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				JdbcUtils.closeConnection(connection);
			}
		}
		return dbType;
	}
}
