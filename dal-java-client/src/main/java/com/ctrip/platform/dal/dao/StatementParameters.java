package com.ctrip.platform.dal.dao;

import java.util.LinkedList;
import java.util.List;

import com.ctrip.platform.dal.common.enums.ParameterDirection;

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
	
	public StatementParameters setResultsParameter(String name) {
		return add(StatementParameter.newBuilder().setResultsParameter(true).setName(name).build());
	}
	
	public StatementParameters setResultsParameter(String name, DalResultSetExtractor<?> extractor) {
		return add(StatementParameter.newBuilder().setResultsParameter(true).setResultSetExtractor(extractor).setName(name).build());
	}
	
	public int size() {
		return parameters.size();
	}
	
	public StatementParameter get(int i) {
		return parameters.get(i);
	}
	
	public StatementParameter get(String name, ParameterDirection direction) {
		if(name == null)
			return null;
		
		for(StatementParameter parameter: parameters) {
			if(parameter.getName() != null && parameter.getName().equalsIgnoreCase(name) && direction == parameter.getDirection())
				return parameter;
		}
		return null;
	}

	public List<StatementParameter> values() {
		return parameters;
	}
	// Other overload helper methods
}
