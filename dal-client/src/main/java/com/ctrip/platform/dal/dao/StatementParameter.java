package com.ctrip.platform.dal.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.common.enums.ParameterDirection;

public class StatementParameter implements Comparable<StatementParameter> {
	private boolean defaultType;
	
	private DbType dbType;
	
	private int sqlType;

	private ParameterDirection direction;

	private String name;

	private int index;
	
    private boolean nullable = false;
    private boolean valid = true;

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
	
	public static StatementParameter registerInOut(int index, int sqlType, Object value) {
		StatementParameter parameter = new StatementParameter(index, sqlType, value);
		parameter.setDirection(ParameterDirection.InputOutput);
		return parameter;
	}
	
	public static StatementParameter registerOut(String name, int sqlType) {
		StatementParameter parameter = new StatementParameter(name, sqlType, null);
		parameter.setDirection(ParameterDirection.Output);
		return parameter;
	}
	
	public static StatementParameter registerOut(int index, int sqlType) {
		StatementParameter parameter = new StatementParameter(index, sqlType, null);
		parameter.setDirection(ParameterDirection.Output);
		return parameter;
	}
	
	public static void validateInParams(String name, List<?> values) {
        if(null == values || values.size() == 0)
            throw new IllegalStateException(name + " must have more than one value.");
        
        if(values.contains(null))
            throw new IllegalStateException(name + " is not support null value.");
    }
	
	public static boolean isNullInParams(List<?> values) {
        if(null == values || values.size() == 0){
            return true;
        }
        
        Iterator<?> ite = values.iterator();
        while(ite.hasNext()){
            if(ite.next()==null){
                ite.remove();
            }
        }
        
        if(values.size() == 0){
            return true;
        }
        
        return false;
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

	public StatementParameter setName(String name) {
		this.name = name;
		return this;
	}

	public StatementParameter setIndex(int index) {
		this.index = index;
		return this;
	}

    /**
     * Set the parameter to be nullable, if it is , the parameter maybe ignored
     */
	public void nullable() {
	    if(isInParam())
	        when(!isNullInParams((List<?>)value));
	    else
	        when(value != null);
    }

    /**
     * Set if the parameter is valid or not by the condition
     */
    public void when(boolean condition) {
        this.valid = condition;
    }
    
    /**
     * @return if this parameter can be removed
     */
    public boolean isValid() {
        if(valid == false)
            return false;
        
        if(isInParam())
            validateInParams(name, (List<?>)value);
            
        return true;
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
