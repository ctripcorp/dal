package com.ctrip.platform.dal.daogen.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseSetUtils {
	private static Map<String, String> databaseSetDBNameCache = null;
	
	static {
		databaseSetDBNameCache = new ConcurrentHashMap<String, String>();
	}
	
	public static String getDBName(String databaseSetName){
		if(databaseSetDBNameCache.containsKey(databaseSetName)){
			return databaseSetDBNameCache.get(databaseSetName);
		} else {
			try{
				String dbName = SpringBeanGetter.getDaoOfDatabaseSet()
						.getMasterDatabaseSetEntryByDatabaseSetName(databaseSetName).getConnectionString();
				if(null != dbName){
					databaseSetDBNameCache.put(databaseSetName, dbName);
				}
				return dbName;
			}catch(Exception e){
				return databaseSetName;
			}
		}
	}
}
