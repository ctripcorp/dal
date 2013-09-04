package com.ctrip.sysdev.das.domain.msg;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.msgpack.type.Value;

import com.ctrip.sysdev.das.domain.enums.AvailableTypeEnum;

/**
 * 
 * @author gawu
 * 
 */
public class AvailableType implements Comparable {

	public AvailableType() {

	}

	/**
	 * Initialize AvailableType with a boolean value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	public AvailableType(int paramIndex, boolean value) {
		this.paramIndex = paramIndex;
		this.currentType = AvailableTypeEnum.BOOL;
		this.bool_arg = value;
	}

	/**
	 * Initialize AvailableType with a byte value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	public AvailableType(int paramIndex, byte value) {
		this.paramIndex = paramIndex;
		this.currentType = AvailableTypeEnum.BYTE;
		this.byte_arg = value;
	}

	/**
	 * Initialize AvailableType with a short value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	public AvailableType(int paramIndex, short value) {
		this.paramIndex = paramIndex;
		this.currentType = AvailableTypeEnum.SHORT;
		this.short_arg = value;
	}

	/**
	 * Initialize AvailableType with an int value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	public AvailableType(int paramIndex, int value) {
		this.paramIndex = paramIndex;
		this.currentType = AvailableTypeEnum.INT;
		this.int_arg = value;
	}

	/**
	 * Initialize AvailableType with a long value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	public AvailableType(int paramIndex, long value) {
		this.paramIndex = paramIndex;
		this.currentType = AvailableTypeEnum.LONG;
		this.long_arg = value;
	}

	/**
	 * Initialize AvailableType with a float value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	public AvailableType(int paramIndex, float value) {
		this.paramIndex = paramIndex;
		this.currentType = AvailableTypeEnum.FLOAT;
		this.float_arg = value;
	}

	/**
	 * Initialize AvailableType with a double value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	public AvailableType(int paramIndex, double value) {
		this.paramIndex = paramIndex;
		this.currentType = AvailableTypeEnum.DOUBLE;
		this.double_arg = value;
	}

	/**
	 * Initialize AvailableType with a BigDecimal value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	public AvailableType(int paramIndex, BigDecimal value) {
		this.paramIndex = paramIndex;
		this.currentType = AvailableTypeEnum.DECIMAL;
		this.decimal_arg = value;
	}

	/**
	 * Initialize AvailableType with a String value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	public AvailableType(int paramIndex, String value) {
		this.paramIndex = paramIndex;
		this.currentType = AvailableTypeEnum.STRING;
		this.string_arg = value;
	}

	/**
	 * Initialize AvailableType with a Timestamp value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	public AvailableType(int paramIndex, Timestamp value) {
		this.paramIndex = paramIndex;
		this.currentType = AvailableTypeEnum.DATETIME;
		this.datetime_arg = value;
	}

	/**
	 * Initialize AvailableType with a byte[] value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	public AvailableType(int paramIndex, byte[] value) {
		this.paramIndex = paramIndex;
		this.currentType = AvailableTypeEnum.BYTEARR;
		this.bytearr_arg = value;
	}

	// /**
	// * Initialize AvailableType with an Object[] value
	// *
	// * @param paramIndex
	// * @param value
	// */
	// public AvailableType(int paramIndex, Object[] value) {
	// this.paramIndex = paramIndex;
	// this.currentType = AvailableTypeEnum.ARRAY;
	// this.array_arg = value;
	// }

	/**
	 * Initialize AvailableType with an Object value
	 * 
	 * @param paramIndex
	 * @param value
	 */
	public AvailableType(int paramIndex, Object value) {
		this.paramIndex = paramIndex;
		this.currentType = AvailableTypeEnum.OBJECT;
		this.object_arg = value;
	}

	public int paramIndex;
	public AvailableTypeEnum currentType;

	public boolean bool_arg;
	public byte byte_arg;
	public short short_arg;
	public int int_arg;
	public long long_arg;
	public float float_arg;
	public double double_arg;
	public BigDecimal decimal_arg;
	// public char char_arg;
	public String string_arg;
	public Timestamp datetime_arg;
	public byte[] bytearr_arg;
	// public Object[] array_arg;
	public Object object_arg;

