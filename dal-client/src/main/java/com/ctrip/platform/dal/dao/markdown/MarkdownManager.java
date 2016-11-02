package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.dao.status.DataSourceStatus;
import com.ctrip.platform.dal.dao.status.MarkdownStatus;

public class MarkdownManager {
	private static Logger logger = LoggerFactory.getLogger(MarkdownManager.class);
	private static final int durations = 1000;
	private static AtomicReference<ScheduledExecutorService> managerRef = new AtomicReference<>();

	private static AtomicReference<List<ErrorDetector>> detectorsRef = new AtomicReference<>();
	private static ConcurrentLinkedQueue<ErrorContext> exqueue = new ConcurrentLinkedQueue<ErrorContext>();

	public static void init() {
		if(managerRef.get() !=null)
			return;
		
		synchronized (MarkdownManager.class) {
			if(managerRef.get() !=null)
				return;
			
			ArrayList<ErrorDetector> detectors = new ArrayList<ErrorDetector>();
			// We currently only have Timeout case
			detectors.add(new TimeoutDetector());

			detectorsRef.set(detectors);
			ScheduledExecutorService manager = Executors.newSingleThreadScheduledExecutor();
			manager.scheduleAtFixedRate(new CollectExceptionTask(), durations,
					durations, TimeUnit.MICROSECONDS);

			managerRef.set(manager);
		}
	}

	public static void shutdown(){
		if(managerRef.get() ==null)
			return;
		
		synchronized (MarkdownManager.class) {
			if(managerRef.get() ==null)
				return;
			
			managerRef.get().shutdownNow();
			managerRef.set(null);
			logger.info("Markdown Manager has been destoryed");
		}
	}
	
	public static void autoMarkdown(MarkDownInfo info) {
		DalStatusManager.getDataSourceStatus(info.getDbKey()).setAutoMarkdown(true);

		DalClientFactory.getDalLogger().info(String.format("Database %s has been marked down automatically", info.getDbKey()));
		DalClientFactory.getDalLogger().markdown(info);
	}

	public static void autoMarkup(MarkupInfo info) {
		DalStatusManager.getDataSourceStatus(info.getDbKey()).setAutoMarkdown(false);
		
		DalClientFactory.getDalLogger().info(String.format("Database %s has been marked up automatically", info.getDbKey()));
		DalClientFactory.getDalLogger().markup(info);
	}

	public static boolean isMarkdown(String key) {
		MarkdownStatus mcb = DalStatusManager.getMarkdownStatus();
		if (mcb.isAppMarkdown())
			return true;
		
		DataSourceStatus item = DalStatusManager.getDataSourceStatus(key);

		// Manual markdown can only be markup manually.
		if (item.isManualMarkdown())
			return true;

		if(!mcb.isEnableAutoMarkdown())
			return false;
		
		if (!item.isAutoMarkdown())
			return false;

		// Timeout is not reached
		if ((System.currentTimeMillis() - item.getAutoMarkdownTime().getTime()) <= mcb.getAutoMarkupDelay() * 1000)
			return true;
	
		autoMarkup(new MarkupInfo(key, Version.getVersion(), 0));

		return false;
	}
	
	/**
	 * Clear all auto markdown
	 */
	public static void resetAutoMarkdowns() {
		for(String dbName: DalClientFactory.getDalConfigure().getDataSourceNames())
			DalStatusManager.getDataSourceStatus(dbName).setAutoMarkdown(false);
	}

	public static void detect(DalConnection conn, long start, Throwable e) {
		if (conn == null || conn.getMeta() == null || !(e instanceof SQLException))
			return;
			
		ErrorContext ctx = new ErrorContext(
				conn.getMeta().getDataBaseKeyName(), 
				conn.getMeta().getDatabaseCategory(), 
				System.currentTimeMillis() - start, 
				(SQLException) e);
		
		exqueue.add(ctx);
	}
	
	private static class CollectExceptionTask implements Runnable {
		@Override
		public void run() {
			try {
				ErrorContext ctx = exqueue.poll();
				while (ctx != null) {
					if(DalStatusManager.getMarkdownStatus().isEnableAutoMarkdown()) {
						if (!isMarkdown(ctx.getName())) {
							for (ErrorDetector mk : detectorsRef.get()) {
								mk.detect(ctx);
							}
						}
					}
					ctx = exqueue.poll();
				}
			} catch (Throwable e) { 
				e.printStackTrace();
			}
		}
	}
}