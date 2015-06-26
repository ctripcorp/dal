package com.ctrip.platform.dal.dao;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import com.ctrip.platform.dal.dao.client.DalHA;
import com.ctrip.platform.dal.dao.task.DalAsyncCallback;
import com.ctrip.platform.dal.exceptions.DalException;

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
	// It is not so nice to put keyholder here, but to make Task stateless, I have no other choice
	private KeyHolder keyHolder;
	
	private DalAsyncCallback asyncCallback;
	
	public KeyHolder getKeyHolder() {
		return keyHolder;
	}

	public DalHints setKeyHolder(KeyHolder keyHolder) {
		this.keyHolder = keyHolder;
		return this;
	}
	
	public DalHints setDalAsyncCallback(DalAsyncCallback callback) {
		this.asyncCallback = callback;
		return this;
	}
	
	public DalAsyncCallback getDalAsyncCallback() {
		return asyncCallback;
	}

	public static DalHints createIfAbsent(DalHints hints) {
		return hints == null ? new DalHints() : hints;
	}
	
	public DalHints clone() {
		DalHints newHints = new DalHints();
		newHints.hints.putAll(hints);
		
		// Make sure we do deep copy for Map
		Map shardColValues = (Map)newHints.get(DalHintEnum.shardColValues);
		if(shardColValues != null)
			newHints.setShardColValues(new HashMap<String, Object>(shardColValues));

		// Make sure we do deep copy for Map
		Map fields = (Map)newHints.get(DalHintEnum.fields);
		if(fields != null)
			newHints.setFields(new LinkedHashMap<String, Object>(fields));
		
		newHints.keyHolder = keyHolder;
		return newHints;
	}
	
	public DalHints() {}
	
	/**
	 * Make sure only shardId, tableShardId, shardValue, shardColValue will be used to locate shard Id.
	 */
	public DalHints cleanUp() {
		return setFields(null).setParameters(null);
	}
	
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
	
	public DalHA getHA(){
		return (DalHA)hints.get(DalHintEnum.heighAvaliable);
	}
	
	public void setHA(DalHA ha){
		hints.put(DalHintEnum.heighAvaliable, ha);
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
	
	public DalHints setIfAbsent(DalHintEnum hint, Object value) {
		if(is(hint))
			return this;
		
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
	
	public DalHints inAllShards() {
		set(DalHintEnum.allShards);
		return this;
	}
	
	public boolean isAllShards() {
		return is(DalHintEnum.allShards);
	}
	
	public DalHints inShards(Set<String> shards) {
		hints.put(DalHintEnum.shards, shards);
		return this;
	}
	
	public Set<String> getShards() {
		return (Set<String>)hints.get(DalHintEnum.shards);
	}
	
	public <T> DalHints mergeBy(ResultMerger<T> merger) {
		hints.put(DalHintEnum.resultMerger, merger);
		return this;
	}

	public <T> DalHints sortBy(Comparator<T> sorter) {
		hints.put(DalHintEnum.resultSorter, sorter);
		return this;
	}

	public <T> Comparator<T> getSorter() {
		return (Comparator<T>)get(DalHintEnum.resultSorter);
	}

	public <T> DalHints sequentialExecute() {
		set(DalHintEnum.sequentialExecution);
		return this;
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
	
	public DalHints asyncExecution() {
		set(DalHintEnum.asyncExecution);
		return this;
	}
	
	/**
	 * If asyncExecution is set or there is callback, we assume it is asynchronized execution.
	 * And in this case the futureResult will always be populated with Future.
	 * If there is callback, the result will be pass to callback also.
	 *  
	 * @return
	 */
	public boolean isAsyncExecution() {
		return is(DalHintEnum.asyncExecution) || is(DalHintEnum.resultCallback);
	}
	
	public Future<?> getAsyncResult() {
		return (Future<?>)get(DalHintEnum.futureResult);
	}
	
	public DalHints callbackWith(DalResultCallback callback) {
		set(DalHintEnum.resultCallback, callback);
		return this;
	}
	
	public boolean isStopOnError() {
		return !is(DalHintEnum.continueOnError);
	}

	public void handleError(String msg, Throwable e) throws DalException {
		if(isStopOnError())
			throw DalException.wrap(e);

		// Just make sure error is not swallowed by us
		DalClientFactory.getDalLogger().error(msg, e);
	}

	public DalHints setIsolationLevel(int isolationLevel) {
		set(DalHintEnum.isolationLevel, isolationLevel);
		return this;
	}
}
