package com.ctrip.platform.dal.dao.strategy;

import java.util.Map;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

public class ShardColModShardStrategy extends AbstractRWSeparationStrategy implements DalShardingStrategy {
	public static final String COLUMNS = "columns";
	public static final String MOD = "mod";
	
	public static final String TABLE_COLUMNS = "tableColumns";
	public static final String TABLE_MOD = "tableMod";
	public static final String SEPARATOR = "separator";

	private String[] columns;
	private Integer mod;

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
		if(columns.length == 0)
			return null;
		
		String shard = hints.getString(DalHintEnum.shard);
		if(shard != null)
			return shard;
		
		// Shard value take the highest priority
		if(hints.is(DalHintEnum.shardValue)) {
			Integer id = (Integer)hints.get(DalHintEnum.shardValue);
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
		if(tableColumns.length == 0)
			return null;
		
		String shard = hints.getString(DalHintEnum.tableShard);
		if(shard != null)
			return buildShardStr(shard);
		
		// Shard value take the highest priority
		if(hints.is(DalHintEnum.tableShardValue)) {
			Integer id = (Integer)hints.get(DalHintEnum.shardValue);
			return buildShardStr(String.valueOf(id%mod));
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
					Integer id = (Integer)param.getValue();
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
				Integer id = shardColValues.get(column);
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
}
