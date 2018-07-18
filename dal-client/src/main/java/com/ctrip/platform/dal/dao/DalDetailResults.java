package com.ctrip.platform.dal.dao;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * To store the DAO result by shard id. The results are organized by DB shard id then Table shard id.
 * @author jhhe
 *
 * @param <T>
 */
public class DalDetailResults<T> {
	public static final String DEFAULT = "default";
	
	/**
	 * This contains the detail generated keys by DB shard and Table Shard. The outside map is keyed by DB shard id.
	 * The inner Map is keyed by table shard id.
	 */
	private final Map<String, Map<String, T>> detailResults = new LinkedHashMap<>();
	
	public void clear() {
		detailResults.clear();
	}
	
	public void addResult(String shardId, String tableShardId, T result) {
		// Then by DB and Table shard ID
		shardId = shardId == null? DEFAULT : shardId;
		tableShardId = tableShardId == null? DEFAULT : tableShardId;
		
		Map<String, T> resultByDbShard = detailResults.get(shardId);
		if(resultByDbShard == null) {
			resultByDbShard = new LinkedHashMap<>();
			detailResults.put(shardId, resultByDbShard);
		}
		
		resultByDbShard.put(tableShardId, result);
	}
	
	/**
	 * Only used for sharding by Db
	 * @param shardId
	 * @return
	 */
	public T getResultByDb(String shardId) {
		return getResult(shardId, null);
	}
	
	/**
	 * Only used for sharding by Tabel
	 * @param tableShardId
	 * @return
	 */
	public T getResultBytable(String tableShardId) {
		return getResult(null, tableShardId);
	}
	
	/**
	 * Used for sharding by Db+Table
	 * @param shardId
	 * @param tableShardId
	 * @return
	 */
	public T getResult(String shardId, String tableShardId) {
		shardId = shardId == null? DEFAULT : shardId;
		tableShardId = tableShardId == null? DEFAULT : tableShardId;
		
		return detailResults.get(shardId).get(tableShardId);
	}
}
