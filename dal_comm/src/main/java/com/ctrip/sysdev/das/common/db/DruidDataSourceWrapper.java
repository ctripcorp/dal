package com.ctrip.sysdev.das.common.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.ctrip.sysdev.das.common.to.Deployment;
import com.ctrip.sysdev.das.common.to.LogicDB;
import com.ctrip.sysdev.das.common.to.LogicDbSetting;
import com.ctrip.sysdev.das.common.to.MasterLogicDB;
import com.ctrip.sysdev.das.common.util.Configuration;

// TODO  handle configure change
public class DruidDataSourceWrapper {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Map<String, DruidDataSource> masterMap = new ConcurrentHashMap<String, DruidDataSource>();
	private Map<String, DruidDataSource[]> slaveMap = new ConcurrentHashMap<String, DruidDataSource[]>();
	
	int initialSize = 1;
	int maxActive = 10;
	int minIdle = 1;
	int maxWait = 60000;
	int timeBetweenEvictionRunsMillis = 60000;
	int minEvictableIdleTimeMillis = 60000;
	String validationQuery = "select 1";
	
	private DasConfigureReader reader;
	
	/**
	 * For Das worker
	 */
	public DruidDataSourceWrapper(Deployment deployment, DasConfigureReader reader) throws Exception {
		this(reader, getLogicDbs(deployment, reader));
	}
	
	public static String[] getLogicDbs(Deployment deployment, DasConfigureReader reader) throws Exception { 
		String[] logicDbs;
		
		if(deployment.isShared()) {
			Set<String> dbs = new HashSet<String>();
			for(String logicDbGroupName: deployment.convertToDbGroups()) {
				dbs.addAll(Arrays.asList(reader.getLogicDbsByGroup(logicDbGroupName)));
			}
			logicDbs = dbs.toArray(new String[0]);
		} else {
			logicDbs = new String[] {deployment.getValue()};
		}
		
		return logicDbs;
	}
	
	/**
	 * For direct client
	 */
	public DruidDataSourceWrapper(DasConfigureReader reader, String...logicDbs) throws Exception {
		this.reader = reader;
		initialize(logicDbs);
	}
	
	private void initialize(String...logicDbs) throws Exception {
		for(String logicDb: logicDbs) {
			MasterLogicDB db = reader.getMasterLogicDB(logicDb);
			createMaster(db);
			createSlave(db.getName(), db.getSlave());
		}
	}

	private void createMaster(MasterLogicDB db) throws Exception {
		String user = getConfig(db.getName(), "user");
		String password = getConfig(db.getName(), "password");
		masterMap.put(db.getName(), create(db.getSetting(), user, password));
	}

	private void createSlave(String masterName, List<LogicDB> slaveDbs) throws Exception {
		if(slaveDbs == null || slaveDbs.size() == 0)
			return ;
		
		DruidDataSource[] slaveDSs = new DruidDataSource[slaveDbs.size()];
		
		int i = 0;
		for(LogicDB slaveDb: slaveDbs){
			String slaveName = slaveDb.getName();
			String user = getConfig(masterName, slaveName, "user");
			String password = getConfig(masterName, slaveName, "password");
			slaveDSs[i++] = create(slaveDb.getSetting(), user, password);
		}
	}

	private DruidDataSource create(LogicDbSetting setting, String user, String password) throws Exception {
		DruidDataSource ds = new DruidDataSource();
		
		ds.setDriverClassName(setting.getDriver());
		ds.setUrl(setting.getJdbcUrl());
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

	public Connection getConnection(String logicDbName, boolean isMaster, boolean isSelect)
			throws SQLException {
		if (isMaster)
			return getMasterConnection(logicDbName);

		if (isSelect)
			return getSlaveConnection(logicDbName);

		return getMasterConnection(logicDbName);
	}

	public Connection getMasterConnection(String logicDbName) throws SQLException {
		return masterMap.get(logicDbName).getConnection();
	}

	public Connection getSlaveConnection(String logicDbName) throws SQLException {
		DruidDataSource[] slaves = slaveMap.get(logicDbName);
		if(slaves == null)
			return getMasterConnection(logicDbName);
		
		int i = (int)(Math.random() * slaves.length);
		return slaves[i].getConnection();
	}
	
	private String getConfig(String...keys) {
		return Configuration.get(Configuration.buildKey(keys));
	}
}
