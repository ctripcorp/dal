package com.ctrip.platform.dal.dao.markdown;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ctrip.platform.dal.dao.client.DalConnection;

public class MarkdownManager {
	
	private static final int durations = 1000;
	private static Thread manager = null;
	
	private static List<AutoMarkdown> mkds = new ArrayList<AutoMarkdown>();
	private static ConcurrentLinkedQueue<Mark> exqueue = new ConcurrentLinkedQueue<Mark>();
	
	//Synchronized the final mark down status
	private static ManualMarkdown maunal = new ManualMarkdown(); 
	
	static{
		mkds.add(new TimeoutAutoMarkdown());
		mkds.add(new LoginAutoMarkdown());
		mkds.add(new NullObjectAutoMarkdown());
		manager = new Thread(new CollectExceptionTask());
		manager.start();
	}
	
	public static void markup(String key){
		for (AutoMarkdown mk : mkds) {
			mk.markup(key);
		}
	}
	
	public static boolean isMarkdown(String key){
		for (AutoMarkdown mk : mkds) {
			if(mk.isMarkdown(key)){
				maunal.markown(key);
			}			
		}
		return maunal.isMarkdown(key);
	}
	
	public static void shutdown(){
		manager.interrupt();
	}
	
	public static void collectException(DalConnection conn, Throwable e){
		exqueue.add(new Mark(conn.getMeta().getAllInOneKey(), conn.getDatabaseProductName(), e));
	}
	
	private static class CollectExceptionTask implements Runnable{
		@Override
		public void run() {
			do{
				Mark kv = exqueue.poll();
				while(kv != null){
					for (AutoMarkdown mk : mkds) {
						mk.collectException(kv);
					}
					kv = exqueue.poll();
				}
				try {
					Thread.sleep(durations);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}while(true);
		}
	}
}
