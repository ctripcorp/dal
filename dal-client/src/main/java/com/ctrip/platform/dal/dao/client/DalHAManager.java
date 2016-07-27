package com.ctrip.platform.dal.dao.client;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class DalHAManager {
	private static AtomicBoolean haEnabled = new AtomicBoolean(false);
	private static AtomicInteger retryCount = new AtomicInteger(3);
	
	private static Map<DatabaseCategory, Set<Integer>> retriableCodes = new HashMap<DatabaseCategory, Set<Integer>>();
	private static Map<DatabaseCategory, Set<Integer>> failOverableCodes = new HashMap<DatabaseCategory, Set<Integer>>();
	
	static{
		retriableCodes.put(DatabaseCategory.SqlServer, new HashSet<Integer>());
		retriableCodes.get(DatabaseCategory.SqlServer).add(-2);
		retriableCodes.get(DatabaseCategory.SqlServer).add(233);
		retriableCodes.get(DatabaseCategory.SqlServer).add(844);
		retriableCodes.get(DatabaseCategory.SqlServer).add(845);
		retriableCodes.get(DatabaseCategory.SqlServer).add(846);
		retriableCodes.get(DatabaseCategory.SqlServer).add(847);
		retriableCodes.get(DatabaseCategory.SqlServer).add(1421);
		
		retriableCodes.put(DatabaseCategory.MySql, new HashSet<Integer>());
		retriableCodes.get(DatabaseCategory.MySql).add(1043);
		retriableCodes.get(DatabaseCategory.MySql).add(1159);
		retriableCodes.get(DatabaseCategory.MySql).add(1161);
		
		failOverableCodes.put(DatabaseCategory.SqlServer, new HashSet<Integer>());
		failOverableCodes.get(DatabaseCategory.SqlServer).add(2);
		failOverableCodes.get(DatabaseCategory.SqlServer).add(53);
		failOverableCodes.get(DatabaseCategory.SqlServer).add(701);
		failOverableCodes.get(DatabaseCategory.SqlServer).add(802);
		failOverableCodes.get(DatabaseCategory.SqlServer).add(945);
		failOverableCodes.get(DatabaseCategory.SqlServer).add(1204);
		failOverableCodes.get(DatabaseCategory.SqlServer).add(1222);
		
		failOverableCodes.put(DatabaseCategory.MySql, new HashSet<Integer>());
		failOverableCodes.get(DatabaseCategory.MySql).add(1021);
		failOverableCodes.get(DatabaseCategory.MySql).add(1037);
		failOverableCodes.get(DatabaseCategory.MySql).add(1038);
		failOverableCodes.get(DatabaseCategory.MySql).add(1039);
		failOverableCodes.get(DatabaseCategory.MySql).add(1040);
		failOverableCodes.get(DatabaseCategory.MySql).add(1041);
		failOverableCodes.get(DatabaseCategory.MySql).add(1154);
		failOverableCodes.get(DatabaseCategory.MySql).add(1158);
		failOverableCodes.get(DatabaseCategory.MySql).add(1160);
		failOverableCodes.get(DatabaseCategory.MySql).add(1189);
		failOverableCodes.get(DatabaseCategory.MySql).add(1190);
		failOverableCodes.get(DatabaseCategory.MySql).add(1205);
		failOverableCodes.get(DatabaseCategory.MySql).add(1218);
		failOverableCodes.get(DatabaseCategory.MySql).add(1219);
		failOverableCodes.get(DatabaseCategory.MySql).add(1220);
	}
	
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
		return haEnabled.get() && retriableCodes.get(category).contains(e.getErrorCode());
	}
	
	public static boolean isFailOverable(DatabaseCategory category, SQLException e) {
		return haEnabled.get() && failOverableCodes.get(category).contains(e.getErrorCode());
	}
}
