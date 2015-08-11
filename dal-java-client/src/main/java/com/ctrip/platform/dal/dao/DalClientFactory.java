package com.ctrip.platform.dal.dao;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.dao.client.DalDirectClient;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.NullLogger;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;
import com.ctrip.platform.dal.dao.task.DalRequestExecutor;
import com.ctrip.platform.dal.dao.task.DalTaskFactory;
import com.ctrip.platform.dal.dao.task.DefaultTaskFactory;

public class DalClientFactory {
	private static Logger logger = LoggerFactory.getLogger(Version.getLoggerName());

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

	public static DalLogger getDalLogger() {
		DalConfigure config = configureRef.get();
		return config == null ? new NullLogger() : config.getDalLogger();
	}
	
	public static DalTaskFactory getTaskFactory() {
		DalConfigure config = configureRef.get();
		return config == null ? new DefaultTaskFactory() : config.getFacory();
	}
	
	/**
	 * Release All resource the Dal client used.
	 */
	public static void shutdownFactory() {
		logger.info("Start shutdown Dal Java Client Factory");
		getDalLogger().shutdown();
		DalRequestExecutor.shutdown();
		logger.info("Dal Java Client Factory is shutdown");
	}
}