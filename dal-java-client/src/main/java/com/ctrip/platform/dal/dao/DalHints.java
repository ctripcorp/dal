package com.ctrip.platform.dal.dao;

import java.util.LinkedHashMap;
import java.util.Map;

public class DalHints {
	private Map<DalHintEnum, Object> hints = new LinkedHashMap<DalHintEnum, Object>();
	
	public boolean is(DalHintEnum hint) {
		return hints.containsKey(hint);
	}
	
	public Object get(DalHintEnum hint) {
		return hints.get(hint);
	}
	
	public DalHints set(DalHintEnum hint) {
		set(hint, null);
		return this;
	}
	
	public DalHints set(DalHintEnum hint, Object value) {
		hints.put(hint, value);
		return this;
	}
	
	public DalHints selectFirst() {
		set(DalHintEnum.rowCount, 1);
		return this;
	}

	public DalHints selectTop(int count) {
		set(DalHintEnum.rowCount, count);
		return this;
	}
	
	public DalHints selectFrom(int start, int count) {
		set(DalHintEnum.startRow, start);
		set(DalHintEnum.rowCount, count);
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
	
	public DalHints shardWith(String column) {
		set(DalHintEnum.usingBatch, column);
		return this;
	}
	
	public DalHints usingBatch() {
		set(DalHintEnum.usingBatch);
		return this;
	}
	
	public DalHints stopOnError() {
		set(DalHintEnum.SPA);
		return this;
	}
	
	public DalHints SPA() {
		hints.put(DalHintEnum.SPA, new Object());
		return this;
	}
}
