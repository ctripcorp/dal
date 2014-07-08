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

import microsoft.sql.DateTimeOffset;

import org.apache.log4j.Logger;
import org.springframework.jdbc.support.JdbcUtils;

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpParameterHost;
import com.ctrip.platform.dal.daogen.host.java.JavaParameterHost;

public class DbUtils {
	private static Logger log;
	private static List<Integer> validMode;
	private static String regEx = null;
	private static Pattern inRegxPattern = null;

	static {
		log = Logger.getLogger(DbUtils.class);
		validMode = new ArrayList<Integer>();
		validMode.add(DatabaseMetaData.procedureColumnIn);
		validMode.add(DatabaseMetaData.procedureColumnInOut);
		validMode.add(DatabaseMetaData.procedureColumnOut);
		 regEx="in\\s(@\\w+)";
		 inRegxPattern = Pattern.compile(regEx, java.util.regex.Pattern.CASE_INSENSITIVE);
	}

	public static boolean tableExists(String dbName, String tableName) {

		boolean result = false;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = DataSourceUtil.getConnection(dbName);

			String dbType = null;
			if (Consts.databaseType.containsKey(dbName)) {
				dbType = Consts.databaseType.get(dbName);
			} else {
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);
			}

			if (dbType.equals("Microsoft SQL Server")) {

				String sql = String
						.format("select Name from sysobjects where xtype  = 'u' and status>=0 and Name=?",
								dbName);
				PreparedStatement statement = connection.prepareStatement(sql,
						ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				statement.setString(1, tableName);
				rs = statement.executeQuery();
				result = rs.next();
			} else {
				String[] types = { "TABLE" };
				rs = connection.getMetaData().getTables(null, null,
						tableName, types);
				result = rs.next();
			}
		} catch (SQLException e) {
			log.error(String.format("get table exists error: [dbName=%s;tableName=%s]", 
					dbName, tableName), e);
		} catch (Exception e) {
			log.error(String.format("get table exists error: [dbName=%s;tableName=%s]", 
					dbName, tableName), e);
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
		List<String> results = new ArrayList<String>();
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = DataSourceUtil.getConnection(dbName);
			String dbType = null;
			if (Consts.databaseType.containsKey(dbName)) {
				dbType = Consts.databaseType.get(dbName);
			} else {
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);
			}

			// 如果是Sql Server，通过Sql语句获取所有视图的名称
			/*if (dbType.equals("Microsoft SQL Server")) {
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
			} else {*/
				String[] types = { "TABLE" };

