package com.ctrip.sysdev.das.common.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.ctrip.sysdev.das.common.to.LogicDB;
import com.ctrip.sysdev.das.common.to.LogicDbSetting;
import com.ctrip.sysdev.das.common.to.MasterLogicDB;

public class DruidDataSourceWrapper {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Map<String, DruidDataSource> masterMap = new ConcurrentHashMap<String, DruidDataSource>();
	private Map<String, DruidDataSource[]> slaveMap = new ConcurrentHashMap<String, DruidDataSource[]>();
	
	public static DruidDataSourceWrapper dataSource;

	int initialSize = 1;
	int maxActive = 10;
	int minIdle = 1;
	int maxWait = 60000;
	int timeBetweenEvictionRunsMillis = 60000;
	int minEvictableIdleTimeMillis = 60000;
	String validationQuery = "select 1";
	
	private DasConfigureReader reader;
	
	public DruidDataSourceWrapper(DasConfigureReader reader, String...logicDbs) throws Exception {
		this.reader = reader;
		createMaster(logicDbs);
		dataSource = this;
	}
	
	private void createMaster(String...logicDbs) throws Exception {
		for(String logicDb: logicDbs) {
			MasterLogicDB db = reader.getMasterLogicDB(logicDb);
			createMaster(logicDb);
			createSlave(db.getName(), db.getSlave());
		}
	}

	private void createMaster(MasterLogicDB db) throws Exception {
		String user = getConfig(buildKey(db.getName(), "user"));
		String password = getConfig(buildKey(db.getName(), "password"));;
		masterMap.put(db.getName(), create(db.getSetting(), user, password));
	}

	private void createSlave(String masterName, List<LogicDB> slaveDbs) throws Exception {
		if(slaveDbs == null || slaveDbs.size() == 0)
			return ;
		
		DruidDataSource[] slaveDSs = new DruidDataSource[slaveDbs.size()];
		
		int i = 0;
		for(LogicDB slaveDb: slaveDbs){
			String slaveName = slaveDb.getName();
			String user = getConfig(buildKey(masterName, buildKey(slaveName, "user")));
			String password = getConfig(buildKey(masterName, buildKey(slaveName, "password")));
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
	
	public final static String KEY_SEPARATOR = ".";

	public String buildKey(String parent, String key) {
		return new StringBuilder(parent).append(KEY_SEPARATOR).append(key).toString();
	}
	
	private String getConfig(String key) {
		return null;
	}

}
