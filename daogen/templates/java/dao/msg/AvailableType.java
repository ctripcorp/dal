package {{product_line}}.{{domain}}.{{app_name}}.dao.msg;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import {{product_line}}.{{domain}}.{{app_name}}.dao.enums.AvailableTypeEnum;

/**
 * 
 * @author gawu
 *
 */
public class AvailableType{
	
	public AvailableType(){
		
	}
	
	public <T> AvailableType(T value){
		this(1, value);
	}
	
	public <T> AvailableType(int paramIndex, T value){
		this.paramIndex = paramIndex;
		
		this.currentType = AvailableTypeEnum.fromClass(value.getClass());
		switch(currentType){
		case BOOL:
			this.bool_arg = (Boolean) value;
			break;
		case BYTE:
			this.byte_arg = (Byte) value;
			break;
		case SHORT:
			this.short_arg = (Short) value;
			break;
		case INT:
			this.int_arg = (Integer) value;
			break;
		case LONG:
			this.long_arg = (Long) value;
			break;
		case FLOAT:
			this.float_arg = (Float) value;
			break;
		case DOUBLE:
			this.double_arg = (Double) value;
			break;
		case DECIMAL:
			this.decimal_arg = (BigDecimal) value;
			break;
		case STRING:
			this.string_arg = (String) value;
			break;
		case DATETIME:
			this.datetime_arg = (Timestamp) value;
			break;
		case BYTEARR:
			this.bytearr_arg = (byte[]) value;
			break;
		default:
			this.object_arg = value;
			break;
		}
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
//	public char char_arg;
	public String string_arg;
	public Timestamp datetime_arg;
	public byte[] bytearr_arg;
	public Object object_arg;
	
	/**
	 * Get the class of current active variable
	 * @return
	 */
	public Class<?> getCurrentClass(){
		switch(currentType){
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
	 * @param ps
	 * @return
	 */
	public void setPreparedStatement(PreparedStatement ps)
			throws Exception{
		
		switch(currentType){
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
		default:
			ps.setObject(paramIndex, object_arg);
			break;
		}
	}
	
	public void setCallableStatement(CallableStatement cs)
			throws Exception{
		
		switch(currentType){
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
	 * @param rs
	 * @param currentEnum
	 * @param index
	 * @return
	 */
	public static AvailableType getResultSet(ResultSet rs, 
			AvailableTypeEnum currentEnum, int index) 
					throws Exception{
		
		AvailableType at = new AvailableType();
		at.currentType = currentEnum;
		
		switch(currentEnum){
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
		switch(currentType){
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
	
}
