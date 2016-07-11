package com.ctrip.platform.dal.dao.status;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.ctrip.platform.dal.dao.markdown.MarkdownManager;

/**
 * TODO add logic DB level markdown control
 * @author jhhe
 *
 */
public class DalStatusManager {
	private static final String GLOBAL_CONFIG_DOMAIN_PREFIX = "com.ctrip.dal.client";
	private static final String TYPE = "type";
	private static final String LOGIC_DB_CONFIG_DOMAIN_PREFIX = "com.ctrip.dal.client.logicDb";
	private static final String DATASOURCE_CONFIG_DOMAIN_PREFIX = "com.ctrip.dal.client.datasource";
	
	private static AtomicBoolean initialized = new AtomicBoolean(false);
	private static AtomicReference<TimeoutMarkdown> timeoutMarkDownRef = new AtomicReference<>();
	private static AtomicReference<HAStatus> haStatusRef = new AtomicReference<>();
	private static AtomicReference<MarkdownStatus> markdownStatusRef = new AtomicReference<>();
	private static Map<String, DataSourceStatus> dataSources = new ConcurrentHashMap<>();
	
	public static synchronized void initialize(Set<String> dbNames) {
		try {
			if(initialized.get() == true)
				return;
			
			registerGlobal();
			registerDataSources(dbNames);
			MarkdownManager.init();
			
			initialized.set(true);;
		} catch (Exception e) {
			e.printStackTrace();
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

	private static void registerDataSources(Set<String> dbNames) throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		for(String name: dbNames) {
			DataSourceStatus status = new DataSourceStatus(name);
			mbs.registerMBean(status, new ObjectName(DATASOURCE_CONFIG_DOMAIN_PREFIX, TYPE, name));
			dataSources.put(name, status);
		}
	}
	
	public static synchronized void shutdown() throws Exception {
		MarkdownManager.shutdown();
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		mbs.unregisterMBean(getGlobalName(HAStatus.class));
		mbs.unregisterMBean(getGlobalName(TimeoutMarkdown.class));
		mbs.unregisterMBean(getGlobalName(MarkdownStatus.class));
		for(String name: dataSources.keySet())
			mbs.unregisterMBean(new ObjectName(DATASOURCE_CONFIG_DOMAIN_PREFIX, TYPE, name));
		dataSources.clear();
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
	
	public static DataSourceStatus getDataSourceStatus(String dbName) {
		return dataSources.get(dbName);
	}
}
