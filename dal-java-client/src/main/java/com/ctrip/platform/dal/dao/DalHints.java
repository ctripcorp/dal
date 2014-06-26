package com.ctrip.platform.dal.dao;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Additional parameters used to indicate how DAL behaves for each of the operation.
 * @author jhhe
 */
public class DalHints {
	private Map<DalHintEnum, Object> hints = new LinkedHashMap<DalHintEnum, Object>();
	
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
		return (String)hints.get(hint);
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
