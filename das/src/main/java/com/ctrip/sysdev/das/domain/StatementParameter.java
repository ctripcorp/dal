package com.ctrip.sysdev.das.domain;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import org.msgpack.packer.Packer;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Unpacker;

import com.ctrip.sysdev.das.domain.enums.DbType;
import com.ctrip.sysdev.das.domain.enums.ParameterDirection;

public class StatementParameter implements Comparable<StatementParameter> {

	private DbType dbType;

	private ParameterDirection direction;

	private boolean nullable;

	private int index;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private int size;

	private Value value;

	private boolean sensitive;

	/**
	 * 获取C#传入Db类型
	 * 
	 * @return
	 */
	public DbType getDbType() {
		return dbType;
	}

	/**
	 * 设置C# 对应Db类型
	 * 
	 * @param dbType
	 */
	public void setDbType(DbType dbType) {
		this.dbType = dbType;
	}

	/**
	 * 获取参数方向
	 * 
	 * @return
	 */
	public ParameterDirection getDirection() {
		return direction;
	}

	/**
	 * 设置参数方向
	 * 
	 * @param direction
	 */
	public void setDirection(ParameterDirection direction) {
		this.direction = direction;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * 
	 * @param nullable
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * 
	 * @return
	 */
	public int getSize() {
		return size;
	}

	/**
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * 
	 * @return
	 */
	public Value getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(Value value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSensitive() {
		return sensitive;
	}

	/**
	 * 
	 * @param sensitive
	 */
	public void setSensitive(boolean sensitive) {
		this.sensitive = sensitive;
	}

	public static StatementParameter createFromUnpack(Unpacker unpacker)
			throws IOException {

		StatementParameter param = new StatementParameter();

		unpacker.readArrayBegin();

		param.setDbType(DbType.fromInt(unpacker.readInt()));

		param.setDirection(ParameterDirection.fromInt(unpacker.readInt()));

		param.setNullable(unpacker.readBoolean());

		param.setSensitive(unpacker.readBoolean());

		param.setIndex(unpacker.readInt());

		param.setSize(unpacker.readInt());

		param.setValue(unpacker.readValue());

		unpacker.readArrayEnd();

		return param;

	}

	public void pack(Packer packer) throws IOException {

//		packer.writeArrayBegin(7);

		packer.write(dbType.getIntVal());

		if (direction == null)
			direction = ParameterDirection.Input;

		packer.write(direction.getIntVal());

		packer.write(nullable);

		packer.write(sensitive);

		packer.write(index);

		packer.write(name);

		packer.write(size);

		packer.write(value);

//		packer.writeArrayEnd();

	}

	public static StatementParameter createFromValue(int index, DbType dbType,
			Value value) {

		StatementParameter param = new StatementParameter();
		param.setIndex(index);
		param.setDbType(dbType);
		param.setValue(value);

		return param;

	}

	public static StatementParameter createFromValue(int index, String name,
			DbType dbType, Value value) {

		StatementParameter param = new StatementParameter();
		param.setIndex(index);
		param.setName(name);
		param.setDbType(dbType);
		param.setValue(value);

		return param;

	}

	@Override
	public int compareTo(StatementParameter o) {
		return this.index - o.getIndex();
	}

	public PreparedStatement setPreparedStatement(PreparedStatement ps)
			throws SQLException {
		
		if(direction == ParameterDirection.Output && ps instanceof CallableStatement){
			((CallableStatement)ps).registerOutParameter(index, DbType.getFromDbType(dbType));
			return ps;
		}

		switch (dbType) {
//		case AnsiString:
//
//			break;
//		case AnsiStringFixedLength:
//			break;
		case Binary:
			ps.setBytes(index, value.asRawValue().getByteArray());
			break;
		case Boolean:
			ps.setBoolean(index, value.asBooleanValue().getBoolean());
			break;
		case Byte:
		case SByte:
			ps.setByte(index, value.asIntegerValue().getByte());
			break;
//		case Currency:
		case Decimal:
			ps.setBigDecimal(index, new BigDecimal(value.asRawValue().getString()));
			break;
//		case Date:
//			ps.setDate(index, new Date(value.asIntegerValue().getLong()));
//			break;
		case DateTime:
//		case DateTime2:
			ps.setTimestamp(index, new Timestamp(value.asIntegerValue()
					.getLong()));
			break;
//		case DateTimeOffset:
//			break;
		case Double:
			ps.setDouble(index, value.asFloatValue().getDouble());
			break;
		case Guid:
			ps.setBytes(index, value.asRawValue().getByteArray());
			break;
		case Int16:
		case UInt16:
			ps.setShort(index, value.asIntegerValue().getShort());
			break;
		case Int32:
		case UInt32:
			ps.setInt(index, value.asIntegerValue().getInt());
			break;
		case Int64:
		case UInt64:
			ps.setLong(index, value.asIntegerValue().getLong());
			break;
//		case Object:
//			break;
		case Single:
			ps.setFloat(index, value.asFloatValue().getFloat());
			break;
		case String:
		case StringFixedLength:
			ps.setString(index, value.asRawValue().getString());
			break;
//		case Time:
//			ps.setTime(index, new Time(value.asIntegerValue().getLong()));
//			break;
//		case VarNumeric:
//			ps.setDouble(index, value.asFloatValue().getDouble());
//			break;
//		case Xml:
//			ps.setString(index, value.asRawValue().getString());
//			break;
		default:
			break;
		}

		return ps;

	}

}
