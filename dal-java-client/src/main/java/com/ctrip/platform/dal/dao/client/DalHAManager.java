package com.ctrip.platform.dal.dao.client;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DalHAManager {
	private static AtomicBoolean haEnabled = new AtomicBoolean(false);
	private static AtomicInteger retryCount = new AtomicInteger(3);
	
	private static Set<Integer> retriableCodes = new HashSet<>();
	private static Set<Integer> failOverableCodes = new HashSet<>();
	
	static{
		retriableCodes.add(1043);
		failOverableCodes.add(1021);
	}
	
	public static void setHaEnabled(boolean enabled) {
		haEnabled.set(enabled);
	}
	
	public static boolean isHaEnabled() {
		return haEnabled.get();
	}
	
	public static void setRetryCount(int count){
		retryCount.set(count);
	}
	
	public static int getRetryCount(){
		return retryCount.get();
	}
	
	public static boolean isRetriable(SQLException e) {
		return haEnabled.get() && retriableCodes.contains(e.getErrorCode());
	}
	
	public static boolean isFailOverable(SQLException e) {
		return haEnabled.get() && failOverableCodes.contains(e.getErrorCode());
	}
}
