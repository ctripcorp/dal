package com.ctrip.platform.dal.dao.markdown;

import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.client.DalLogger;

public class MarkupManager {
	
	private static ConcurrentHashMap<String, MarkupProcedure> markups = new ConcurrentHashMap<String, MarkupProcedure>();
	/**
	 * Try to pass request which is marked down.
	 * @param key
	 * @return
	 */
	public static boolean isPass(String key, DalLogger logger){
		return getMarkup(key, logger).isPass();
	}
	
	/**
	 * Mark up passed, but execute failed
	 * @param key
	 */
	public static void rollback(ErrorContext ctx){
		if(TimeoutDetector.isTimeOutException(ctx) && 
				markups.containsKey(ctx.getName())){
			getMarkup(ctx.getName(), ctx.getLogger()).rollback();
		}
	}
	
	public static void reset(String key){
		markups.remove(key);
	}
	
	public static MarkupProcedure getMarkup(String key, DalLogger logger){
		if(!markups.containsKey(key))
			markups.putIfAbsent(key, new MarkupProcedure(key, logger));
		return markups.get(key);
	}
	
	public static String getMarkupInfo(String key){
		if(markups.containsKey(key))
			return markups.get(key).toString();
		return "No Info";
	}
}
