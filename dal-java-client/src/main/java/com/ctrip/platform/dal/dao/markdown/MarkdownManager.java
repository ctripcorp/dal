package com.ctrip.platform.dal.dao.markdown;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;

public class MarkdownManager {
	
	private static final int durations = 1000;
	private static Thread manager = null;
	
	private static List<AutoMarkdown> mkds = new ArrayList<AutoMarkdown>();
	private static ConcurrentLinkedQueue<MarkKey> exqueue = new ConcurrentLinkedQueue<MarkKey>();
	
	static{
		mkds.add(new TimeoutAutoMarkdown());
		mkds.add(new LoginAutoMarkdown());
		mkds.add(new NullObjectAutoMarkdown());
		manager = new Thread(new CollectExceptionTask());
		manager.start();
	}
	
	public static boolean isMarkdown(String key){
		return ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(key);
	}
	
	public static void shutdown(){
		manager.interrupt();
	}
	
	public static void collectException(DalConnection conn, Throwable e){
		if(!ConfigBeanFactory.getTimeoutMarkDownBean().isEnableTimeoutMarkDown())
			return;
		if(conn != null && conn.getMeta() != null)
			exqueue.add(new MarkKey(conn.getMeta().getAllInOneKey(), conn.getDatabaseProductName(), e));
	}
	
	private static class CollectExceptionTask implements Runnable{
		@Override
		public void run() {
			do{
				MarkKey kv = exqueue.poll();
				while(kv != null){
					for (AutoMarkdown mk : mkds) {
						mk.collectException(kv);
					}
					kv = exqueue.poll();
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
