package com.ctrip.platform.dal.dao;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.ctrip.platform.dal.common.enums.ParameterDirection;

public class StatementParameters {
	private static final String SQLHIDDENString = "*";
	
	private List<StatementParameter> parameters = new LinkedList<StatementParameter>();

	// Set when there is IN parameter
	private boolean needExpand = false;
	
	public StatementParameters add(StatementParameter parameter) {
		parameters.add(parameter);
		return this;
	}
	
	public StatementParameters set(int index, Object value) {
		return add(StatementParameter.Builder.set(index, value).build());
	}
	
	public StatementParameters set(int index, int sqlType, Object value) {
		return add(StatementParameter.Builder.set(index, sqlType, value).build());
	}

	public StatementParameters set(int index, String name, int sqlType, Object value) {
		return add(StatementParameter.Builder.set(index, sqlType, value).setName(name).build());
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

	public StatementParameters setSensitive(int index, String name, int sqlType, Object value) {
		return add(StatementParameter.Builder.set(index, sqlType, value).setSensitive(true).setName(name).build());
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
	
	public int setInParameter(int index, int sqlType, List<?> values) {
		set(index++, sqlType, values);
		needExpand = true;
		return index;
	}
	
	public int setSensitiveInParameter(int index, int sqlType, List<?> values) {
		setSensitive(index++, sqlType, values);
		needExpand = true;
		return index;
	}
	
	public int setInParameter(int index, String name, int sqlType, List<?> values) {
		set(index++, name, sqlType, values);
		needExpand = true;
		return index;
	}
	
	public int setSensitiveInParameter(int index, String name, int sqlType, List<?> values) {
		setSensitive(index++, name, sqlType, values);
		needExpand = true;
		return index;
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
	
	public String toLogString() {
		StringBuilder valuesSb = new StringBuilder();
		int i = 0;
		for (StatementParameter param : this.values()) {
			valuesSb.append(String.format("%s=%s", 
					param.getName() == null ? param.getIndex() : param.getName(), 
					param.isSensitive() ? SQLHIDDENString : param.getValue()));
			if (++i < size())
				valuesSb.append(",");
		}
		return valuesSb.toString();
	}
	
	public StatementParameters duplicateWith(String name, Object value) {
		StatementParameters tempParameters = new StatementParameters();
		
		for(StatementParameter parameter: parameters){
			Object pValue = name.equals(parameter.getName()) ? value: parameter.getValue();
			
			tempParameters.add(StatementParameter.Builder.
					set(parameter.getIndex(), parameter.getSqlType(), pValue).
					setName(parameter.getName()).
					setSensitive(parameter.isSensitive()).build());
		}
		
		return tempParameters;
	}
	
	/**
	 * This must be executed after duplicateWith
	 */
	public void expand() {
		//There is no IN parameter
		if(needExpand == false)
			return;
		
		//To be safe, order parameters by original index
		Collections.sort(parameters);
		int i = 0;
		while(i < parameters.size()) {
			StatementParameter p = parameters.get(i);
			if(p.getValue() instanceof List<?>){
				List<?> values = (List<?>)p.getValue();
				for(Object val : values){
					parameters.add(StatementParameter.Builder.
							set(++i, p.getSqlType(), val).
							setName(p.getName()).
							setSensitive(p.isSensitive()).build());
				}
			}else{
				p.currentBuilder.setIndex(++i);
			}
		}
	}
}
