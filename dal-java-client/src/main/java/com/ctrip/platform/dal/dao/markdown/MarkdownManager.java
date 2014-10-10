package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;

public class MarkdownManager {
	//private static Logger logger = LoggerFactory.getLogger(MarkdownManager.class);
	private static final int durations = 1000;
	private static Thread manager = null;
	
	private static List<AutoMarkdown> mkds = new ArrayList<AutoMarkdown>();
	private static ConcurrentLinkedQueue<MarkContext> exqueue = new ConcurrentLinkedQueue<MarkContext>();
	
	static{
		mkds.add(new TimeoutAutoMarkdown());
		mkds.add(new LoginAutoMarkdown());
		mkds.add(new NullObjectAutoMarkdown());
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
					ConfigBeanFactory.getMarkdownConfigBean().getAutoMarkupDelay() * 60 * 1000) //mark-down manually
				return true;
			
			if(MarkupManager.isMarkup(key)){
				ConfigBeanFactory.getMarkdownConfigBean().markup(key);
				//logger.info("########################Mark-Up########################");
				return false;
			}
			
			if(MarkupManager.isPass(key)){
				//logger.info("########################Passed########################");
				return false;
			}
		}			
		return false;
	}
	
	public static void shutdown(){
		manager.interrupt();
	}
	
	public static void collectException(DalConnection conn, long cost, Throwable e){
		if(conn != null && conn.getMeta() != null && e instanceof SQLException){
			MarkContext ctx = new MarkContext(conn.getMeta().getAllInOneKey(), 
					conn.getDatabaseProductName(), cost, (SQLException)e);
			exqueue.add(ctx);
			MarkupManager.rollback(ctx);
		}	
	}
	
	private static class CollectExceptionTask implements Runnable{
		@Override
		public void run() {
			do{
				MarkContext kv = exqueue.poll();
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
