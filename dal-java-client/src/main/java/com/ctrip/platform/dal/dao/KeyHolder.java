package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KeyHolder {
	private final List<Map<String, Object>> keyList;


	/**
	 * Create a new GeneratedKeyHolder with a default list.
	 */
	public KeyHolder() {
		this.keyList = new LinkedList<Map<String, Object>>();
	}

	/**
	 * Create a new GeneratedKeyHolder with a given list.
	 * @param keyList a list to hold maps of keys
	 */
	public KeyHolder(List<Map<String, Object>> keyList) {
		this.keyList = keyList;
	}


	public Number getKey() throws SQLException {
		if (this.keyList.size() == 0) {
			return null;
		}
		if (this.keyList.size() > 1 || this.keyList.get(0).size() > 1) {
			throw new SQLException(
					"The getKey method should only be used when a single key is returned.  " +
					"The current key entry contains multiple keys: " + this.keyList);
		}
		Iterator<Object> keyIter = this.keyList.get(0).values().iterator();
		if (keyIter.hasNext()) {
			Object key = keyIter.next();
			if (!(key instanceof Number)) {
				throw new SQLException(
						"The generated key is not of a supported numeric type. " +
						"Unable to cast [" + (key != null ? key.getClass().getName() : null) +
						"] to [" + Number.class.getName() + "]");
			}
			return (Number) key;
		}
		else {
			throw new SQLException("Unable to retrieve the generated key. " +
					"Check that the table has an identity column enabled.");
		}
	}

	public Map<String, Object> getKeys() throws SQLException {
		if (this.keyList.size() == 0) {
			return null;
		}
		if (this.keyList.size() > 1)
			throw new SQLException(
					"The getKeys method should only be used when keys for a single row are returned.  " +
					"The current key list contains keys for multiple rows: " + this.keyList);
		return this.keyList.get(0);
	}

	public List<Map<String, Object>> getKeyList() {
		return this.keyList;
	}
}
