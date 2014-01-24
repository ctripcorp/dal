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
	
	// TODO use builder to build parameter can be optimized 
	public DbType getDbType() {
		return currentBuilder.dbType_;
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

	public static final class Builder {

		private Builder() {

		}

		public static Builder create() {
			return new Builder();
		}

		private DbType dbType_;

		private ParameterDirection direction_;

		private boolean nullable_;

		private String name_;

		private int index_;

		private boolean sensitive_;

		private Object value_;

		public Builder setDbType(DbType dbType) {
			dbType_ = dbType;
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

	public static void main(String[] args) {
		StatementParameter.Builder builder = StatementParameter.newBuilder();
		builder.setDbType(DbType.Boolean)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false).setValue(false);
		StatementParameter instance = builder.build();
		System.out.println(instance.build2SqlParameters().toByteArray());
		
	}

}
