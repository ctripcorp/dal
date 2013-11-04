package com.ctrip.sysdev.das;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private Map<String, DruidDataSource> masterMap = new HashMap<String, DruidDataSource>();
	
	private Map<String, DruidDataSource[]> slaveMap = new HashMap<String, DruidDataSource[]>();
	
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
		createMaster(zk);		
		dataSource = this;
	}
	
	private void createMaster(ZooKeeper zk) throws Exception {
		List<String> logicDBs = zk.getChildren(DB, false);
		for(String logicDB: logicDBs) {
			createMaster(zk, logicDB);
			createSlave(zk, logicDB);
		}
	}

	private void createMaster(ZooKeeper zk, String logicDB) throws Exception {
		String logicDBPath = StringKit.buildPath(DB, logicDB);
		String driverClass = new String(zk.getData(StringKit.buildPath(logicDBPath, DRIVER), false, null));
		String jdbcUrl = new String(zk.getData(StringKit.buildPath(logicDBPath, JDBC_URL), false, null));
		String user = Configuration.get(StringKit.buildKey(logicDB, "user"));
		String password = Configuration.get(StringKit.buildKey(logicDB, "password"));;
		masterMap.put(logicDB, create(logicDB, driverClass, jdbcUrl, user, password));
	}

	private void createSlave(ZooKeeper zk, String logicDB) throws Exception {
		String slavePath = StringKit.buildPath(StringKit.buildPath(DB, logicDB), "slave");
		if(zk.exists(slavePath, false) == null)
			return;
		
		List<String> slaveDBs = zk.getChildren(slavePath, false);
		DruidDataSource[] slaveDSs = new DruidDataSource[slaveDBs.size()];
		
		int i = 0;
		for(String slaveDB: slaveDBs){
			String slaveDBPath = StringKit.buildPath(slavePath, logicDB);
			String driverClass = new String(zk.getData(StringKit.buildPath(slaveDBPath, DRIVER), false, null));
			String jdbcUrl = new String(zk.getData(StringKit.buildPath(slaveDBPath, JDBC_URL), false, null));
			String user = Configuration.get(StringKit.buildKey(logicDB, StringKit.buildKey(slaveDB, "user")));
			String password = Configuration.get(StringKit.buildKey(logicDB, StringKit.buildKey(slaveDB, "password")));
			slaveDSs[i++] = create(logicDB, driverClass, jdbcUrl, user, password);
		}
	}

	private DruidDataSource create(String ligicDb, String driverClass, String jdbcUrl,
			String user, String password) throws Exception {
		DruidDataSource ds = new DruidDataSource();
		
		ds.setDriverClassName(driverClass);
		ds.setUrl(jdbcUrl);
		ds.setUsername(user);
		ds.setPassword(password);

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
			logger.info("Connection initilized: jdbcUrl=" + ds.getUrl());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public Connection getMasterConnection(String logicDb) throws SQLException {
		return masterMap.get(logicDb).getConnection();
	}

	public Connection getSlaveConnection(String logicDb) throws SQLException {
		DruidDataSource[] slaves = slaveMap.get(logicDb);
		if(slaves == null)
			return getMasterConnection(logicDb);
		
		int i = (int)(Math.random() * slaves.length);
		return slaves[i].getConnection();
	}
}
