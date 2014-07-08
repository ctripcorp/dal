package com.ctrip.platform.dal.dao;

import java.util.Date;

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.client.DasProto;
import com.google.protobuf.ByteString;

public class StatementParameter {

	Builder currentBuilder;

	private StatementParameter(Builder builder) {
		currentBuilder = builder;
	}

	public static Builder newBuilder() {
		return Builder.create();
	}

	public DasProto.SqlParameters build2SqlParameters() {
		return currentBuilder.build2SqlParameters();
	}
	
	public boolean isDefaultType(){
		return currentBuilder.defaultType_;
	}
	
	// TODO use builder to build parameter can be optimized 
	public DbType getDbType() {
		return currentBuilder.dbType_;
	}

	public int getSqlType() {
		return currentBuilder.sqlType_;
	}

	public ParameterDirection getDirection() {
		return currentBuilder.direction_;
	}

	public boolean isNullable() {
		return currentBuilder.nullable_;
	}

	public String getName() {
		return currentBuilder.name_;
	}

	public int getIndex() {
		return currentBuilder.index_;
	}

	public boolean isSensitive() {
		return currentBuilder.sensitive_;
	}

	public Object getValue() {
		return currentBuilder.value_;
	}
	
	public void setValue(Object value) {
		currentBuilder.value_ = value;;
	}
	
	public boolean isResultsParameter() {
		return currentBuilder.resultsParameter_;
	}

	public DalResultSetExtractor<?> getResultSetExtractor() {
		return currentBuilder.resultSetExtractor_;
	}
	
	public boolean isInputParameter() {
		if(isResultsParameter())
			return false;
		return 	currentBuilder.direction_ == ParameterDirection.Input || currentBuilder.direction_ == ParameterDirection.InputOutput;
	}
	
	public boolean isOutParameter() {
		if(currentBuilder.resultsParameter_ || currentBuilder.direction_ == null)
			return false;
		return 	currentBuilder.direction_ == ParameterDirection.Output || currentBuilder.direction_ == ParameterDirection.InputOutput;
	}
	
	public static final class Builder {

		private Builder() {

		}

		public static Builder create() {
			return new Builder();
		}

		public static Builder set(int index, int sqlType, Object value) {
			Builder builder = new Builder();
			
			builder.index_ = index;
			builder.sqlType_ = sqlType;
			builder.value_ = value;
			builder.direction_ = ParameterDirection.Input;
			
			return builder;
		}
		
		public static Builder set(int index, Object value) {
			Builder builder = new Builder();
			
			builder.index_ = index;
			builder.defaultType_ = true;
			builder.value_ = value;
			builder.direction_ = ParameterDirection.Input;
			
			return builder;
		}
		
		public static Builder set(String name, int sqlType, Object value) {
			Builder builder = new Builder();
			
			builder.index_ = -1;
			builder.name_ = name;
			builder.sqlType_ = sqlType;
			builder.value_ = value;
			builder.direction_ = ParameterDirection.Input;
			
			return builder;
		}
		
		public static Builder registerInOut(String name, int sqlType, Object value) {
			Builder builder = new Builder();
			
			builder.index_ = -1;
			builder.sqlType_ = sqlType;
			builder.name_ = name;
			builder.value_ = value;
			builder.direction_ = ParameterDirection.InputOutput;
			
			return builder;
		}
		
		public static Builder registerOut(String name, int sqlType) {
			Builder builder = new Builder();
			
			builder.index_ = -1;
			builder.sqlType_ = sqlType;
			builder.name_ = name;
			builder.direction_ = ParameterDirection.Output;
			
			return builder;
		}
		
		private boolean defaultType_;
		
		private DbType dbType_;
		
		private int sqlType_;

		private ParameterDirection direction_;

		private boolean nullable_;

		private String name_;

		private int index_;

		private boolean sensitive_;

		private Object value_;
		
		private boolean resultsParameter_;
		private DalResultSetExtractor<?> resultSetExtractor_;
		
		public Builder setSqlType(int sqlType) {
			sqlType_ = sqlType;
			return this;
		}
		

		public Builder setDirection(ParameterDirection direction) {
			direction_ = direction;
			return this;
		}

		public Builder setNullable(boolean nullable) {
			nullable_ = nullable;
			return this;
		}

		public Builder setName(String name) {
			name_ = name;
			return this;
		}

		public Builder setIndex(int index) {
			index_ = index;
			return this;
		}

		public Builder setSensitive(boolean sensitive) {
			sensitive_ = sensitive;
			return this;
		}

		public Builder setValue(Object value) {
			value_ = value;
			return this;
		}
		
		public Builder setResultsParameter(boolean resultsParameter) {
			resultsParameter_ = resultsParameter;
			return this;
		}

		public Builder setResultSetExtractor(DalResultSetExtractor<?> resultSetExtractor) {
			resultSetExtractor_ = resultSetExtractor;
			return this;
		}

		public Builder setDefaultType(boolean defaultType){
			defaultType_ = defaultType;
			return this;
		}
		
		public StatementParameter build() {
			return new StatementParameter(this);
		}

		DasProto.SqlParameters build2SqlParameters() {

			DasProto.SqlParameters.Builder builder = DasProto.SqlParameters
					.newBuilder();
			builder.setDbType(dbType_.getIntVal());
			builder.setDirection(direction_.getIntVal());
			builder.setIsNull(nullable_);
			builder.setName(name_);
			builder.setIndex(index_);
			builder.setSensitive(sensitive_);

			DasProto.AvailableType.Builder valueBuilder = DasProto.AvailableType
					.newBuilder();

			switch (dbType_) {
			case Binary:
				builder.setValue(valueBuilder.setCurrent(5)
						.setBytesArg(ByteString.copyFrom((byte[]) value_))
						.build());
				break;
			case Boolean:
				builder.setValue(valueBuilder.setCurrent(0)
						.setBoolArg(Boolean.parseBoolean(value_.toString()))
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
						.setInt32Arg(Integer.parseInt(value_.toString()))
						.build());
				break;
			case Int64:
			case UInt64:
				builder.setValue(valueBuilder.setCurrent(2)
						.setInt64Arg(Long.parseLong(value_.toString())).build());
				break;
			case DateTime:
				builder.setValue(valueBuilder.setCurrent(2)
						.setInt64Arg(((Date) value_).getTime()).build());
				break;
			case Single:
			case Double:
				builder.setValue(valueBuilder.setCurrent(3)
						.setDoubleArg(Double.parseDouble(value_.toString()))
						.build());
				break;
			case String:
			case Decimal:
				builder.setValue(valueBuilder.setCurrent(4)
						.setStringArg(value_.toString()).build());
				break;
			default:
				builder.setValue(valueBuilder.setCurrent(4)
						.setStringArg(value_.toString()).build());
				break;
			}

			return builder.build();
		}
	}

}
