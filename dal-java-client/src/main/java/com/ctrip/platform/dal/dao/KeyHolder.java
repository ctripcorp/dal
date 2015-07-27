package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class KeyHolder {
	private boolean requireMerge = false;
	private int pojoListSize;

	private final Map<Integer, Map<String, Object>> allKeys = new ConcurrentHashMap<>();
	private final List<Map<String, Object>> keyList = new LinkedList<Map<String, Object>>();
	
	private AtomicBoolean merged = new AtomicBoolean(false);

	/**
	 * Indicate that merge is needed for cross shard case
	 * @param size
	 */
	public void setSize(int size) {
		pojoListSize = size;
	}
	
	public int size() {
		if(pojoListSize != 0)
			return pojoListSize;
		return keyList.size();
	}
	
	/**
	 * Indicate that a cross shard operation is under going, the generated keys need to be merged
	 */
	public void requireMerge() {
		this.requireMerge = true;
	}
	
	public boolean isRequireMerge() {
		return requireMerge;
	}
	
	public boolean isMerged() {
		return merged.get();
	}
	
	public void waitForMerge() throws InterruptedException {
		while(isMerged() == false)
			Thread.sleep(1);
	}
	
	public void waitForMerge(int timeout) throws InterruptedException {
		int i = 0;
		while(isMerged() == false && timeout > i++)
			Thread.sleep(1);
	}
	
	/**
	 * Get the generated Id. The type is of Number.
	 * @return id in number
	 * @throws SQLException if there is more than one generated key or the conversion is failed.
	 */
	public Number getKey() throws SQLException {
		return getId(getKeys());
	}

	/**
	 * Get the generated Id for given index. The type is of Number.
	 * @return key in number format
	 * @throws SQLException if the generated key is not number type.
	 */
	public Number getKey(int index) throws SQLException {
		if(size() != 0 && requireMerge && allKeys.containsKey(index))
			return getId(allKeys.get(index));
		
		try {
			return getId(getKeyList().get(index));
		} catch (Throwable e) {
			throw new DalException(ErrorCode.ValidateKeyHolderConvert, e);
		}
	}

	/**
	 * Get the first generated key in map.
	 * @return null if no key found, or the keys in a map
	 * @throws SQLException
	 */
	public Map<String, Object> getKeys() throws SQLException {
		if (size() != 1) {
			throw new DalException(ErrorCode.ValidateKeyHolderSize, keyList);
		}
		
		return this.keyList.get(0);
	}

	/**
	 * Get all the generated keys for multiple insert.
	 * @return all the generated keys
	 * @throws DalException 
	 */
	public List<Map<String, Object>> getKeyList() throws DalException {
		if(requireMerge && merged.get() == false)
			throw new DalException(ErrorCode.KeyGenerationFailOrNotCompleted);

		return this.keyList;
	}
	
	/**
	 * Convert generated keys to list of number. 
	 * @return
	 * @throws SQLException if the conversion fails
	 */
	public List<Number> getIdList() throws SQLException {
		List<Number> idList = new ArrayList<Number>();
		
		try {
			for(Map<String, Object> key: getKeyList()) {
				idList.add(getId(key));
			}
			return idList;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new DalException(ErrorCode.ValidateKeyHolderConvert, e);
		}
	}
	
	private Number getId(Map<String, Object> key) throws DalException {
		if(key.size() != 1)
			throw new DalException(ErrorCode.ValidateKeyHolderFetchSize, key);
		
		return (Number)key.values().iterator().next();
	}
	
	/**
	 * For internal use, add key in a dedicate shard
	 * @param key
	 */
	public void addKey(Map<String, Object> key) {
		keyList.add(key);
	}
	
	/**
	 * For internal use. Add partial generated keys, it will only be invoked for cross shard combine insert case 
	 * @param indexList
	 * @param tmpHolder
	 */
	public void addPatial(Integer[] indexList, KeyHolder tmpHolder) {
		int i = 0;
		for(Integer index: indexList) {
			allKeys.put(index, tmpHolder.keyList.get(i++));
		}
		
		// All partial is added, start merge generated keys
		if(pojoListSize == allKeys.size())
			merge();
	}
	
	private synchronized void merge() {
		if(merged.get())
			return;
		
		keyList.clear();
		for(int i = 0; i < allKeys.size(); i++)
			keyList.add(allKeys.get(i));
		
		merged.set(true);
	}
}