	/**
	 * Get the class of current active variable
	 * 
	 * @return
	 */
	public Class<?> getCurrentClass() {
		switch (currentType) {
		case BOOL:
			return boolean.class;
		case BYTE:
			return byte.class;
		case SHORT:
			return short.class;
		case INT:
			return int.class;
		case LONG:
			return long.class;
		case FLOAT:
			return float.class;
		case DOUBLE:
			return double.class;
		case DECIMAL:
			return BigDecimal.class;
		case STRING:
			return String.class;
		case DATETIME:
			return Timestamp.class;
		case BYTEARR:
			return byte[].class;
		default:
			return Object.class;
		}
	}

	/**
	 * Set the current active variable to the PreparedStatement
	 * 
	 * @param ps
	 * @return
	 */
	public void setPreparedStatement(PreparedStatement ps) throws Exception {

		switch (currentType) {
		case BOOL:
			ps.setBoolean(paramIndex, bool_arg);
			break;
		case BYTE:
			ps.setByte(paramIndex, byte_arg);
			break;
		case SHORT:
			ps.setShort(paramIndex, short_arg);
			break;
		case INT:
			ps.setInt(paramIndex, int_arg);
			break;
		case LONG:
			ps.setLong(paramIndex, long_arg);
			break;
		case FLOAT:
			ps.setFloat(paramIndex, float_arg);
			break;
		case DOUBLE:
			ps.setDouble(paramIndex, double_arg);
			break;
		case DECIMAL:
			ps.setBigDecimal(paramIndex, decimal_arg);
			break;
		case STRING:
			ps.setString(paramIndex, string_arg);
			break;
		case DATETIME:
			ps.setTimestamp(paramIndex, datetime_arg);
			break;
		case BYTEARR:
			ps.setBytes(paramIndex, bytearr_arg);
			break;
		// case ARRAY:
		// for (Object obj : array_arg) {
		// setPreparedStatementByClass(obj, ps);
		// paramIndex++;
		// }
		// break;
		default:
			if (object_arg != null && object_arg.getClass().isArray()) {

				for (int i = 0; i < Array.getLength(object_arg); i++) {

					setPreparedStatementByClass((Value)Array.get(object_arg, i), ps);
					paramIndex++;
				}
			} else {
				ps.setObject(paramIndex, object_arg);
			}
			break;
		}
	}

	public void setPreparedStatementByClass(Value obj, PreparedStatement ps)
			throws SQLException {
		
		if(obj.isBooleanValue()){
			ps.setBoolean(paramIndex, obj.asBooleanValue().getBoolean());
		}else if(obj.isIntegerValue()){
			ps.setInt(paramIndex, obj.asIntegerValue().getInt());
		}else if(obj.isFloatValue()){
			ps.setFloat(paramIndex, obj.asFloatValue().getFloat());
		}else if(obj.isRawValue()){
			ps.setString(paramIndex, obj.asRawValue().getString());
		}
		
		// AvailableTypeEnum correspondingEnum = AvailableTypeEnum.fromClass(obj
		// .getClass());

//		switch (correspondingEnum) {
//		case BOOL:
//			ps.setBoolean(paramIndex, (Boolean) obj);
//			break;
//		case BYTE:
//			ps.setByte(paramIndex, (Byte) obj);
//			break;
//		case SHORT:
//			ps.setShort(paramIndex, (Short) obj);
//			break;
//		case INT:
//			ps.setInt(paramIndex, (Integer) obj);
//			break;
//		case LONG:
//			ps.setLong(paramIndex, (Long) obj);
//			break;
//		case FLOAT:
//			ps.setFloat(paramIndex, (Float) obj);
//			break;
//		case DOUBLE:
//			ps.setDouble(paramIndex, (Double) obj);
//			break;
//		case DECIMAL:
//			ps.setBigDecimal(paramIndex, (BigDecimal) obj);
//			break;
//		case STRING:
//			ps.setString(paramIndex, (String) obj);
//			break;
//		case DATETIME:
//			ps.setTimestamp(paramIndex, (Timestamp) obj);
//			break;
//		case BYTEARR:
//			ps.setBytes(paramIndex, (byte[]) obj);
//			break;
//		default:
//			ps.setObject(paramIndex, obj);
//			break;
//		}

	}

