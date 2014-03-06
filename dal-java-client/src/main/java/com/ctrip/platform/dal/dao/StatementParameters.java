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

	public StatementParameters set(String name, int sqlType, Object value) {
		return add(StatementParameter.Builder.set(name, sqlType, value).build());
	}

	public StatementParameters registerInOut(String name, int sqlType, Object value) {
		return add(StatementParameter.Builder.registerInOut(name, sqlType, value).build());
	}
	
	public StatementParameters registerOut(String name, int sqlType) {
		return add(StatementParameter.Builder.registerOut(name, sqlType).build());
	}
	
	public StatementParameters setSensitive(int index, int sqlType, Object value) {
		return add(StatementParameter.Builder.set(index, sqlType, value).setSensitive(true).build());
	}

	public StatementParameters setSensitive(String name, int sqlType, Object value) {
		return add(StatementParameter.Builder.set(name, sqlType, value).setSensitive(true).build());
	}

	public StatementParameters registerInOutSensitive(String name, int sqlType, Object value) {
		return add(StatementParameter.Builder.registerInOut(name, sqlType, value).setSensitive(true).build());
	}
	
	public StatementParameters registerOutSensitive(String name, int sqlType) {
		return add(StatementParameter.Builder.registerOut(name, sqlType).setSensitive(true).build());
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
