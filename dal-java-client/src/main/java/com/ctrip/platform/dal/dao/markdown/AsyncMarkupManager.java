package com.ctrip.platform.dal.dao.markdown;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.ctrip.platform.dal.dao.configbeans.MarkdownConfigBean;

public class AsyncMarkupManager {
	private static final int durations = 1000;
	private static ScheduledExecutorService manager = null;
	private static ConcurrentHashMap<String, AysncMarkupPhase> status = new ConcurrentHashMap<>();
	
	static{
		manager = Executors.newSingleThreadScheduledExecutor(); 
		manager.scheduleAtFixedRate(new DetectMarkup(), durations,
				durations, TimeUnit.MICROSECONDS);
	}
	
	public static boolean isPass(String key){
		if(!status.containsKey(key))
			status.putIfAbsent(key, new AysncMarkupPhase(key));
		return status.get(key).isPassed();
	}
	
	public static AysncMarkupPhase getStatus(String key){
		return status.get(key);
	}
	
	public static void callback(ErrorContext ctx){
		if(TimeoutDetector.isTimeOutException(ctx)){
			status.get(ctx.getName()).rollback();
		}
	}
	
	public void shutdown(){
		manager.shutdown();
	}
	
	private static class DetectMarkup implements Runnable{
		@Override
		public void run() {
			try {
				for (AysncMarkupPhase pro : status.values()) {
					MarkdownConfigBean mcb = ConfigBeanFactory.getMarkdownConfigBean();
					
					//Roll back to the previous phase
					if(pro.getRollbackCount() > 0){
						if(pro.getPhaseIndex() >= 1){
							pro.setPhaseIndex(pro.getPhaseIndex() - 1);
						}
						pro.resetConter();
					}
					
					//Move to next phase
					if(pro.getPassed() >= mcb.getAutoMarkUpVolume() * mcb.getMarkUpSchedule()[pro.getPhaseIndex()])
					{
						int nextPhaseIndex = pro.getPhaseIndex() + 1;
						pro.resetConter();	
						if(pro.getPhaseIndex() <= mcb.getMarkUpSchedule().length - 1) {
							pro.setPhaseIndex(nextPhaseIndex);			
						}
					}
					//Marked up and reset the counter
					if(pro.getPhaseIndex() >= mcb.getMarkUpSchedule().length){
						ConfigBeanFactory.getMarkdownConfigBean().markup(pro.getName());
						pro.resetConter();
						pro.setPhaseIndex(0);
					}
				}
			}catch(Exception e){ }
		}
	}
}
