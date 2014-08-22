package com.ctrip.platform.dal.dao.client;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class DalHAManager {
	private static AtomicBoolean haEnabled = new AtomicBoolean(false);
	
	private static Set<Integer> retriableCodes = new HashSet<>();
	private static Set<Integer> failOverableCodes = new HashSet<>();
	
	static{
		retriableCodes.add(-111);
		failOverableCodes.add(-111);
	}
	
	public static void setHaEnabled(boolean enabled) {
		haEnabled.set(enabled);
	}
	
	public static boolean isRetriable(SQLException e) {
		return haEnabled.get() && retriableCodes.contains(e.getErrorCode());
	}
	
	public static boolean isFailOverable(SQLException e) {
		return haEnabled.get() && failOverableCodes.contains(e.getErrorCode());
	}
}
