package com.ctrip.platform.dal.dao.markdown;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MarkupManager {
	
	private static Map<String, Markup> markups = new ConcurrentHashMap<String, Markup>();
		
	/**
	 * Try to pass request which is marked down.
	 * @param key
	 * @return
	 */
	public static boolean isPass(String key){
		return getMarkup(key).isPass();
	}
	
	/**
	 * Mark up passed, but execute failed
	 * @param key
	 */
	public static void rollback(ErrorContext ctx){
		if(TimeoutDetector.isTimeOutHint(ctx) && 
				markups.containsKey(ctx.getName())){
			getMarkup(ctx.getName()).rollback();
		}
	}
	
	public static void reset(String key){
		if(markups.containsKey(key))
			markups.remove(key);
	}
	
	private static Markup getMarkup(String key){
		if(!markups.containsKey(key))
			markups.put(key, new Markup(key));
		return markups.get(key);
	}
	
	public static String getMarkupInfo(String key){
		if(markups.containsKey(key))
			return markups.get(key).toString();
		return "No Info";
	}
}
