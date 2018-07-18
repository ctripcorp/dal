package com.ctrip.platform.dal.dao.strategy;

import java.util.Map;

/**
 * This strategy locate both db and table shard by mod shard value.
 * The checking sequency is same for both DB and table shard:
 * 
 * Shard id
 * Shard value
 * Shard column values
 * Parameters
 * Entity fields
 * 
 * @author jhhe
 *
 */
public class ShardColModShardStrategy extends AbstractColumnShardStrategy {
	/**
	 * Key used to declared mod for locating DB shard.
	 */
	public static final String MOD = "mod";

	/**
	 * Key used to declared mod for locating table shard.
	 */
	public static final String TABLE_MOD = "tableMod";

	private Integer mod;

	private Integer tableMod;
	
	/**
	 * columns are separated by ','
	 * @Override
	 */
	public void initialize(Map<String, String> settings) {
	    super.initialize(settings);
		
		if(settings.containsKey(MOD)) {
			mod = Integer.parseInt(settings.get(MOD));
		}
		
		if(settings.containsKey(TABLE_MOD)) {
			tableMod = Integer.parseInt(settings.get(TABLE_MOD));
		}
	}
	
    @Override
    public String calculateDbShard(Object value) {
        Long id = getLongValue(value);
        return String.valueOf(id%mod);
    }

    @Override
    public String calculateTableShard(String tableName, Object value) {
        Long id = getLongValue(value);
        return String.valueOf(id%tableMod);
    }

    private Long getLongValue(Object value) {
		if(value == null)
			return null;
		
		if(value instanceof Long)
			return (Long)value;
		
		if(value instanceof Number)
			return ((Number)value).longValue();
		
		if(value instanceof String)
			return new Long((String)value);
		
		throw new RuntimeException(String.format("Shard value: %s can not be recoganized as int value", value.toString()));
	}
}
