package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;

public class MarkdownManager {
	private static final int durations = 1000;
	private static Thread manager = null;
	
	private static List<ErrorDetector> detectors = new ArrayList<ErrorDetector>();
	private static ConcurrentLinkedQueue<ErrorContext> exqueue = new ConcurrentLinkedQueue<ErrorContext>();
	
	static{
		detectors.add(new TimeoutDetector());
		detectors.add(new LoginFailDetector());
		detectors.add(new NullObjectDetector());
		manager = new Thread(new CollectExceptionTask());
		manager.start();
	}
	
	public static boolean isMarkdown(String key){
		if(ConfigBeanFactory.getMarkdownConfigBean().isMarkdown()){
			return true;
		}
		Markdown item = ConfigBeanFactory.getMarkdownConfigBean().getMarkItem(key);
		if(item != null){
			if(!item.isAuto() || (System.currentTimeMillis() - item.getMarkdownTime()) <=
					ConfigBeanFactory.getMarkdownConfigBean().getAutoMarkupDelay() * 1000) //mark-down manually
				return true;
			
			if(!MarkupManager.isPass(key)){
				return true;
			}
		}			
		return false;
	}
	
	public static void shutdown(){
		manager.interrupt();
	}
	
	public static void detect(DalConnection conn, long start, Throwable e){
		long cost = System.currentTimeMillis() - start;
		if(conn != null && conn.getMeta() != null && e instanceof SQLException){
			ErrorContext ctx = new ErrorContext(conn.getMeta().getAllInOneKey(), 
					conn.getMeta().getDatabaseCategory(), cost, (SQLException)e);
			exqueue.add(ctx);
			MarkupManager.rollback(ctx);
		}	
	}
	
	private static class CollectExceptionTask implements Runnable{
		@Override
		public void run() {
			do{
				ErrorContext ctx = exqueue.poll();
				while(ctx != null){
					if(!ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(ctx.getName())){
						for (ErrorDetector mk : detectors) {
							mk.detect(ctx);
						}
					}
					ctx = exqueue.poll();
				}
				try {
					Thread.sleep(durations);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}while(true);
		}
	}
}
