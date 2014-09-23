package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.sql.exceptions.DalException;
import com.ctrip.platform.dal.sql.exceptions.ErrorCode;

public class KeyHolder {
	private final List<Map<String, Object>> keyList = new LinkedList<Map<String, Object>>();;

	public int size() {
		return keyList.size();
	}
	
	/**
	 * Get the generated Id. The type is of Number.
	 * @return id in number
	 * @throws SQLException if there is more than one generated key or the conversion is failed.
	 */
	public Number getKey() throws SQLException {
		if (this.keyList.size() != 1) {
			throw new DalException(ErrorCode.ValidateKeyHolderSize, keyList);
		}
		return getKey(0);
	}

	/**
	 * Get the generated Id for given index. The type is of Number.
	 * @return key in number format
	 * @throws SQLException if there is more than one generated key or the conversion is failed.
	 */
	public Number getKey(int index) throws SQLException {
		try {
			if(keyList.get(index).size() != 1)
				throw new DalException(ErrorCode.ValidateKeyHolderFetchSize, keyList.get(index));
			
			return (Number)keyList.get(index).values().iterator().next();
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
		if (this.keyList.size() != 1) {
			throw new DalException(ErrorCode.ValidateKeyHolderSize, keyList);
		}
		return this.keyList.get(0);
	}

	/**
	 * Get all the generated keys for multiple insert.
	 * @return all the generated keys
	 */
	public List<Map<String, Object>> getKeyList() {
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
			for(Map<String, Object> key: keyList) {
				idList.add((Number)key.values().iterator().next());
			}
			return idList;
		} catch (Throwable e) {
			throw new DalException(ErrorCode.ValidateKeyHolderConvert, e);
		}
	}
}
