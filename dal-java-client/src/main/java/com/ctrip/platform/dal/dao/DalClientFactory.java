package com.ctrip.platform.dal.dao;

import java.util.concurrent.atomic.AtomicReference;

import com.ctrip.platform.dal.common.db.ConnectionPropertyReader;
import com.ctrip.platform.dal.common.db.DasConfigureReader;
import com.ctrip.platform.dal.common.db.DruidDataSourceWrapper;
import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.dao.client.DalDirectClient;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;
import com.ctrip.platform.dal.dao.logging.MetricsLogger;

public class DalClientFactory {
	private static AtomicReference<DruidDataSourceWrapper> connPool = new AtomicReference<DruidDataSourceWrapper>();

	private static final String DAL_CONFIG = "Dal.config";
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
		if (configureRef.get() != null)
			return;

		synchronized (DalClientFactory.class) {
			if (configureRef.get() != null)
				return;
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			if (classLoader == null) {
				classLoader = DalClientFactory.class.getClassLoader();
			}

			configureRef.set(DalConfigureFactory.load(classLoader
					.getResource(DAL_CONFIG)));
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
		if (configureRef.get() != null)
			return;

		synchronized (DalClientFactory.class) {
			if (configureRef.get() != null)
				return;
			configureRef.set(DalConfigureFactory.load(path));
		}
	}

	/**
	 * Initialize from local connections.properties in classpath. For local
	 * testing.
	 * 
	 * @throws Exception
	 */
	public static void initPrivateFactory() throws Exception {
		// DasConfigureReader reader = new ConfigureServiceReader(new
		// DasConfigureService("localhost:8080", new
		// File(DEFAULT_SNAPSHOT_PATH)));
		ConnectionPropertyReader reader = new ConnectionPropertyReader();
		Configuration.addResource(reader.DEFAULT_CONF_NAME);
		String[] logicDbNames = reader.getLogicDbNames();
		try {
			DalClientFactory.initDirectClientFactory(reader, logicDbNames);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Cannot initilize DB");
			System.exit(0);
		}
	}

	/**
	 * For advanced direct client initialization.
	 * 
	 * @deprecated
	 * @param reader
	 * @param logicDbNames
	 * @throws Exception
	 */
	public static void initDirectClientFactory(DasConfigureReader reader,
			String... logicDbNames) throws Exception {
		// TODO FIXIT should allow initialize logic Db for several times
		if (connPool.get() != null)
			return;
		synchronized (DalClientFactory.class) {
			if (connPool.get() != null)
				return;
			connPool.set(new DruidDataSourceWrapper(reader, logicDbNames));
		}
	}

	/**
	 * For advanced indirect client initialization.
	 * 
	 * @deprecated
	 * @param reader
	 * @param logicDbNames
	 * @throws Exception
	 */
	public static void initDasClientFactory(DasConfigureReader reader,
			String... logicDbNames) throws Exception {
		// TODO to support
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

		if (configureRef.get() == null)
			return new DalDirectClient(connPool.get(), logicDbName);

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
		MetricsLogger.shutdown();
	}
}
