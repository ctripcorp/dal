package com.ctrip.platform.dal.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.ctrip.platform.dal.common.enums.ParameterDirection;

public class StatementParameters {
	private static final String SQLHIDDENString = "*";
	
	private List<StatementParameter> parameters = new LinkedList<StatementParameter>();

	public StatementParameters add(StatementParameter parameter) {
		parameters.add(parameter);
		return this;
	}
	
	public StatementParameters addAll(StatementParameters extraParameters) {
		int index = parameters.size() + 1;
		for(StatementParameter p: extraParameters.values())
			add(p.setIndex(index++));		
		return this;
	}
	
	public StatementParameters set(int index, Object value) {
		return add(new StatementParameter(index, value));
	}
	
	public StatementParameters set(int index, int sqlType, Object value) {
		return add(new StatementParameter(index, sqlType, value));
	}

	public StatementParameters set(int index, String name, int sqlType, Object value) {
		return add(new StatementParameter(index, sqlType, value).setName(name));
	}

	public StatementParameters set(String name, int sqlType, Object value) {
		return add(new StatementParameter(name, sqlType, value));
	}

	public StatementParameters registerInOut(String name, int sqlType, Object value) {
		return add(StatementParameter.registerInOut(name, sqlType, value));
	}
	
	public StatementParameters registerOut(String name, int sqlType) {
		return add(StatementParameter.registerOut(name, sqlType));
	}
	
	public StatementParameters setSensitive(int index, int sqlType, Object value) {
		return add(new StatementParameter(index, sqlType, value).setSensitive(true));
	}

	public StatementParameters setSensitive(int index, String name, int sqlType, Object value) {
		return add(new StatementParameter(index, sqlType, value).setSensitive(true).setName(name));
	}
	
	public StatementParameters setSensitive(String name, int sqlType, Object value) {
		return add(new StatementParameter(name, sqlType, value).setSensitive(true));
	}

	public StatementParameters registerInOutSensitive(String name, int sqlType, Object value) {
		return add(StatementParameter.registerInOut(name, sqlType, value).setSensitive(true));
	}
	
	public StatementParameters registerOutSensitive(String name, int sqlType) {
		return add(StatementParameter.registerOut(name, sqlType).setSensitive(true));
	}
	
	public StatementParameters setResultsParameter(String name) {
		return add(new StatementParameter().setResultsParameter(true).setName(name));
	}
	
	public StatementParameters setResultsParameter(String name, DalResultSetExtractor<?> extractor) {
		return add(new StatementParameter().setResultsParameter(true).setResultSetExtractor(extractor).setName(name));
	}
	
	public int setInParameter(int index, String name, int sqlType, List<?> values, boolean sensitive) {
		add(new StatementParameter(index++, sqlType, values).setName(name).setSensitive(sensitive).setInParam(true));
		return index;
	}
	
	public int setInParameter(int index, int sqlType, List<?> values) {
		return setInParameter(index, null, sqlType, values, false);
	}
	
	public int setSensitiveInParameter(int index, int sqlType, List<?> values) {
		return setInParameter(index, null, sqlType, values, true);
	}
	
	public int setInParameter(int index, String name, int sqlType, List<?> values) {
		return setInParameter(index, name, sqlType, values, false);	
	}
	
	public int setSensitiveInParameter(int index, String name, int sqlType, List<?> values) {
		return setInParameter(index, name, sqlType, values, true);
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
			
			tempParameters.add(new StatementParameter(parameter).setValue(pValue));
		}
		
		return tempParameters;
	}
	
	public StatementParameters duplicate() {
		StatementParameters tempParameters = new StatementParameters();
		
		for(StatementParameter parameter: parameters){
			tempParameters.add(new StatementParameter(parameter));
		}
		
		return tempParameters;
	}
	
	public List<List<?>> getAllInParameters() {
		List<List<?>> inParams = new ArrayList<>();
		for(StatementParameter parameter: parameters)
			if(parameter.isInParam())
				inParams.add((List<?>)parameter.getValue());
			
		return inParams;
	}
	
	public boolean containsInParameter() {
		for(StatementParameter p: parameters)
			if(p.isInParam())
				return true;
		return false;
	}
	
	/**
	 * Expand in parameters if necessary. This must be executed before execution
	 */
	public void compile() {
		if(!containsInParameter())
			return;
		
		//To be safe, order parameters by original index
		Collections.sort(parameters);

		// Make a copy of original parameters
		List<StatementParameter> tmpParameters = new LinkedList<StatementParameter>(parameters);

		// The change will be made into original parameters
		int i = 0;
		for(StatementParameter p: tmpParameters) {
			if(p.isInParam()) {
				// Remove the original
				parameters.remove(p);
				List<?> values = p.getValue();
				for(Object val : values) {
					parameters.add(i, new StatementParameter(p).setIndex((i+1)).setInParam(false).setValue(val));
					i++;
				}
			}else {
				p.setIndex(++i);
			}
		}
	}
}
