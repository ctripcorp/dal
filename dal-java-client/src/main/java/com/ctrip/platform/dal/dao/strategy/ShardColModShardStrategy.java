package com.ctrip.platform.dal.dao.strategy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

public class ShardColModShardStrategy extends AbstractRWSeparationStrategy implements DalShardingStrategy {
	public static final String COLUMNS = "columns";
	public static final String MOD = "mod";
	
	public static final String SHARDED_TABLES = "shardedTables";
	public static final String TABLE_COLUMNS = "tableColumns";
	public static final String TABLE_MOD = "tableMod";
	public static final String SEPARATOR = "separator";

	private String[] columns;
	private Integer mod;

	private Set<String> shardedTables = new HashSet<String>();
	private String[] tableColumns;
	private Integer tableMod;
	private String separator;
	
	/**
	 * columns are separated by ','
	 * @Override
	 */
	public void initialize(Map<String, String> settings) {
		if(settings.containsKey(COLUMNS)) {
			columns = settings.get(COLUMNS).split(",");
		}
		
		if(settings.containsKey(MOD)) {
			mod = Integer.parseInt(settings.get(MOD));
		}
		
		if(settings.containsKey(SHARDED_TABLES)) {
			String[] tables = settings.get(SHARDED_TABLES).split(",");
			for(String table: tables)
				shardedTables.add(table);
		}
		
		if(settings.containsKey(TABLE_COLUMNS)) {
			tableColumns = settings.get(TABLE_COLUMNS).split(",");
		}
		
		if(settings.containsKey(TABLE_MOD)) {
			tableMod = Integer.parseInt(settings.get(TABLE_MOD));
		}
		
		if(settings.containsKey(SEPARATOR)) {
			separator = settings.get(SEPARATOR);
		}
	}

	@Override
	public boolean isShardingByDb() {
		return columns != null;
	}

	/**
	 * This method will locate shard id by first referring shardValue, then parameter and finally shardCol defined in hints.
	 * If shard can be decided by any of these values, it will return immediately with the found id.
	 */
	public String locateDbShard(DalConfigure configure, String logicDbName,
			DalHints hints) {
		if(!isShardingByDb())
			throw new RuntimeException(String.format("Logic Db %s is not configured to be shard by database", logicDbName));
		
		String shard = hints.getShardId();
		if(shard != null)
			return shard;
		
		// Shard value take the highest priority
		if(hints.is(DalHintEnum.shardValue)) {
			Integer id = getIntValue(hints.get(DalHintEnum.shardValue));
			return String.valueOf(id%mod);
		}
		
		shard = locateByParameters(hints, columns, mod);
		if(shard != null)
			return shard;
		
		shard = locateByShardCol(hints, columns, mod);
		if(shard != null)
			return shard;
		
		throw new RuntimeException("Can not locate shard for " + logicDbName);
	}

	@Override
	public boolean isShardingByTable() {
		return tableColumns != null;
	}

	@Override
	public String locateTableShard(DalConfigure configure, String logicDbName,
			DalHints hints) {
		if(!isShardingByTable())
			throw new RuntimeException(String.format("Logic Db %s is not configured to be shard by table", logicDbName));
		
		String shard = hints.getTableShardId();
		if(shard != null)
			return buildShardStr(shard);
		
		// Shard value take the highest priority
		if(hints.is(DalHintEnum.tableShardValue)) {
			Integer id = getIntValue(hints.get(DalHintEnum.tableShardValue));
			return buildShardStr(String.valueOf(id%tableMod));
		}
		
		shard = locateByParameters(hints, tableColumns, tableMod);
		if(shard != null)
			return buildShardStr(shard);
		
		shard = locateByShardCol(hints, tableColumns, tableMod);
		if(shard != null)
			return buildShardStr(shard);
		
		throw new RuntimeException("Can not locate table shard for " + logicDbName);
	}
	
	private String locateByParameters(DalHints hints, String[] columns, int mod) {
		StatementParameters parameters = (StatementParameters)hints.get(DalHintEnum.parameters);
		if(parameters != null) {
			for(String column: columns) {
				StatementParameter param = parameters.get(column, ParameterDirection.Input);
				if(param != null && param.getValue() != null) {
					Integer id = getIntValue(param.getValue());
					return String.valueOf(id%mod);
				}
			}
		}
		return null;
	}
	
	private String locateByShardCol(DalHints hints, String[] columns, int mod) {
		Map<String, Integer> shardColValues = (Map<String, Integer>)hints.get(DalHintEnum.shardColValues);
		
		if(shardColValues != null) {
			for(String column: columns) {
				Integer id = getIntValue(shardColValues.get(column));
				if(id != null) {
					return String.valueOf(id%mod);
				}
			}
		}
		return null;
	}
	
	private String buildShardStr(String shardId) {
		return separator == null? shardId: separator + shardId;
	}
	
	private Integer getIntValue(Object value) {
		if(value instanceof Integer)
			return (Integer)value;
		
		if(value instanceof Number)
			return ((Number)value).intValue();
		
		if(value instanceof String)
			return new Integer((String)value);
		
		throw new RuntimeException(String.format("Shard value: %s can not be recoganized as int value", value.toString()));
	}

	@Override
	public boolean isShardingEnable(String tableName) {
		return shardedTables.contains(tableName);
	}
}
