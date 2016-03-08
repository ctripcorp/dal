package com.ctrip.platform.dal.dao;

import java.util.Date;

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.client.DasProto;
import com.google.protobuf.ByteString;

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
		
		this.value = template.value;
		this.inParam = template.inParam;
		
		this.resultsParameter = template.resultsParameter;
		this.resultSetExtractor = template.resultSetExtractor;
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
	
	public DasProto.SqlParameters build2SqlParameters() {
		DasProto.SqlParameters.Builder builder = DasProto.SqlParameters
				.newBuilder();
		builder.setDbType(dbType.getIntVal());
		builder.setDirection(direction.getIntVal());
		builder.setIsNull(nullable);
		builder.setName(name);
		builder.setIndex(index);
		builder.setSensitive(sensitive);

		DasProto.AvailableType.Builder valueBuilder = DasProto.AvailableType
				.newBuilder();

		switch (dbType) {
		case Binary:
			builder.setValue(valueBuilder.setCurrent(5)
					.setBytesArg(ByteString.copyFrom((byte[]) value))
					.build());
			break;
		case Boolean:
			builder.setValue(valueBuilder.setCurrent(0)
					.setBoolArg(Boolean.parseBoolean(value.toString()))
					.build());
			break;
		case Byte:
		case SByte:
		case Int16:
		case Int32:
		case UInt16:
		case UInt32:
		case StringFixedLength:
			builder.setValue(valueBuilder.setCurrent(1)
					.setInt32Arg(Integer.parseInt(value.toString()))
					.build());
			break;
		case Int64:
		case UInt64:
			builder.setValue(valueBuilder.setCurrent(2)
					.setInt64Arg(Long.parseLong(value.toString())).build());
			break;
		case DateTime:
			builder.setValue(valueBuilder.setCurrent(2)
					.setInt64Arg(((Date) value).getTime()).build());
			break;
		case Single:
		case Double:
			builder.setValue(valueBuilder.setCurrent(3)
					.setDoubleArg(Double.parseDouble(value.toString()))
					.build());
			break;
		case String:
		case Decimal:
			builder.setValue(valueBuilder.setCurrent(4)
					.setStringArg(value.toString()).build());
			break;
		default:
			builder.setValue(valueBuilder.setCurrent(4)
					.setStringArg(value.toString()).build());
			break;
		}

		return builder.build();
	}

	@Override
	public int compareTo(StatementParameter o) {
		return this.index - o.index;
	}
}
