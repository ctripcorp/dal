package com.ctrip.platform.dal.common.db;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import com.ctrip.platform.dal.common.to.LogicDbSetting;
import com.ctrip.platform.dal.common.to.MasterLogicDB;
import com.ctrip.platform.dal.common.util.Configuration;

public class ConnectionPropertyReader implements DasConfigureReader {
	public static final String DEFAULT_CONF_NAME = "connections.properties";
	private Properties conf;
	
	public ConnectionPropertyReader() {
		load();
	}
	
	private void load() {
		conf = new Properties();
		InputStream in = null;
		ClassLoader classLoader;
		classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = Configuration.class.getClassLoader();
		}

		try {
			URL url = classLoader.getResource(DEFAULT_CONF_NAME);
			if (url == null)
				return;

			in = url.openStream();
			conf.load(in);
		} catch (Exception e) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}

	}
	@Override
	public String[] getLogicDbsByGroup(String logicDbGroupName)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private static final String SEPARATOR = ".";
	private static final String DRIVER = "driver";
	private static final String JDBC_URL = "jdbcUrl";
	private static final String LOGIC_DB_NAMES = "logicDbNames";
	private static final String SLAVES = "slaves";
	
	@Override
	public MasterLogicDB getMasterLogicDB(String logicdbName) throws Exception {
		MasterLogicDB master = new MasterLogicDB();
		master.setName(logicdbName);
		LogicDbSetting setting = new LogicDbSetting();
		setting.setDriver(get(logicdbName + SEPARATOR + DRIVER));
		setting.setJdbcUrl(get(logicdbName + SEPARATOR + JDBC_URL));
		master.setSetting(setting);
		return master;
	}
	
	public String[] getLogicDbNames() {
		return get(LOGIC_DB_NAMES).split(",");
	}
	
	private String get(String key) {
		return conf.getProperty(key);
	}
}
