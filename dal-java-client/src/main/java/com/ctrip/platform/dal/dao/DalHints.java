package com.ctrip.platform.dal.dao;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Additional parameters used to indicate how DAL behaves for each of the operation.
 * 
 * IMPORTANT NOTE!!!
 * Because entry may be changed by by DAL internal logic, DalHints is not intended to be reused. 
 * You should never create a class level DalHints reference and reuse it in the following calls.
 * 
 * @author jhhe
 */
public class DalHints {
	private Map<DalHintEnum, Object> hints = new LinkedHashMap<DalHintEnum, Object>();
	
	public static DalHints createIfAbsent(DalHints hints) {
		return hints == null ? new DalHints() : hints;
	}
	
	public DalHints clone() {
		DalHints newHints = new DalHints ();
		newHints.hints.putAll(hints);
		return newHints;
	}
	
	public static DalHints copyOf(DalHints otherHints) {
		DalHints hints = new DalHints();
		hints.hints.putAll(otherHints.hints);
		return hints;
	}
	
	public DalHints() {}
	
	public DalHints(DalHintEnum...hints) {
		for(DalHintEnum hint: hints) {
			set(hint);
		}
	}
	
	public boolean is(DalHintEnum hint) {
		return hints.containsKey(hint);
	}
	
	public Object get(DalHintEnum hint) {
		return hints.get(hint);
	}
	
	public Integer getInt(DalHintEnum hint, int defaultValue) {
		Object value = hints.get(hint);
		if(value == null)
			return defaultValue;
		return (Integer)value;
	}
	
	public Integer getInt(DalHintEnum hint) {
		return (Integer)hints.get(hint);
	}
	
	public String getString(DalHintEnum hint) {
		Object value = hints.get(hint);
		if(value == null)
			return null;
		
		if(value instanceof String)
			return (String)value;
		
		return value.toString();
	}

	public String[] getStrings(DalHintEnum hint) {
		return (String[])hints.get(hint);
	}
	
	public DalHints set(DalHintEnum hint) {
		set(hint, null);
		return this;
	}
	
	public DalHints set(DalHintEnum hint, Object value) {
		hints.put(hint, value);
		return this;
	}
	
	public DalHints inShard(String shardId) {
		hints.put(DalHintEnum.shard, shardId);
		return this;
	}
	
	public DalHints inShard(Integer shardId) {
		hints.put(DalHintEnum.shard, shardId);
		return this;
	}
	
	public DalHints inTableShard(String tableShardId) {
		hints.put(DalHintEnum.tableShard, tableShardId);
		return this;
	}
	
	public DalHints inTableShard(Integer tableShardId) {
		hints.put(DalHintEnum.tableShard, tableShardId);
		return this;
	}
	
	public String getShardId() {
		return getString(DalHintEnum.shard);
	}
	
	public String getTableShardId() {
		return getString(DalHintEnum.tableShard);
	}
	
	public DalHints setShardValue(Object shardValue) {
		return set(DalHintEnum.shardValue, shardValue);
	}
	
	public DalHints setTableShardValue(Object tableShardValue) {
		return set(DalHintEnum.tableShardValue, tableShardValue);
	}
	
	public DalHints setShardColValues(Map<String, ?> shardColValues) {
		return set(DalHintEnum.shardColValues, shardColValues);
	}

	public DalHints setShardColValue(String column, Object value) {
		if(is(DalHintEnum.shardColValues) == false) {
			setShardColValues(new HashMap<String, Object>());
		}
		
		Map<String, Object> shardColValues = (Map<String, Object>)get(DalHintEnum.shardColValues);
		shardColValues.put(column, value);
		return this;
	}
	
	public DalHints setFields(Map<String, ?> fields) {
		return set(DalHintEnum.fields, fields);
	}
	
	public DalHints setParameters(StatementParameters parameters) {
		return set(DalHintEnum.parameters, parameters);
	}
	
	public DalHints masterOnly() {
		set(DalHintEnum.masterOnly, true);
		return this;
	}

	public DalHints slaveOnly() {
		set(DalHintEnum.masterOnly, false);
		return this;
	}
	
	public DalHints continueOnError() {
		set(DalHintEnum.continueOnError);
		return this;
	}
	
	public boolean isStopOnError() {
		return !is(DalHintEnum.continueOnError);
	}

	public DalHints setIsolationLevel(int isolationLevel) {
		set(DalHintEnum.isolationLevel, isolationLevel);
		return this;
	}
}
