package com.ctrip.platform.dal.dao.status;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;

/**
 * TODO add logic DB level markdown control
 * @author jhhe
 *
 */
public class DalStatusManager {
	private static final String GLOBAL_CONFIG_DOMAIN_PREFIX = "com.ctrip.dal.client";
	private static final String TYPE = "type";
	private static final String LOGIC_DB_CONFIG_DOMAIN_PREFIX = "com.ctrip.dal.client.DatabaseSet";
	private static final String DATASOURCE_CONFIG_DOMAIN_PREFIX = "com.ctrip.dal.client.DataSource";
	
	private static AtomicBoolean initialized = new AtomicBoolean(false);
	private static AtomicReference<TimeoutMarkdown> timeoutMarkDownRef = new AtomicReference<>();
	private static AtomicReference<HAStatus> haStatusRef = new AtomicReference<>();
	private static AtomicReference<MarkdownStatus> markdownStatusRef = new AtomicReference<>();
	private static Map<String, DatabaseSetStatus> logicDbs = new ConcurrentHashMap<>();
	private static Map<String, DataSourceStatus> dataSources = new ConcurrentHashMap<>();
	
	public static void initialize(DalConfigure config) throws Exception {
		if(initialized.get() == true)
			return;
		
		synchronized (DalStatusManager.class) {
			if(initialized.get() == true)
				return;

			registerGlobal();
			registerDatabaseSets(config.getDatabaseSetNames());
			registerDataSources(config.getDataSourceNames());
			MarkdownManager.init();
			
			initialized.set(true);;
		}
	}
	
	private static void registerGlobal() throws Exception{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		haStatusRef.set(new HAStatus());
		mbs.registerMBean(haStatusRef.get(), getGlobalName(HAStatus.class));
		
		timeoutMarkDownRef.set(new TimeoutMarkdown());
		mbs.registerMBean(timeoutMarkDownRef.get(), getGlobalName(TimeoutMarkdown.class));
		
		markdownStatusRef.set(new MarkdownStatus());
		mbs.registerMBean(markdownStatusRef.get(), getGlobalName(MarkdownStatus.class));
	}

	private static void registerDatabaseSets(Set<String> logicDbNames) throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		for(String name: logicDbNames) {
			DatabaseSetStatus status = new DatabaseSetStatus(name);
			mbs.registerMBean(status, new ObjectName(LOGIC_DB_CONFIG_DOMAIN_PREFIX, TYPE, name));
			logicDbs.put(name, status);
		}
	}

	private static void registerDataSources(Set<String> datasourceNames) throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		for(String name: datasourceNames) {
			DataSourceStatus status = new DataSourceStatus(name);
			mbs.registerMBean(status, new ObjectName(DATASOURCE_CONFIG_DOMAIN_PREFIX, TYPE, name));
			dataSources.put(name, status);
		}
	}
	
	public static synchronized void shutdown() throws Exception {
		if(initialized.get() == false)
			return;
		
		MarkdownManager.shutdown();
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		mbs.unregisterMBean(getGlobalName(HAStatus.class));
		mbs.unregisterMBean(getGlobalName(TimeoutMarkdown.class));
		mbs.unregisterMBean(getGlobalName(MarkdownStatus.class));
		
		for(String name: dataSources.keySet())
			mbs.unregisterMBean(new ObjectName(DATASOURCE_CONFIG_DOMAIN_PREFIX, TYPE, name));
		dataSources.clear();
		
		for(String name: logicDbs.keySet())
			mbs.unregisterMBean(new ObjectName(LOGIC_DB_CONFIG_DOMAIN_PREFIX, TYPE, name));
		logicDbs.clear();
		
		initialized.set(false);
	}
	
	private static ObjectName getGlobalName(Class clazz) throws Exception {
		return new ObjectName(GLOBAL_CONFIG_DOMAIN_PREFIX, TYPE, clazz.getSimpleName());
	}

	public static TimeoutMarkdown getTimeoutMarkdown() {
		return timeoutMarkDownRef.get();
	}

	public static HAStatus getHaStatus() {
		return haStatusRef.get();
	}

	public static MarkdownStatus getMarkdownStatus() {
		return markdownStatusRef.get();
	}
	
	public static DatabaseSetStatus getDatabaseSetStatus(String dbName) {
		return logicDbs.get(dbName);
	}
	
	public static DataSourceStatus getDataSourceStatus(String dbName) {
		return dataSources.get(dbName);
	}
}
