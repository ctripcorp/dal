package com.ctrip.platform.dal.dao.client;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class DalHAManager {
	private static AtomicBoolean haEnabled = new AtomicBoolean(false);
	private static AtomicInteger retryCount = new AtomicInteger(3);
	
	public static void setHaEnabled(boolean enabled) {
		haEnabled.set(enabled);
	}
	
	public static boolean isHaEnabled() {
		return haEnabled.get() && !DalTransactionManager.isInTransaction();
	}
	
	public static void setRetryCount(int count){
		retryCount.set(count);
	}
	
	public static int getRetryCount(){
		return retryCount.get();
	}
	
	public static boolean isRetriable(DatabaseCategory category, SQLException e) {
		return haEnabled.get() && category.getRetriableCodeSet().contains(e.getErrorCode());
	}
	
	public static boolean isFailOverable(DatabaseCategory category, SQLException e) {
		return haEnabled.get() && category.getFailOverableCodeSet().contains(e.getErrorCode());
	}
}
