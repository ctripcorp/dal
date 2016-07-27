package com.ctrip.platform.dal.dao;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.common.enums.ParameterDirection;

public class StatementParameter implements Comparable<StatementParameter> {
	private boolean defaultType;
	
	private DbType dbType;
	
	private int sqlType;

	private ParameterDirection direction;

	private boolean nullable;

	private String name;

	private int index;

	private boolean sensitive;

	private Object value;
	private boolean inParam;
	
	private boolean resultsParameter;
	private DalResultSetExtractor<?> resultSetExtractor;

	public StatementParameter() {}
	
	public StatementParameter(StatementParameter template) {
		this.defaultType = template.defaultType;
		this.dbType = template.dbType;
		this.sqlType = template.sqlType;
		this.direction = template.direction;
		this.nullable = template.nullable;
		this.name = template.name;
		this.index = template.index;
		this.sensitive = template.sensitive;
		
		this.resultsParameter = template.resultsParameter;
		this.resultSetExtractor = template.resultSetExtractor;
		
		handleValue(template);
	}
	
	private void handleValue(StatementParameter template) {
		this.inParam = template.inParam;
		value = inParam ? new ArrayList<>((List)template.value) : template.value;
	}
	
	public StatementParameter(int index, int sqlType, Object value) {
		this.index = index;
		this.sqlType = sqlType;
		this.value = value;
		this.direction = ParameterDirection.Input;
	}
	
	public StatementParameter(int index, Object value) {
		this.index = index;
		this.defaultType = true;
		this.value = value;
		this.direction = ParameterDirection.Input;
	}
	
	public StatementParameter(String name, int sqlType, Object value) {
		this.index = -1;
		this.name = name;
		this.sqlType = sqlType;
		this.value = value;
		this.direction = ParameterDirection.Input;
	}
	
	public static StatementParameter registerInOut(String name, int sqlType, Object value) {
		StatementParameter parameter = new StatementParameter(name, sqlType, value);
		parameter.setDirection(ParameterDirection.InputOutput);
		return parameter;
	}
	
	public static StatementParameter registerOut(String name, int sqlType) {
		StatementParameter parameter = new StatementParameter(name, sqlType, null);
		parameter.setDirection(ParameterDirection.Output);
		return parameter;
	}
	
	public boolean isDefaultType(){
		return defaultType;
	}
	
	public DbType getDbType() {
		return dbType;
	}

	public int getSqlType() {
		return sqlType;
	}

	public ParameterDirection getDirection() {
		return direction;
	}

	public boolean isNullable() {
		return nullable;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public boolean isSensitive() {
		return sensitive;
	}

	public <T> T getValue() {
		return (T)value;
	}
	
	public boolean isInParam() {
		return inParam;
	}

	public boolean isResultsParameter() {
		return resultsParameter;
	}

	public DalResultSetExtractor<?> getResultSetExtractor() {
		return resultSetExtractor;
	}
	
	public boolean isInputParameter() {
		if(isResultsParameter())
			return false;
		return 	direction == ParameterDirection.Input || direction == ParameterDirection.InputOutput;
	}
	
	public boolean isOutParameter() {
		if(resultsParameter || direction == null)
			return false;
		return 	direction == ParameterDirection.Output || direction == ParameterDirection.InputOutput;
	}
	
	public StatementParameter setSqlType(int sqlType) {
		this.sqlType = sqlType;
		return this;
	}
	
	public StatementParameter setDirection(ParameterDirection direction) {
		this.direction = direction;
		return this;
	}

	public StatementParameter setNullable(boolean nullable) {
		this.nullable = nullable;
		return this;
	}

	public StatementParameter setName(String name) {
		this.name = name;
		return this;
	}

	public StatementParameter setIndex(int index) {
		this.index = index;
		return this;
	}

	public StatementParameter setSensitive(boolean sensitive) {
		this.sensitive = sensitive;
		return this;
	}

	public StatementParameter setValue(Object value) {
		this.value = value;
		return this;
	}
	
	public StatementParameter setInParam(boolean inParam) {
		this.inParam = inParam;
		return this;
	}

	public StatementParameter setResultsParameter(boolean resultsParameter) {
		this.resultsParameter = resultsParameter;
		return this;
	}

	public StatementParameter setResultSetExtractor(DalResultSetExtractor<?> resultSetExtractor) {
		this.resultSetExtractor = resultSetExtractor;
		return this;
	}

	public StatementParameter setDefaultType(boolean defaultType){
		this.defaultType = defaultType;
		return this;
	}

	@Override
	public int compareTo(StatementParameter o) {
		return this.index - o.index;
	}
}
