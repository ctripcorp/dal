package com.ctrip.platform.dal.dao.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;

/**
 * New strategy that support same function that current hotel DAL team provided.
 * Add cache for DB timeout. Key is DB name, value is timeout. timeout is provided by app user. when DB get updated, timeout will be reset.
 * For new read request, it will check timeout first. if not timeout, read master, if timeout read slave.
 * @author jhhe
 * @deprecated not used for now
 */
public class SmartReadRouteStrategy implements DalShardingStrategy {
	public static final String REPL_SLA = "replSla";
	public static final int DEEFAULT_THRESHOLD = 5;
	private final ConcurrentHashMap<String, Integer> dbTimeoutMap = new ConcurrentHashMap<String, Integer>();
	private int threshold;
	@Override
	public void initialize(Map<String, String> settings) {
		String valueStr = settings.get(REPL_SLA);
		if(valueStr == null)
			threshold = DEEFAULT_THRESHOLD;
		else {
			try {
				threshold = Integer.parseInt(valueStr);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				threshold = DEEFAULT_THRESHOLD;
			}
		}
	}


	@Override
	public boolean isMaster(DalConfigure configure, String logicDbName,
			DalHints hints) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String locateDbShard(DalConfigure configure, String logicDbName,
			DalHints hints) {
		String shard = null;
		DalEventEnum operation = (DalEventEnum)hints.get(DalHintEnum.operation);
		DatabaseSet dbSet = configure.getDatabaseSet(logicDbName);
		
		if(operation == DalEventEnum.QUERY) {
			Integer lastUpdateTime = dbTimeoutMap.get(logicDbName);
			// No update from server started
			if(lastUpdateTime == null) {
				
//				return dbSet.getSlaveDbs(dbSet.getAllShards().iterator().next());
			}
		} else {
			
		}
			
//		
//		if(shard != null) {
//			Set<String> shards = new HashSet<String>();
//			shards.add(shard);
//			return shards;
//		}
//		
//		return (Set<String>)hints.get(DalHintEnum.shards);
		return null;
	}

	@Override
	public String locateTableShard(DalConfigure configure, String logicDbName, String tabelName,
			DalHints hints) {
		return hints.getString(DalHintEnum.tableShard);
	}


	@Override
	public boolean isShardingByDb() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isShardingByTable() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isShardingEnable(String tableName) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public String getTableShardSeparator() {
		// TODO Auto-generated method stub
		return null;
	}
}
