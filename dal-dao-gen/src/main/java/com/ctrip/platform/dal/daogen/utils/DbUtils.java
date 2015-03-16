package com.ctrip.platform.dal.daogen.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.jdbc.support.JdbcUtils;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.mysql.jdbc.StringUtils;

public class DbUtils {
	
	private static Logger log = Logger.getLogger(DbUtils.class);
	public static List<Integer> validMode = new ArrayList<Integer>();
	private static Pattern inRegxPattern = Pattern.compile("in\\s(@\\w+)", java.util.regex.Pattern.CASE_INSENSITIVE);

	static {
		validMode.add(DatabaseMetaData.procedureColumnIn);
		validMode.add(DatabaseMetaData.procedureColumnInOut);
		validMode.add(DatabaseMetaData.procedureColumnOut);
	}

	public static boolean tableExists(String allInOneName, String tableName) {
		try {
			return objectExist(allInOneName, "u", tableName);
		} catch(Exception e) {
			log.error(String.format("get table exists error: [allInOneName=%s;tableName=%s]", allInOneName, tableName), e);
		}
		return false;
	}
	
	private static boolean objectExist(String allInOneName, String objectType, String objectName) throws Exception {
		String dbType = getDbType(allInOneName);
		if (dbType.equals("Microsoft SQL Server")) {
			return mssqlObjectExist(allInOneName, objectType, objectName);
		} else {
			return mysqlObjectExist(allInOneName, objectType, objectName);
		}
	}
	
	private static boolean mssqlObjectExist(String allInOneName, String objectType, String objectName) throws Exception {
		String sql = "select Name from sysobjects where xtype = ? and status>=0 and Name=?";
		return query(allInOneName, sql, new Object[]{objectType, objectName}, new ResultSetExtractor<Boolean>(){
			@Override
			public Boolean extract(ResultSet rs) throws SQLException {
				return rs.next();
			}
		});
	}
	
