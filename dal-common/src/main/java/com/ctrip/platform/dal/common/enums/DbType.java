package com.ctrip.platform.dal.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum DbType {

    AnsiString(0),

    Binary(1),

    Byte(2),

    Boolean(3),

//    Currency(4),

//    Date(5),

    DateTime(6),

    Decimal(7),

    Double(8),

    Guid(9),

    Int16(10),

    Int32(11),

    Int64(12),

//    Object(13),

    SByte(14),

    Single(15),

    String(16),

//    Time(17),

    UInt16(18),

    UInt32(19),

    UInt64(20),

//    VarNumeric(21),

//    AnsiStringFixedLength(22),

    StringFixedLength(23);

//    Xml(25),

//    DateTime2(26),

//    DateTimeOffset(27);
    
    private int intVal;
    private static Map<DbType, Integer> netDbTypeToJdbcType = new HashMap<DbType, Integer>();
    private static Map<Integer, DbType> jdbcTypeToNetDbType = new HashMap<Integer, DbType>();
    //DbType转c#语言的数据类型
    private static Map<DbType, String> netDbTypeToNetType = new HashMap<DbType, String>();
    private static final Map<Integer, DbType> intToEnum = new HashMap<Integer, DbType>();

    static{
    	 for(DbType blah : values()) {
         	intToEnum.put(blah.getIntVal(), blah);
         }
    	 
    	netDbTypeToJdbcType.put(AnsiString, java.sql.Types.VARCHAR);
    	netDbTypeToJdbcType.put(Binary, java.sql.Types.VARBINARY);
    	netDbTypeToJdbcType.put(Byte, java.sql.Types.TINYINT);
    	netDbTypeToJdbcType.put(Boolean, java.sql.Types.BIT);
//    	netDbTypeToJdbcType.put(Currency, java.sql.Types.DECIMAL);
//    	netDbTypeToJdbcType.put(Date, java.sql.Types.DATE);
    	netDbTypeToJdbcType.put(DateTime, java.sql.Types.TIMESTAMP);
    	netDbTypeToJdbcType.put(Decimal, java.sql.Types.DECIMAL);
    	netDbTypeToJdbcType.put(Double, java.sql.Types.DOUBLE);
    	netDbTypeToJdbcType.put(Guid, java.sql.Types.CHAR);
    	netDbTypeToJdbcType.put(Int16, java.sql.Types.SMALLINT);
    	netDbTypeToJdbcType.put(Int32, java.sql.Types.INTEGER);
    	netDbTypeToJdbcType.put(Int64, java.sql.Types.BIGINT);
//    	netDbTypeToJdbcType.put(Object, java.sql.Types.JAVA_OBJECT);
    	netDbTypeToJdbcType.put(Single, java.sql.Types.REAL);
    	netDbTypeToJdbcType.put(String, java.sql.Types.NVARCHAR);
//    	netDbTypeToJdbcType.put(Time, java.sql.Types.TIMESTAMP);
//    	netDbTypeToJdbcType.put(AnsiStringFixedLength, java.sql.Types.CHAR);
    	netDbTypeToJdbcType.put(StringFixedLength, java.sql.Types.NCHAR);
//    	netDbTypeToJdbcType.put(Xml, java.sql.Types.LONGNVARCHAR);
//    	netDbTypeToJdbcType.put(DateTime2, java.sql.Types.TIMESTAMP);
//    	netDbTypeToJdbcType.put(DateTimeOffset, microsoft.sql.Types.DATETIMEOFFSET);
    	netDbTypeToJdbcType.put(SByte, java.sql.Types.TINYINT);
    	netDbTypeToJdbcType.put(UInt16, java.sql.Types.SMALLINT);
    	netDbTypeToJdbcType.put(UInt32, java.sql.Types.INTEGER);
    	netDbTypeToJdbcType.put(UInt64, java.sql.Types.BIGINT);
//    	netDbTypeToJdbcType.put(VarNumeric, java.sql.Types.NUMERIC);
    	
    	for(Map.Entry<DbType, Integer> entry : netDbTypeToJdbcType.entrySet()){
    		jdbcTypeToNetDbType.put(entry.getValue(), entry.getKey());
    	}
    	
    	netDbTypeToNetType.put(AnsiString, "string");
    	netDbTypeToNetType.put(Binary, "byte[]");
    	netDbTypeToNetType.put(Byte, "byte");
    	netDbTypeToNetType.put(Boolean, "bool");
//    	netDbTypeToJava.put(Currency, java.sql.Types.DECIMAL);
//    	netDbTypeToJava.put(Date, java.sql.Types.DATE);
    	netDbTypeToNetType.put(DateTime, "DateTime");
    	netDbTypeToNetType.put(Decimal, "decimal");
    	netDbTypeToNetType.put(Double, "double");
    	netDbTypeToNetType.put(Guid, "Guid");
    	netDbTypeToNetType.put(Int16, "short");
    	netDbTypeToNetType.put(Int32, "int");
    	netDbTypeToNetType.put(Int64, "long");
//    	netDbTypeToJava.put(Object, java.sql.Types.JAVA_OBJECT);
    	netDbTypeToNetType.put(Single, "float");
    	netDbTypeToNetType.put(String, "string");
//    	netDbTypeToJava.put(Time, java.sql.Types.TIMESTAMP);
//    	netDbTypeToJava.put(AnsiStringFixedLength, java.sql.Types.CHAR);
    	netDbTypeToNetType.put(StringFixedLength, "char");
//    	netDbTypeToJava.put(Xml, java.sql.Types.LONGNVARCHAR);
//    	netDbTypeToJava.put(DateTime2, java.sql.Types.TIMESTAMP);
//    	netDbTypeToJava.put(DateTimeOffset, microsoft.sql.Types.DATETIMEOFFSET);
    	netDbTypeToNetType.put(SByte, "short");
    	netDbTypeToNetType.put(UInt16, "ushort");
    	netDbTypeToNetType.put(UInt32, "uint");
    	netDbTypeToNetType.put(UInt64, "ulong");
//    	netDbTypeToJava.put(VarNumeric, java.sql.Types.NUMERIC);
    }

	DbType(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
	
	public static DbType fromInt(int symbol) {
        return intToEnum.get(symbol);
    }
	
	public static int getFromDbType(DbType t){
		return netDbTypeToJdbcType.get(t);
	}
	
	public static DbType getDbTypeFromJdbcType(Integer i){
		return jdbcTypeToNetDbType.get(i);
	}
	
	public static String getCSharpType(DbType t){
		return netDbTypeToNetType.get(t);
	}
    
}
