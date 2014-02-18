package com.ctrip.platform.dal.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum DbType {

//    AnsiString(0),

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
    private static Map<DbType, Integer> netDbTypeToJava = new HashMap<DbType, Integer>();
    private static final Map<Integer, DbType> intToEnum = new HashMap<Integer, DbType>();

    static{
    	 for(DbType blah : values()) {
         	intToEnum.put(blah.getIntVal(), blah);
         }
    	 
//    	netDbTypeToJava.put(AnsiString, java.sql.Types.VARCHAR);
    	netDbTypeToJava.put(Binary, java.sql.Types.VARBINARY);
    	netDbTypeToJava.put(Byte, java.sql.Types.TINYINT);
    	netDbTypeToJava.put(Boolean, java.sql.Types.BIT);
//    	netDbTypeToJava.put(Currency, java.sql.Types.DECIMAL);
//    	netDbTypeToJava.put(Date, java.sql.Types.DATE);
    	netDbTypeToJava.put(DateTime, java.sql.Types.TIMESTAMP);
    	netDbTypeToJava.put(Decimal, java.sql.Types.DECIMAL);
    	netDbTypeToJava.put(Double, java.sql.Types.DOUBLE);
    	netDbTypeToJava.put(Guid, java.sql.Types.CHAR);
    	netDbTypeToJava.put(Int16, java.sql.Types.SMALLINT);
    	netDbTypeToJava.put(Int32, java.sql.Types.INTEGER);
    	netDbTypeToJava.put(Int64, java.sql.Types.BIGINT);
//    	netDbTypeToJava.put(Object, java.sql.Types.JAVA_OBJECT);
    	netDbTypeToJava.put(Single, java.sql.Types.REAL);
    	netDbTypeToJava.put(String, java.sql.Types.NVARCHAR);
//    	netDbTypeToJava.put(Time, java.sql.Types.TIMESTAMP);
//    	netDbTypeToJava.put(AnsiStringFixedLength, java.sql.Types.CHAR);
    	netDbTypeToJava.put(StringFixedLength, java.sql.Types.NCHAR);
//    	netDbTypeToJava.put(Xml, java.sql.Types.LONGNVARCHAR);
//    	netDbTypeToJava.put(DateTime2, java.sql.Types.TIMESTAMP);
//    	netDbTypeToJava.put(DateTimeOffset, microsoft.sql.Types.DATETIMEOFFSET);
    	netDbTypeToJava.put(SByte, java.sql.Types.TINYINT);
    	netDbTypeToJava.put(UInt16, java.sql.Types.SMALLINT);
    	netDbTypeToJava.put(UInt32, java.sql.Types.INTEGER);
    	netDbTypeToJava.put(UInt64, java.sql.Types.BIGINT);
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
		return netDbTypeToJava.get(t);
	}
    
}
