package com.ctrip.platform.dal.dao;

import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.common.db.DruidDataSourceWrapper;
import com.ctrip.platform.dal.dao.client.DalDirectClient;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;
import com.ctrip.platform.dal.sql.logging.MetricsLogger;

public class DalClientFactory {
	private static Logger logger = LoggerFactory.getLogger(DalClientFactory.class);
	private static AtomicReference<DruidDataSourceWrapper> connPool = new AtomicReference<DruidDataSourceWrapper>();

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
		if (configureRef.get() != null) {
			logReInitialized();
			return;
		}

		synchronized (DalClientFactory.class) {
			if (configureRef.get() != null) {
				logReInitialized();
				return;
			}
			
			configureRef.set(DalConfigureFactory.load());
			logInitialized();
		}
	}
	
	/**
	 * Initialize for DB All In One client. Load Dal.config from give path
	 * 
	 * @param path
	 *            Dal.Config file path
	 * @throws Exception
	 */
	public static void initClientFactory(String path) throws Exception {
		if (configureRef.get() != null) {
			logReInitialized();
			return;
		}

		synchronized (DalClientFactory.class) {
			if (configureRef.get() != null) {
				logReInitialized();
				return;
			}
			configureRef.set(DalConfigureFactory.load(path));
			logInitialized(path);
		}
	}

	/**
	 * Actively initialize connection pools for all the logic db in the
	 * Dal.config
	 */
	public static void warmUpConnections() {
		DalConfigure config = configureRef.get();
		if (config == null)
			return;
		config.warmUpConnections();
	}

	public static DalClient getClient(String logicDbName) {
		if (logicDbName == null)
			throw new NullPointerException("Database Set name can not be null");

		DalConfigure config = configureRef.get();
		if (config == null)
			throw new IllegalStateException(
					"DalClientFactory has not been not initialized or initilization fail");

		if (config.getDatabaseSet(logicDbName) == null)
			throw new IllegalArgumentException(
					"Can not find definition for Database Set "
							+ logicDbName
							+ ". Please check spelling or define it in Dal.config");

		return new DalDirectClient(configureRef.get(), logicDbName);
	}

	/**
	 * Release All resource the Dal client used.
	 */
	public static void shutdownFactory() {
		logger.info("Start shutdown Dal Java Client Factory");
		MetricsLogger.shutdown();
		logger.info("Dal Java Client Factory is shutdown");
	}
	
	private static void logInitialized() {
		logger.info("Successfully initialized Dal Java Client Factory");
	}

	private static void logInitialized(String path) {
		logger.info("Successfully initialized Dal Java Client Factory with " + path);
	}
	
	private static void logReInitialized() {
		logger.info("Dal Java Client Factory is already initialized.");
	}
}