				rs = connection.getMetaData().getTables(null, "dbo", "%",
						types);
				String tableName = null;
				while (rs.next()) {
					tableName = rs.getString("TABLE_NAME");
					if(tableName.toLowerCase().equals("sysdiagrams")){
						continue;
					}
					results.add(rs.getString("TABLE_NAME"));
				}
			//}
		} catch (SQLException e) {
//			log.error(String.format("get all table names error: [dbName=%s]", 
//					dbName), e);
			throw e;
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
			connection = DataSourceUtil.getConnection(dbName);
			String dbType = null;
			if (Consts.databaseType.containsKey(dbName)) {
				dbType = Consts.databaseType.get(dbName);
			} else {
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);
			}

			if (dbType.equals("Microsoft SQL Server")) {

				String sql = String
						.format(" select Name from sysobjects where xtype ='v' and status>=0 and Name = ?",
								dbName);
				PreparedStatement statement = connection.prepareStatement(sql,
						ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				statement.setString(1, viewName);
				rs = statement.executeQuery();
				result = rs.next();
				
			} else {
				String[] types = { "VIEW" };
				rs = connection.getMetaData().getTables(null, null, viewName,
						types);
				result = rs.next();
			}
		} catch (SQLException e) {
			log.error(String.format("get view exists error: [dbName=%s;viewName=%s]", 
					dbName, viewName), e);
		} catch (Exception e) {
			log.error(String.format("get view exists error: [dbName=%s;viewName=%s]", 
					dbName, viewName), e);
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

		List<String> results = new ArrayList<String>();
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = DataSourceUtil.getConnection(dbName);
			String dbType = null;
			if (Consts.databaseType.containsKey(dbName)) {
				dbType = Consts.databaseType.get(dbName);
			} else {
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);
			}

			// 如果是Sql Server，通过Sql语句获取所有视图的名称
			/*if (dbType.equals("Microsoft SQL Server")) {
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
			} else {*/
				String[] types = { "VIEW" };

				rs = connection.getMetaData().getTables(null, "dbo", "%",
						types);
				while (rs.next()) {
					results.add(rs.getString("TABLE_NAME"));
				}
			//}
		} catch (SQLException e) {
//			log.error(String.format("get all view names error: [dbName=%s]", 
//					dbName), e);
			throw e;
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeConnection(connection);
		}

		return results;
	}

	public static boolean spExists(String dbName, final StoredProcedure sp) {

		boolean result = false;
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = DataSourceUtil.getConnection(dbName);
			String dbType = null;
			if (Consts.databaseType.containsKey(dbName)) {
				dbType = Consts.databaseType.get(dbName);
			} else {
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);
			}
			// 如果是Sql Server，通过Sql语句获取所有表和视图的名称
			if (dbType.equals("Microsoft SQL Server")) {

				String sql = "select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE' and SPECIFIC_SCHEMA=? and SPECIFIC_NAME=?";
				PreparedStatement statement = connection.prepareStatement(sql,
						ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				statement.setString(1, sp.getSchema());
				statement.setString(2, sp.getName());
				rs = statement.executeQuery();
				result = rs.next();
				
			}
		} catch (Exception e) {
			log.error(String.format("get sp exists error: [dbName=%s;spName=%s]", 
					dbName, sp.getName()), e);
//			throw e;
		} finally {
			JdbcUtils.closeResultSet(rs);
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

		List<StoredProcedure> results = new ArrayList<StoredProcedure>();
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = DataSourceUtil.getConnection(dbName);
			String dbType = null;
			if (Consts.databaseType.containsKey(dbName)) {
				dbType = Consts.databaseType.get(dbName);
			} else {
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);
			}

			// 如果是Sql Server，通过Sql语句获取所有视图的名称
			if (dbType.equals("Microsoft SQL Server")) {

				String sql = String
						.format("select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE'",
								dbName);
				PreparedStatement statement = connection.prepareStatement(sql,
						ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				rs = statement.executeQuery();
				while(rs.next()){
					StoredProcedure sp = new StoredProcedure();
					sp.setSchema(rs.getString(1));
					sp.setName(rs.getString(2));
					results.add(sp);
				}
				
			}
		} catch (SQLException e) {
//			log.error(String.format("get all sp names error: [dbName=%s]", 
//					dbName), e);
			throw e;
		} finally {
			JdbcUtils.closeResultSet(rs);
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
			connection = DataSourceUtil.getConnection(dbName);

			ResultSet spParams = connection.getMetaData().getProcedureColumns(
					null, sp.getSchema(), sp.getName(), null);
			boolean terminal = false;
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
			} else if (language == CurrentLanguage.Java) {
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

					host.setName(spParams.getString("COLUMN_NAME").replace("@",""));
					Class<?> javaClass = Consts.jdbcSqlTypeToJavaClass.get(host.getSqlType());
					if(null == javaClass){
						if(-153 == host.getSqlType()){
							log.error(String.format("The Table-Valued Parameters is not support for JDBC. [%s, %s]", 
									dbName, sp.getName()));
							terminal = true;
							break;
						}else{
							log.fatal(String.format("The java type cant be mapped.[%s, %s, %s, %s, %s]", 
									host.getName(), dbName, sp.getName(), host.getSqlType(), javaClass));
							terminal = true;
							break;
						}
					}
					host.setJavaClass(javaClass);

					parameters.add(host);
				}
			}
			return terminal ? null : parameters;
		} catch (SQLException e) {
			log.error(String.format("get sp params error: [dbName=%s;spName=%s;language=%s]", 
					dbName,sp.getName(), language.name()), e);
		} catch (Exception e) {
			log.error(String.format("get sp params error: [dbName=%s;spName=%s;language=%s]", 
					dbName,sp.getName(), language.name()), e);
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
			connection = DataSourceUtil.getConnection(dbName);
			primaryKeyRs = connection.getMetaData().getPrimaryKeys(null,
					null, tableName);

			while (primaryKeyRs.next()) {
				primaryKeys.add(primaryKeyRs.getString("COLUMN_NAME"));
			}
		} catch (SQLException e) {
			log.error(String.format("get primary key names error: [dbName=%s;tableName=%s]", 
					dbName, tableName), e);
		} catch (Exception e) {
			log.error(String.format("get primary key names error: [dbName=%s;tableName=%s]", 
					dbName, tableName), e);
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
		ResultSet allColumnsRs = null;
		List<AbstractParameterHost> allColumns = new ArrayList<AbstractParameterHost>();
		try {
			connection = DataSourceUtil.getConnection(dbName);

			// 首先检查表是否存在

			allColumnsRs = connection.getMetaData().getColumns(null, null,
					tableName, null);
			boolean terminal = false;
			if (language == CurrentLanguage.CSharp) {
				while (allColumnsRs.next()) {
					CSharpParameterHost host = new CSharpParameterHost();
					String typeName = allColumnsRs.getString("TYPE_NAME");
					int dataType = allColumnsRs.getInt("DATA_TYPE");
					int length = allColumnsRs.getInt("COLUMN_SIZE");
					
					//特殊处理
					DbType dbType;
					if(null != typeName && typeName.equalsIgnoreCase("year"))
						dbType = DbType.Int16;
					else if(null != typeName && typeName.equalsIgnoreCase("sql_variant"))
						dbType = DbType.Object;
					else if (dataType == 1 && length > 1)
						dbType = DbType.AnsiString;
					else if(-155 == dataType){
						dbType = DbType.DateTimeOffset;
					}
					else
						dbType =DbType.getDbTypeFromJdbcType(dataType);

					host.setDbType(dbType);
					//host.setName(CommonUtils.normalizeVariable(allColumnsRs.getString("COLUMN_NAME")));
					host.setName(allColumnsRs.getString("COLUMN_NAME"));
					String remark = allColumnsRs.getString("REMARKS");
					if(remark == null)
						remark = "";
					host.setComment(remark.replace("\n", " "));
					host.setType(DbType.getCSharpType(host.getDbType()));
					host.setIdentity(allColumnsRs.getString("IS_AUTOINCREMENT")
							.equalsIgnoreCase("YES"));
					host.setNullable(allColumnsRs.getShort("NULLABLE") == DatabaseMetaData.columnNullable);
					host.setValueType(Consts.CSharpValueTypes.contains(host
							.getType()));
					// 仅获取String类型的长度
					 if (host.getType().equalsIgnoreCase("string"))
						 host.setLength(length);

					// COLUMN_SIZE

					allColumns.add(host);
				}
			} else if (language == CurrentLanguage.Java) {
				Map<Integer, Class<?>> typeMapper = getSqlType2JavaTypeMaper(dbName, tableName);
				while (allColumnsRs.next()) {
					JavaParameterHost host = new JavaParameterHost();
					String typeName = allColumnsRs.getString("TYPE_NAME");
					host.setSqlType(allColumnsRs.getInt("DATA_TYPE"));
					host.setName(allColumnsRs.getString("COLUMN_NAME"));
					Class<?> javaClass = null != typeMapper && typeMapper.containsKey(host.getSqlType()) ? 
							typeMapper.get(host.getSqlType()) :Consts.jdbcSqlTypeToJavaClass.get(host.getSqlType());
					if(null == javaClass){
						if(null != typeName && typeName.equalsIgnoreCase("sql_variant")){
							log.fatal(String.format("The sql_variant is not support by java.[%s, %s, %s, %s, %s]", 
									host.getName(), dbName, tableName, host.getSqlType(), javaClass));
							terminal = true;
							break;
						}
						else if(null != typeName && typeName.equalsIgnoreCase("datetimeoffset")){
							javaClass = DateTimeOffset.class;
						}
						else{
							log.fatal(String.format("The java type cant be mapped.[%s, %s, %s, %s, %s]", 
									host.getName(), dbName, tableName, host.getSqlType(), javaClass));
							terminal = true;
							break;
						}
					}
					host.setJavaClass(javaClass);
					host.setIndex(allColumnsRs.getInt("ORDINAL_POSITION"));
					host.setIdentity(allColumnsRs.getString("IS_AUTOINCREMENT")
							.equalsIgnoreCase("YES"));
					allColumns.add(host);
				}
			}

			return terminal ? null : allColumns;
		} catch (SQLException e) {
			log.error(String.format("get all column names error: [dbName=%s;tableName=%s;language=%s]", 
					dbName, tableName, language), e);
		} catch (Exception e) {
			log.error(String.format("get all column names error: [dbName=%s;tableName=%s;language=%s]", 
					dbName, tableName, language), e);
		} finally {
			JdbcUtils.closeResultSet(allColumnsRs);
			JdbcUtils.closeConnection(connection);
		}

		return null;
	}

	private static Map<Integer, Class<?>> getSqlType2JavaTypeMaper(String dbName, String tableViewName)
	{
		Map<Integer, Class<?>> map = new HashMap<Integer, Class<?>>();;
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = DataSourceUtil.getConnection(dbName);
			String dbType = null;
			if (Consts.databaseType.containsKey(dbName)) {
				dbType = Consts.databaseType.get(dbName);
			} else {
				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);
			}
			
			rs = connection.getMetaData().getColumns(null, null,
					tableViewName, null);
			
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				Integer sqlType = rsmd.getColumnType(i);
				Class<?> javaType = null;
				try {
					javaType = Class.forName(rsmd.getColumnClassName(i));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				if(!map.containsKey(sqlType) && null != javaType)
				{
					map.put(sqlType, javaType);
				}
			}
		} catch (SQLException e) {
			log.error(String.format("get sql-type to java-type maper error: [dbName=%s;tableVeiwName=%s]",
					dbName, tableViewName), e);
		} catch(Exception e){
			log.error(String.format("get sql-type to java-type maper error: [dbName=%s;tableVeiwName=%s]",
					dbName, tableViewName), e);
		}
		finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeConnection(connection);
			
		}
		return map;
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
			
			Matcher m = inRegxPattern.matcher(sql);
			String temp=sql;
			while(m.find()) {
				temp = temp.replace(m.group(1), String.format("(?) "));
	    	}
			
			String replacedSql = temp.replaceAll("[@:]\\w+", "?");

			connection = DataSourceUtil.getConnection(dbName);

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
					pHost.setName(rsMeta.getColumnLabel(i));
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
					paramHost.setName(rsMeta.getColumnLabel(i));
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
			
			
		} catch (SQLException e) {
			log.error(String.format("test query sql error: [dbName=%s;sql=%s;language=%s]", 
					dbName, sql, language), e);
		} catch (Exception e) {
			log.error(String.format("test query sql error: [dbName=%s;sql=%s;language=%s]", 
					dbName, sql, language), e);
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

	public static String getDbType(String dbName) throws Exception {

		String dbType = null;
		if (Consts.databaseType.containsKey(dbName)) {
			dbType = Consts.databaseType.get(dbName);
		} else {
			Connection connection = null;
			try {
				connection = DataSourceUtil.getConnection(dbName);

				dbType = connection.getMetaData().getDatabaseProductName();
				Consts.databaseType.put(dbName, dbType);

			} catch (Exception ex) {
//				log.error(String.format("get db type error: [dbName=%s]", dbName), ex);
				throw ex;
			} finally {
				JdbcUtils.closeConnection(connection);
			}
		}
		return dbType;
	}
}
