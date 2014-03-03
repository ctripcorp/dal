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
import com.ctrip.platform.dal.daogen.dao.DaoOfDbServer;
import com.ctrip.platform.dal.daogen.java.JavaParameterHost;
import com.ctrip.platform.dal.daogen.pojo.CurrentLanguage;
import com.ctrip.platform.dal.daogen.pojo.DbServer;
import com.ctrip.platform.dal.daogen.pojo.StoredProcedure;

public class DbUtils {
	private static DaoOfDbServer dbServerDao;
	private static List<Integer> validMode;

	static {
		dbServerDao = SpringBeanGetter.getDaoOfDbServer();
		validMode = new ArrayList<Integer>();
		validMode.add(DatabaseMetaData.procedureColumnIn);
		validMode.add(DatabaseMetaData.procedureColumnInOut);
		validMode.add(DatabaseMetaData.procedureColumnOut);
	}

	/**
	 * 获取所有表名
	 * 
	 * @param server
	 * @param dbName
	 * @return
	 */
	public static List<String> getAllTableNames(int server, String dbName) {
		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);

		DbServer dbServer = dbServerDao.getDbServerByID(server);

		if (ds == null) {
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		List<String> results = new ArrayList<String>();

		// 如果是Sql Server，通过Sql语句获取所有表名称
		if (dbServer != null
				&& dbServer.getDb_type().equalsIgnoreCase("sqlserver")) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

			String sql = String
					.format("use %s select Name from sysobjects where xtype  = 'u' and status>=0",
							dbName);
			results = jdbcTemplate.query(sql, new RowMapper<String>() {
				public String mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					return rs.getString(1);
				}
			});
		}
		// 如果是MySql，通过JDBC标准方式获取所有表的名称
		else if (dbServer != null
				&& dbServer.getDb_type().equalsIgnoreCase("mysql")) {
			String[] types = { "TABLE" };
			ResultSet rs = null;
			Connection connection = null;
			try {
				connection = ds.getConnection();
				rs = connection.getMetaData().getTables(dbName, null, "%",
						types);
				while (rs.next()) {
					results.add(rs.getString("TABLE_NAME"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtils.closeResultSet(rs);
				JdbcUtils.closeConnection(connection);
			}
		}

		return results;
	}

	/**
	 * 获取所有视图
	 * 
	 * @param server
	 * @param dbName
	 * @return
	 */
	public static List<String> getAllViewNames(int server, String dbName) {
		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);

		DbServer dbServer = dbServerDao.getDbServerByID(server);

		if (ds == null) {
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		List<String> results = new ArrayList<String>();

		// 如果是Sql Server，通过Sql语句获取所有视图的名称
		if (dbServer != null
				&& dbServer.getDb_type().equalsIgnoreCase("sqlserver")) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

			String sql = String
					.format("use %s select Name from sysobjects where xtype ='v' and status>=0",
							dbName);
			results = jdbcTemplate.query(sql, new RowMapper<String>() {
				public String mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					return rs.getString(1);
				}
			});
		}
		// 如果是MySql，通过JDBC标准方式获取所有表和视图的名称
		else if (dbServer != null
				&& dbServer.getDb_type().equalsIgnoreCase("mysql")) {
			String[] types = { "VIEW" };
			ResultSet rs = null;
			Connection connection = null;
			try {
				connection = ds.getConnection();
				rs = connection.getMetaData().getTables(dbName, null, "%",
						types);
				while (rs.next()) {
					results.add(rs.getString("TABLE_NAME"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtils.closeResultSet(rs);
				JdbcUtils.closeConnection(connection);
			}
		}

		return results;
	}

	/**
	 * 
	 * @param server
	 * @param dbName
	 * @return
	 */
	public static List<StoredProcedure> getAllSpNames(int server, String dbName) {

		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);

		DbServer dbServer = dbServerDao.getDbServerByID(server);

		if (ds == null) {
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		List<StoredProcedure> results = new ArrayList<StoredProcedure>();

		// 如果是Sql Server，通过Sql语句获取所有表和视图的名称
		if (dbServer != null
				&& dbServer.getDb_type().equalsIgnoreCase("sqlserver")) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

			String sql = String
					.format("use %s select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE'",
							dbName);
			results = jdbcTemplate.query(sql, new RowMapper<StoredProcedure>() {
				public StoredProcedure mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					StoredProcedure sp = new StoredProcedure();
					sp.setSchema(rs.getString(1));
					sp.setName(rs.getString(2));
					return sp;
				}
			});
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
	public static List<AbstractParameterHost> getSpParams(int server,
			String dbName, StoredProcedure sp, CurrentLanguage language) {

		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);

		if (ds == null) {
			DbServer dbServer = dbServerDao.getDbServerByID(server);
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		Connection connection = null;
		List<AbstractParameterHost> parameters = new ArrayList<AbstractParameterHost>();
		try {
			connection = ds.getConnection();

			ResultSet spParams = connection.getMetaData().getProcedureColumns(
					dbName, sp.getSchema(), sp.getName(), null);

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

					parameters.add(host);
				}
			} else // TODO replace with CurrentLanguage
			if (language == CurrentLanguage.Java) {
				while (spParams.next()) {
					int paramMode = spParams.getShort("COLUMN_TYPE");

					if (!validMode.contains(paramMode)) {
						continue;
					}

					JavaParameterHost host = new JavaParameterHost();
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

		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			JdbcUtils.closeConnection(connection);
		}

		return parameters;

	}

	/**
	 * 由调用者负责Connection的生命周期！！！！
	 * 
	 * @param connection
	 * @return
	 */
	public static List<String> getPrimaryKeyNames(int server, String dbName,
			String tableName) {

		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);

		if (ds == null) {
			DbServer dbServer = dbServerDao.getDbServerByID(server);
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		Connection connection = null;

		// 获取所有主键
		ResultSet primaryKeyRs = null;
		List<String> primaryKeys = new ArrayList<String>();
		try {
			connection = ds.getConnection();
			primaryKeyRs = connection.getMetaData().getPrimaryKeys(dbName,
					null, tableName);

			while (primaryKeyRs.next()) {
				primaryKeys.add(primaryKeyRs.getString("COLUMN_NAME"));
			}
		} catch (SQLException e) {
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
	public static List<AbstractParameterHost> getAllColumnNames(int server,
			String dbName, String tableName, CurrentLanguage language) {
		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);

		if (ds == null) {
			DbServer dbServer = dbServerDao.getDbServerByID(server);
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		Connection connection = null;

		// 获取所有列
		ResultSet allColumnsRs = null;
		List<AbstractParameterHost> allColumns = new ArrayList<AbstractParameterHost>();
		try {
			connection = ds.getConnection();
			allColumnsRs = connection.getMetaData().getColumns(dbName, null,
					tableName, null);

			if (language == CurrentLanguage.CSharp) {
				while (allColumnsRs.next()) {
					CSharpParameterHost host = new CSharpParameterHost();
					DbType dbType = DbType.getDbTypeFromJdbcType(allColumnsRs
							.getInt("DATA_TYPE"));
					host.setDbType(dbType);
					host.setName(allColumnsRs.getString("COLUMN_NAME"));
					host.setType(DbType.getCSharpType(host.getDbType()));
					host.setIdentity(allColumnsRs.getString("IS_AUTOINCREMENT")
							.equalsIgnoreCase("YES"));
					host.setNullable(allColumnsRs.getShort("NULLABLE") == DatabaseMetaData.columnNullable);
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
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(allColumnsRs);
			JdbcUtils.closeConnection(connection);
		}

		return allColumns;
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
	public static ResultSetMetaData testAQuerySql(int server, String dbName,
			String sql, String params) {
		String[] parameters = params.split(",");

		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);

		if (ds == null) {
			DbServer dbServer = dbServerDao.getDbServerByID(server);
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		Connection connection = null;
		ResultSet rs = null;
		try {

			String replacedSql = sql.replaceAll("[@:]\\w+", "?");

			connection = ds.getConnection();

			connection.setCatalog(dbName);

			PreparedStatement ps = connection.prepareStatement(replacedSql);

			int index = 0;
			for (String param : parameters) {
				if (param != null && !param.isEmpty()) {
					String[] tuple = param.split("_");

					try {
						index = Integer.valueOf(tuple[0]);
					} catch (NumberFormatException ex) {
						index++;
					}
					ps.setObject(index, tuple[2], Integer.valueOf(tuple[1]));
				}
			}

			rs = ps.executeQuery();

			return rs.getMetaData();

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

}
