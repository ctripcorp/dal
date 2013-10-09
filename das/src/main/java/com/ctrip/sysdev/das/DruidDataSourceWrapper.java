package com.ctrip.sysdev.das;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.ctrip.sysdev.das.controller.DasControllerConstants;
import com.ctrip.sysdev.das.utils.Configuration;
import com.ctrip.sysdev.das.utils.StringKit;
import com.google.inject.Inject;
import com.google.inject.name.Named;


public class DruidDataSourceWrapper implements DasControllerConstants {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// Should we make it thread safe? Currently, it is just read-only. If we want to support hot depoly
	// We need to consider make it thread safe.
	private Map<String, DruidDataSource> dsMap = new HashMap<String, DruidDataSource>();
	
	public static DruidDataSourceWrapper dataSource;

	@Inject
	@Named("initialSize")
	int initialSize;

	@Inject
	@Named("maxActive")
	int maxActive;

	@Inject
	@Named("minIdle")
	int minIdle;

	@Inject
	@Named("maxWait")
	int maxWait;

	@Inject
	@Named("timeBetweenEvictionRunsMillis")
	int timeBetweenEvictionRunsMillis;

	@Inject
	@Named("minEvictableIdleTimeMillis")
	int minEvictableIdleTimeMillis;

	@Inject
	@Named("validationQuery")
	String validationQuery;

	public void initDataSourceWrapper(ZooKeeper zk) throws Exception {
		List<String> logicDBs = zk.getChildren(DB, false);
		for(String logicDB: logicDBs) {
			String logicDBPath = StringKit.buildPath(DB, logicDB);
			String driverClass = new String(zk.getData(StringKit.buildPath(logicDBPath, DRIVER), false, null));
			String jdbcUrl = new String(zk.getData(StringKit.buildPath(logicDBPath, JDBC_URL), false, null));
			String user = Configuration.get(StringKit.buildKey(logicDB, "user"));
			String password = Configuration.get(StringKit.buildKey(logicDB, "password"));;
			create(logicDB, driverClass, jdbcUrl, user, password);
		}

		for (DruidDataSource ds : dsMap.values()) {

			ds.setInitialSize(initialSize);
			ds.setMinIdle(minIdle);
			ds.setMaxActive(maxActive);
			ds.setMaxWait(maxWait);

			ds.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
			ds.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
			ds.setValidationQuery(validationQuery);
			ds.setTestWhileIdle(true);
			ds.setTestOnBorrow(false);
			ds.setTestOnReturn(false);
			ds.init();
			try {
				Connection connection = ds.getConnection();
				connection.close();
				logger.info("数据连接池已经建立:jdbcUrl=" + ds.getUrl());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		dataSource = this;
	}

	private void create(String ligicDb, String driverClass, String jdbcUrl,
			String user, String password) {
		DruidDataSource dataSource = new DruidDataSource();
		dsMap.put(ligicDb, dataSource);
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(jdbcUrl);
		dataSource.setUsername(user);
		dataSource.setPassword(password);
	}

	public Connection getConnection(String logicDb) throws SQLException {
//		logicDb = "testMsSql";
		return dsMap.get(logicDb).getConnection();
	}
}