	private static boolean mysqlObjectExist(String allInOneName, String objectType, String objectName) {
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = DataSourceUtil.getConnection(allInOneName);
			objectType = "u".equalsIgnoreCase(objectType) ? "TABLE" : "VIEW";
			rs = connection.getMetaData().getTables(null, null, objectName, new String[]{objectType});
			return rs.next();
		} catch(Exception ex) {
			log.warn(ex.getMessage(), ex);
		} finally {
			releaseResource(rs, connection);
		}
		return false;
	}
	
	private static <T> T query(String allInOneName, String sql, Object []params, ResultSetExtractor<T> extractor) throws Exception {
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = DataSourceUtil.getConnection(allInOneName);
			PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			if (params != null && params.length > 0) {
				for (int i=0;i<params.length;i++)
					statement.setObject(i+1, params[i]);
			}
			rs = statement.executeQuery();
			return extractor.extract(rs);
		} catch (Exception ex) {
			handleException(null, ex);
		} finally {
			releaseResource(rs, connection);
		}
		return null;
	}
	
	/**
	 * 获取所有表名
	 */
	public static List<String> getAllTableNames(String allInOneName) throws Exception {
		List<String> results = new ArrayList<String>();
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = DataSourceUtil.getConnection(allInOneName);
			rs = connection.getMetaData().getTables(null, "dbo", "%", new String[]{"TABLE"});
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				if ("sysdiagrams".equals(tableName.toLowerCase()))
					continue;
				results.add(rs.getString("TABLE_NAME"));
			}
		} catch(Exception e) {
			handleException(null, e);
		} finally {
			releaseResource(rs, connection);
		}
		return results;
	}

	public static boolean viewExists(String allInOneName, String viewName) {
		try {
			return objectExist(allInOneName, "v", viewName);
		} catch(Exception e) {
			log.error(String.format("get view exists error: [allInOneName=%s;viewName=%s]", allInOneName, viewName), e);
		}
		return false;
	}
	
	/**
	 * 获取所有视图
	 */
	public static List<String> getAllViewNames(String allInOneName) throws Exception {
		List<String> results = new ArrayList<String>();
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = DataSourceUtil.getConnection(allInOneName);
			rs = connection.getMetaData().getTables(null, "dbo", "%", new String[]{"VIEW"});
			while (rs.next())
				results.add(rs.getString("TABLE_NAME"));
		} catch(Exception e) {
			handleException(null, e);
		} finally {
			releaseResource(rs, connection);
		}
		return results;
	}

	public static boolean spExists(String allInOneName, final StoredProcedure sp) {
		try {
			// 如果是Sql Server，通过Sql语句获取所有表和视图的名称
			if ( !isMySqlServer(allInOneName) ) {
				String sql = "select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE' and SPECIFIC_SCHEMA=? and SPECIFIC_NAME=?";
				return query(allInOneName, sql, new Object[]{sp.getSchema(), sp.getName()}, new ResultSetExtractor<Boolean>() {
					@Override
					public Boolean extract(ResultSet rs) throws SQLException {
						return rs.next();
					}
				});
			}
		} catch (Exception e) {
			log.error(String.format("get sp exists error: [allInOneName=%s;spName=%s]", allInOneName, sp.getName()), e);
		}
		return false;
	}

	public static List<StoredProcedure> getAllSpNames(String allInOneName) throws Exception {
		try {
			// 如果是Sql Server，通过Sql语句获取所有视图的名称
			if ( !isMySqlServer(allInOneName) ) {
				String sql = "select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE'";
				return query(allInOneName, sql, null, new ResultSetExtractor<List<StoredProcedure>>() {
					@Override
					public List<StoredProcedure> extract(ResultSet rs) throws SQLException {
						List<StoredProcedure> results = new ArrayList<StoredProcedure>();
						while(rs.next()){
							StoredProcedure sp = new StoredProcedure();
							sp.setSchema(rs.getString(1));
							sp.setName(rs.getString(2));
							results.add(sp);
						}
						return results;
					}
				});
			}
		} catch (SQLException e) {
			handleException(String.format("get all sp names error: [allInOneName=%s]", allInOneName), e);
		}
		return new ArrayList<StoredProcedure>();
	}
	
	/**
	 * 获取存储过程的所有参数
	 */
	public static <T> T getSpParams(String allInOneName, StoredProcedure sp, ResultSetExtractor<T> extractor) {
		Connection connection = null;
		ResultSet spParamRs = null;
		try {
			connection = DataSourceUtil.getConnection(allInOneName);
			spParamRs = connection.getMetaData().getProcedureColumns(null, sp.getSchema(), sp.getName(), null);
			return extractor.extract(spParamRs);
		} catch (Exception e) {
			log.error(String.format("get sp params error: [allInOneName=%s;spName=%s;]", allInOneName, sp.getName()), e);
		} finally {
			releaseResource(spParamRs, connection);
		}
		return null;
	}

	public static List<String> getPrimaryKeyNames(String allInOneName, String tableName) {
		Connection connection = null;
		// 获取所有主键
		ResultSet primaryKeyRs = null;
		List<String> primaryKeys = new ArrayList<String>();
		try {
			connection = DataSourceUtil.getConnection(allInOneName);
			primaryKeyRs = connection.getMetaData().getPrimaryKeys(null, null, tableName);
			while (primaryKeyRs.next())
				primaryKeys.add(primaryKeyRs.getString("COLUMN_NAME"));
		} catch (Exception e) {
			log.error(String.format("get primary key names error: [allInOneName=%s;tableName=%s]", allInOneName, tableName), e);
		} finally {
			releaseResource(primaryKeyRs, connection);
		}
		return primaryKeys;
	}
	
	public static DbType getDotNetDbType(String typeName, int dataType, int length) {
		DbType dbType;
		if (null != typeName && typeName.equalsIgnoreCase("year")) {
			dbType = DbType.Int16;
		} else if (null != typeName && typeName.equalsIgnoreCase("uniqueidentifier")) {
			dbType = DbType.Guid;
		} else if (null != typeName && typeName.equalsIgnoreCase("sql_variant")) {
			dbType = DbType.Object;
		} else if (dataType == 1 && length > 1) {
			dbType = DbType.AnsiString;
		} else if (-155 == dataType) {
			dbType = DbType.DateTimeOffset;
		} else if (-7 == dataType && length > 1) {
			dbType = DbType.UInt64;
		} else {
			dbType = DbType.getDbTypeFromJdbcType(dataType);
		}
		return dbType;
	}
	
	public static <T> T getAllColumnNames(String allInOneName, String tableName, ResultSetExtractor<T> extractor) {
		Connection connection = null;
		ResultSet allColumnsRs = null;
		try {
			connection = DataSourceUtil.getConnection(allInOneName);
			allColumnsRs = connection.getMetaData().getColumns(null, null, tableName, null);
			return extractor.extract(allColumnsRs);
		} catch (Exception e) {
			log.error(String.format("get all column names error: [allInOneName=%s;tableName=%s;]", allInOneName, tableName), e);
		} finally {
			releaseResource(allColumnsRs, connection);
		}
		return null;
	}

	public static Map<String, Class<?>> getSqlType2JavaTypeMaper(String allInOneName, String tableViewName) {
		try {
			String sql = buildColumnSql(allInOneName, tableViewName);
			return query(allInOneName, sql, null, new ResultSetExtractor<Map<String, Class<?>>>() {
				@Override
				public Map<String, Class<?>> extract(ResultSet rs) throws SQLException {
					Map<String, Class<?>> map = new HashMap<String, Class<?>>();
					ResultSetMetaData rsMeta = rs.getMetaData();
					for(int i=1;i<=rsMeta.getColumnCount();i++){
						String columnName = rsMeta.getColumnName(i);
						Integer sqlType = rsMeta.getColumnType(i);
						Class<?> javaType = null;
						try {
							javaType = Class.forName(rsMeta.getColumnClassName(i));
						} catch (Exception e) {
							e.printStackTrace();
							javaType = Consts.jdbcSqlTypeToJavaClass.get(sqlType);
						}
						if(!map.containsKey(columnName) && null != javaType)
							map.put(columnName, javaType);
					}
					return map;
				}
			});
		} catch(Exception e){
			log.error(String.format("get sql-type to java-type maper error: [allInOneName=%s;tableViewName=%s]",
					allInOneName, tableViewName), e);
		}
		return new HashMap<String, Class<?>>();
	}
	
	private static String buildColumnSql(String allInOneName, String tableViewName) throws Exception {
		if( isMySqlServer(allInOneName) ){
			return "select * from " + tableViewName + " limit 1";
		} else {
			return "select top 1 * from " + tableViewName;
		}
	}
	
	public static Map<String, Integer> getColumnSqlType(String allInOneName, String tableViewName) {
		try {
			String sql = buildColumnSql(allInOneName, tableViewName);
			return query(allInOneName, sql, null, new ResultSetExtractor<Map<String, Integer>>() {
				@Override
				public Map<String, Integer> extract(ResultSet rs) throws SQLException {
					ResultSetMetaData rsMeta = rs.getMetaData();
					Map<String, Integer> map = new HashMap<String, Integer>();
					for (int i=1;i<=rsMeta.getColumnCount();i++) {
						String columnName = rsMeta.getColumnName(i);
						Integer sqlType = rsMeta.getColumnType(i);
						if(!map.containsKey(columnName) && null != sqlType) {
							map.put(columnName, sqlType);
						}
					}
					return map;
				}
			});
		} catch(Exception e){
			log.error(String.format("get sql-type to java-type maper error: [allInOneName=%s;tableViewName=%s]",
					allInOneName, tableViewName), e);
		}
		return new HashMap<String, Integer>();
	}
	
	private static boolean isMySqlServer(String allInOneName) throws Exception {
		Connection connection = null;
		try {
			String dbType = getDbType(allInOneName);
			connection = DataSourceUtil.getConnection(allInOneName);
			if(dbType.equalsIgnoreCase("Microsoft SQL Server")){
				return false;
			} else {
				return true;
			}
		} finally {
			releaseResource(null, connection);
		}
	}
	
	public static List<AbstractParameterHost> getSelectFieldHosts(String allInOneName, String sql, 
			ResultSetExtractor<List<AbstractParameterHost>> extractor) {
		String testSql = sql;
		int whereIndex = StringUtils.indexOfIgnoreCase(testSql, "where");
		if(whereIndex > 0)
			testSql = sql.substring(0, whereIndex);
		try {
			if( isMySqlServer(allInOneName) ){
				testSql = testSql + " limit 1";
			} else{
				testSql = testSql.replace("select", "select top(1)");
			}
			return query(allInOneName, testSql, null, extractor);
		} catch (Exception e) {
			log.error(String.format("get select field error: [allInOneName=%s;sql=%s;]", allInOneName, sql), e);
		}
		return new ArrayList<AbstractParameterHost>(); 
	}
	
	public static List<AbstractParameterHost> testAQuerySql(String allInOneName, String sql,
			String params, ResultSetExtractor<List<AbstractParameterHost>> extractor, boolean justTest) throws Exception {
		String[] parameters = params.split(";");
		Connection connection = null;
		ResultSet rs = null;
		try {
			Matcher m = inRegxPattern.matcher(sql);
			String temp=sql;
			while(m.find()) {
				temp = temp.replace(m.group(1), String.format("(?) "));
	    	}
			String replacedSql = temp.replaceAll("[@:]\\w+", "?");
			connection = DataSourceUtil.getConnection(allInOneName);
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
					ps.setObject(index, mockATest(Integer.valueOf(tuple[1])), Integer.valueOf(tuple[1]));
				}
			}
			rs = ps.executeQuery();
			return justTest ? new ArrayList<AbstractParameterHost>() : extractor.extract(rs);
		} catch(Exception e) {
			handleException(null, e);
		} finally {
			releaseResource(rs, connection);
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

	public static String getDbType(String allInOneName) throws Exception {
		String dbType = null;
		if (Consts.databaseType.containsKey(allInOneName)) {
			dbType = Consts.databaseType.get(allInOneName);
		} else {
			Connection connection = null;
			try {
				connection = DataSourceUtil.getConnection(allInOneName);
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(allInOneName, dbType);
			} catch(Exception e) {
				handleException(null, e);
			} finally {
				releaseResource(null, connection);
			}
		}
		return dbType;
	}
	
	public static DatabaseCategory getDatabaseCategory(String allInOneName) throws Exception {
		DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
		String dbType = getDbType(allInOneName);
		if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
			dbCategory = DatabaseCategory.MySql;
		}
		return dbCategory;
	}
	
	public static Map<String,String> getSqlserverColumnComment(String allInOneName, String tableName) throws Exception {
		Map<String,String> map = new HashMap<String,String>();
		if( isMySqlServer(allInOneName) ){
			return map;
		}
		String sql = ""
				+ "SELECT sys.columns.name as name, "
				+ "       CONVERT(VARCHAR(1000), (SELECT VALUE "
				+ "                              FROM   sys.extended_properties "
				+ "                              WHERE  sys.extended_properties.major_id = sys.columns.object_id "
				+ "                                     AND sys.extended_properties.minor_id = sys.columns.column_id)) AS description "
				+ "FROM   sys.columns, "
				+ "       sys.tables "
				+ "WHERE  sys.columns.object_id = sys.tables.object_id "
				+ "       AND sys.tables.name = ? "
				+ "ORDER  BY sys.columns.column_id ";
		return query(allInOneName, sql, new Object[]{tableName}, new ResultSetExtractor<Map<String,String>>() {
			@Override
			public Map<String, String> extract(ResultSet rs) throws SQLException {
				Map<String,String> map = new HashMap<String,String>();
				while (rs.next())
					map.put(rs.getString("name").toLowerCase(), rs.getString("description"));
				return map;
			}
		});
	}
	
	private static void releaseResource(ResultSet rs, Connection conn) {
		JdbcUtils.closeResultSet(rs);
		JdbcUtils.closeConnection(conn);
	}
	
	private static void handleException(String msg, Exception e) throws Exception {
		log.warn(msg==null?e.getMessage():msg, e);
		throw e;
	}
	
}
