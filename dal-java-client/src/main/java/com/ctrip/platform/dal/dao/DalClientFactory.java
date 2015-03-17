package com.ctrip.platform.dal.dao;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.dao.client.DalDirectClient;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;
import com.ctrip.platform.dal.sql.logging.MetricsLogger;

public class DalClientFactory {
	private static Logger logger = LoggerFactory.getLogger(DalClientFactory.class);
	private static final String CLIENT_VERSION = "dal.client.version";
//	private static AtomicReference<DruidDataSourceWrapper> connPool = new AtomicReference<DruidDataSourceWrapper>();

	private static AtomicReference<DalConfigure> configureRef = new AtomicReference<DalConfigure>();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				shutdownFactory();
			}
		}));
	}

	/**
	 * Initialize for DB All In One client. Load Dal.config from classpath
	 * 
	 * @throws Exception
	 */
	public static void initClientFactory() throws Exception {
		internalInitClientFactory(null);
	}
	
	/**
	 * Initialize for DB All In One client. Load Dal.config from give path
	 * 
	 * @param path
	 *            Dal.Config file path
	 * @throws Exception
	 */
	public static void initClientFactory(String path) throws Exception {
		if(path == null)
			throw new NullPointerException("Path is empty");
		
		internalInitClientFactory(path);
	}
	
	private static void internalInitClientFactory(String path) throws Exception {
		if (configureRef.get() != null) {
			logger.warn("Dal Java Client Factory is already initialized.");
			return;
		}

		synchronized (DalClientFactory.class) {
			if (configureRef.get() != null) {
				return;
			}
			
			if(path == null) {
				configureRef.set(DalConfigureFactory.load());
				logger.info("Successfully initialized Dal Java Client Factory");
			} else {
				configureRef.set(DalConfigureFactory.load(path));
				logger.info("Successfully initialized Dal Java Client Factory with " + path);
			}
		}
	}
	
	public static Set<String> getAllDB(){
		return configureRef.get().getAllDB();
	}

	/**
	 * Actively initialize connection pools for all the logic db in the
	 * Dal.config
	 */
	public static void warmUpConnections() {
		getDalConfigure().warmUpConnections();
	}

	public static DalClient getClient(String logicDbName) {
		String version = System.getProperty(CLIENT_VERSION);
		if(version == null){
			System.setProperty(CLIENT_VERSION, Version.getVersion());
		}
		if (logicDbName == null)
			throw new NullPointerException("Database Set name can not be null");

		DalConfigure config = getDalConfigure();

		// Verify if it is existed
		config.getDatabaseSet(logicDbName);

		return new DalDirectClient(getDalConfigure(), logicDbName);
	}
	
	public static DalConfigure getDalConfigure() {
		DalConfigure config = configureRef.get();
		if (config == null)
			throw new IllegalStateException(
					"DalClientFactory has not been not initialized or initilization fail");
		return config;
	}

	/**
	 * Release All resource the Dal client used.
	 */
	public static void shutdownFactory() {
		logger.info("Start shutdown Dal Java Client Factory");
		MetricsLogger.shutdown();
		logger.info("Dal Java Client Factory is shutdown");
	}
}