	public void setCallableStatement(CallableStatement cs) throws Exception {

		switch (currentType) {
		case BOOL:
			cs.setBoolean(paramIndex, bool_arg);
			break;
		case BYTE:
			cs.setByte(paramIndex, byte_arg);
			break;
		case SHORT:
			cs.setShort(paramIndex, short_arg);
			break;
		case INT:
			cs.setInt(paramIndex, int_arg);
			break;
		case LONG:
			cs.setLong(paramIndex, long_arg);
			break;
		case FLOAT:
			cs.setFloat(paramIndex, float_arg);
			break;
		case DOUBLE:
			cs.setDouble(paramIndex, double_arg);
			break;
		case DECIMAL:
			cs.setBigDecimal(paramIndex, decimal_arg);
			break;
		case STRING:
			cs.setString(paramIndex, string_arg);
			break;
		case DATETIME:
			cs.setTimestamp(paramIndex, datetime_arg);
			break;
		case BYTEARR:
			cs.setBytes(paramIndex, bytearr_arg);
			break;
		default:
			cs.setObject(paramIndex, object_arg);
			break;
		}
	}

	/**
	 * Get the available type according to the available type o
	 * 
	 * @param rs
	 * @param currentEnum
	 * @param index
	 * @return
	 */
	public static AvailableType getResultSet(ResultSet rs,
			AvailableTypeEnum currentEnum, int index) throws Exception {

		AvailableType at = new AvailableType();
		at.currentType = currentEnum;

		switch (currentEnum) {
		case BOOL:
			at.bool_arg = rs.getBoolean(index);
			break;
		case BYTE:
			at.byte_arg = rs.getByte(index);
			break;
		case SHORT:
			at.short_arg = rs.getShort(index);
			break;
		case INT:
			at.int_arg = rs.getInt(index);
			break;
		case LONG:
			at.long_arg = rs.getLong(index);
			break;
		case FLOAT:
			at.float_arg = rs.getFloat(index);
			break;
		case DOUBLE:
			at.double_arg = rs.getDouble(index);
			break;
		case DECIMAL:
			at.decimal_arg = rs.getBigDecimal(index);
			break;
		case STRING:
			at.string_arg = rs.getString(index);
			break;
		case DATETIME:
			at.datetime_arg = rs.getTimestamp(index);
			break;
		case BYTEARR:
			at.bytearr_arg = rs.getBytes(index);
			break;
		default:
			at.object_arg = rs.getObject(index);
			break;
		}

		return at;

	}

	@Override
	public String toString() {
		switch (currentType) {
		case BOOL:
			return String.valueOf(bool_arg);
		case BYTE:
			return String.valueOf(byte_arg);
		case SHORT:
			return String.valueOf(short_arg);
		case INT:
			return String.valueOf(int_arg);
		case LONG:
			return String.valueOf(long_arg);
		case FLOAT:
			return String.valueOf(float_arg);
		case DOUBLE:
			return String.valueOf(double_arg);
		case DECIMAL:
			return String.valueOf(decimal_arg);
		case STRING:
			return string_arg;
		case DATETIME:
			return datetime_arg.toString();
		case BYTEARR:
			return String.valueOf(bytearr_arg);
		default:
			return object_arg.toString();
		}
	}

	@Override
	public int compareTo(Object anotherAvailableType) throws ClassCastException {
		if (!(anotherAvailableType instanceof AvailableType))
			throw new ClassCastException("An AvailableType object expected.");
		int anotherParamIndex = ((AvailableType) anotherAvailableType).paramIndex;
		return this.paramIndex - anotherParamIndex;
	}

}
