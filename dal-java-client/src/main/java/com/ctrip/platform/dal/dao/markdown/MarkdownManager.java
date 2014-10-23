package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;

public class MarkdownManager {
	private static final int durations = 1000;
	private static ScheduledExecutorService manager = null;

	private static List<ErrorDetector> detectors = new ArrayList<ErrorDetector>();
	private static ConcurrentLinkedQueue<ErrorContext> exqueue = new ConcurrentLinkedQueue<ErrorContext>();

	static {
		manager = Executors.newSingleThreadScheduledExecutor();
		detectors.add(new TimeoutDetector());
		detectors.add(new LoginFailDetector());
		detectors.add(new NullObjectDetector());

		manager.scheduleAtFixedRate(new CollectExceptionTask(), durations,
				durations, TimeUnit.MICROSECONDS);
	}

	public static boolean isMarkdown(String key) {
		if (ConfigBeanFactory.getMarkdownConfigBean().isMarkdown()) {
			return true;
		}
		Markdown item = ConfigBeanFactory.getMarkdownConfigBean().getMarkItem(
				key);
		if (item != null) {
			// Manual markdeddown can only be markup manually.
			if (!item.isAuto())
				return true;

			// Timeout is not reached
			if ((System.currentTimeMillis() - item.getMarkdownTime()) <= ConfigBeanFactory
					.getMarkdownConfigBean().getAutoMarkupDelay() * 1000)
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
						for (ErrorDetector mk : detectors) {
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
		return ((TimeoutDetector)detectors.get(0)).toDebugInfo(key);
	}
}
