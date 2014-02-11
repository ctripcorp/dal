package com.ctrip.platform.dal.dao;

import java.util.LinkedList;
import java.util.List;

public class StatementParameters {
	private List<StatementParameter> parameters = new LinkedList<StatementParameter>();
	
	public StatementParameters add(StatementParameter parameter) {
		parameters.add(parameter);
		return this;
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
