package com.ctrip.platform.dao;

import java.util.HashMap;
import java.util.Map;

public class DalHints {
	private Map<DalHintEnum, Object> hints = new HashMap<DalHintEnum, Object>();
	
	public boolean contains(DalHintEnum hint) {
		return hints.containsKey(hint);
	}
	
	public Object get(DalHintEnum hint) {
		return hints.get(hint);
	}
	
	public DalHints addHint(DalHintEnum hint) {
		hints.put(hint, null);
		return this;
	}
	
	public DalHints addHint(DalHintEnum hint, Object value) {
		
		return this;
	}
	
	public DalHints selectFirst() {
		
		return this;
	}

	public DalHints selectTop(int number) {
		
		return this;
	}
	
	public DalHints selectFrom(int start, int number) {
		
		return this;
	}
	
	public DalHints masterOnly() {
		
		return this;
	}

	public DalHints slaveOnly() {
		
		return this;
	}
	
	public DalHints shardWith(String column) {
		
		return this;
	}
}
