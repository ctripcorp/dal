package com.ctrip.platform.dal.dao;

import java.util.LinkedList;
import java.util.List;

public class StatementParameters {
	private List<StatementParameter> parameters = new LinkedList<StatementParameter>();
	
	public StatementParameters add(StatementParameter parameter) {
		parameters.add(parameter);
		return this;
	}
	
	public StatementParameters set(int index, int sqlType, Object value) {
		return add(StatementParameter.Builder.set(index, sqlType, value).build());
	}

	public StatementParameters registerInOut(int index, int sqlType, String name, Object value) {
		return add(StatementParameter.Builder.registerInOut(index, sqlType, name, value).build());
	}
	
	public StatementParameters registerOut(int index, int sqlType, String name) {
		return add(StatementParameter.Builder.registerOut(index, sqlType, name).build());
	}
	
	public int size() {
		return parameters.size();
	}
	
	public StatementParameter get(int i) {
		return parameters.get(i);
	}
	
	public List<StatementParameter> values() {
		return parameters;
	}
	// Other overload helper methods
}
