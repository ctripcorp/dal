package com.ctrip.platform.dal.daogen.utils;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.ctrip.platform.dal.daogen.dao.DalGroupDBDao;
import com.ctrip.platform.dal.daogen.entity.DalGroupDB;

public class DataSourceUtil {

	private static final Logger log = Logger.getLogger(DataSourceUtil.class);

	private static final String DBURL_MYSQL = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8";
	private static final String DBURL_SQLSERVER = "jdbc:sqlserver://%s:%s;DatabaseName=%s";

	private static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
	private static final String DRIVER_SQLSERVRE = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	public static DataSource getDataSource(String allInOneName)
			throws SQLException {
		if (isEmpty(allInOneName)) {
			throw new SQLException(
					"the param allInOneName is null. So can not get DataSourse.");
		}
		DataSource ds = null;
		DalGroupDBDao allDbDao = SpringBeanGetter.getDaoOfDalGroupDB();
		DalGroupDB db = allDbDao.getGroupDBByDbName(allInOneName);
		if (db == null) {
			log.error(allInOneName + " is not exist in the table of alldbs.");
			throw new SQLException(allInOneName
					+ " is not exist in the table of alldbs.");
		}
		String address = db.getDb_address();
		String port = db.getDb_port();
		String userName = db.getDb_user();
		String password = db.getDb_password();
		String driverClass = db.getDb_providerName();
		String catalog = db.getDb_catalog();
		validSqlParam(allInOneName, address, port, catalog, userName, password, driverClass);
		ds = createDataSource(address, port, catalog, userName, password, driverClass);
		return ds;
	}

	private static DataSource createDataSource(String address, String port, String catalog,
			String userName, String password, String driverClass)
			throws SQLException {

		String url = "";
		if ("System.Sql.SqlClient".equals(driverClass)) {
			driverClass = DRIVER_SQLSERVRE;
			url = String.format(DBURL_SQLSERVER, address, port, catalog);
		} else {
			driverClass = DRIVER_MYSQL;
			url = String.format(DBURL_MYSQL, address, port, catalog);
		}

		PoolProperties p = new PoolProperties();

		p.setUrl(url);
		p.setUsername(userName);
		p.setPassword(password);
		p.setDriverClassName(driverClass);
		p.setJmxEnabled(false);
		p.setTestWhileIdle(false);
		p.setTestOnBorrow(true);
		p.setValidationQuery("SELECT 1");
		p.setTestOnReturn(false);
		p.setValidationInterval(30000L);
		p.setTimeBetweenEvictionRunsMillis(30000);
		p.setMaxActive(100);
		p.setInitialSize(10);
		p.setMaxWait(20000);
		p.setRemoveAbandonedTimeout(60);
		p.setMinEvictableIdleTimeMillis(30000);
		p.setMinIdle(10);
		p.setLogAbandoned(false);
		p.setRemoveAbandoned(true);
//		p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

		org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource(
				p);

		ds.createPool();
		return ds;
	}

	private static void validSqlParam(String allInOneName, String address,
			String port, String catalog, String userName, String password,
			String driverClass) throws SQLException {
		if (isEmpty(address)) {
			throw new SQLException("the address of " + allInOneName
					+ " is null.");
		}
		if (isEmpty(port)) {
			throw new SQLException("the port of " + allInOneName + " is null.");
		}
		if (isEmpty(userName)) {
			throw new SQLException("the userName of " + allInOneName
					+ " is null.");
		}
		if (isEmpty(password)) {
			throw new SQLException("the password of " + allInOneName
					+ " is null.");
		}
		if (isEmpty(catalog)) {
			throw new SQLException("the catalog of " + allInOneName
					+ " is null.");
		}
		if (isEmpty(driverClass)) {
			throw new SQLException("the driverClass of " + allInOneName
					+ " is null.");
		}
	}

	private static boolean isEmpty(String str) {
		if (str != null && (!"".equals(str.trim()))) {
			return false;
		}
		return true;
	}
}
