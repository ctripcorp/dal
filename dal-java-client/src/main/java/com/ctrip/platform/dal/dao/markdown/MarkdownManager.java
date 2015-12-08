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

import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.ctrip.platform.dal.dao.configbeans.MarkdownConfigBean;

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
			detectors.add(new TimeoutDetector());
			detectors.add(new LoginFailDetector());
			detectors.add(new NullObjectDetector());

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

	public static boolean isMarkdown(String key) {
		MarkdownConfigBean mcb = ConfigBeanFactory.getMarkdownConfigBean();
		if (mcb.isAppMarkDown()) {
			return true;
		}
		Markdown item = mcb.getMarkItem(key);
		if (item != null) {
			// Manual markdeddown can only be markup manually.
			if (!item.isAuto())
				return true;

			// Timeout is not reached
			if ((System.currentTimeMillis() - item.getMarkdownTime()) <= mcb.getAutoMarkUpDelay() * 1000)
				return true;

			if (!MarkupManager.isPass(key)) {
				return true;
			}
		}
		return false;
	}

	public static void detect(DalConnection conn, long start, Throwable e) {
		long cost = System.currentTimeMillis() - start;
		if (conn != null && conn.getMeta() != null && e instanceof SQLException) {
			ErrorContext ctx = new ErrorContext(
					conn.getMeta().getAllInOneKey(), conn.getMeta()
							.getDatabaseCategory(), cost, (SQLException) e);
			exqueue.add(ctx);
			MarkupManager.rollback(ctx);
		}
	}
	
	private static class CollectExceptionTask implements Runnable {
		@Override
		public void run() {
			try {
				ErrorContext ctx = exqueue.poll();
				while (ctx != null) {
					if (!ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(
							ctx.getName())) {
						for (ErrorDetector mk : detectorsRef.get()) {
							mk.detect(ctx);
						}
					}
					ctx = exqueue.poll();
				}
			} catch (Throwable e) { 
				e.printStackTrace();
			}
		}
	}
	
	public static String getDebugInfo(String key){
		return ((TimeoutDetector)detectorsRef.get().get(0)).toDebugInfo(key);
	}
}
