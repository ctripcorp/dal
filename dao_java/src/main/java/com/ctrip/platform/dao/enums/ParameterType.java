package com.ctrip.platform.dao.enums;

import java.util.HashMap;
import java.util.Map;

public enum ParameterType {
	
	//NULL(0), 
	BOOL(0),
	BYTE(1),
	SHORT(2),
	INT(3),
	LONG(4),
	FLOAT(5),
	DOUBLE(6),
	DECIMAL(7),
	STRING(8),
	DATETIME(9),
	BYTEARRAY(10),
	INTARRAY(11),
	STRINGARRAY(12),
	PARAMARRAY(13);
	
	private int intVal;

	ParameterType(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
	
	 // Implementing a fromString method on an enum type
    private static final Map<Integer, ParameterType> intToEnum = new HashMap<Integer, ParameterType>();
    private static final Map<ParameterType, Integer> enumToSqlType = new HashMap<ParameterType, Integer>();
    static {
        // Initialize map from constant name to enum constant
        for(ParameterType blah : values()) {
        	intToEnum.put(blah.getIntVal(), blah);
        }
        enumToSqlType.put(BOOL, java.sql.Types.BOOLEAN);
        enumToSqlType.put(BYTE, java.sql.Types.TINYINT);
        enumToSqlType.put(SHORT, java.sql.Types.SMALLINT);
        enumToSqlType.put(INT, java.sql.Types.INTEGER);
        enumToSqlType.put(LONG, java.sql.Types.BIGINT);
        enumToSqlType.put(FLOAT, java.sql.Types.REAL);
        enumToSqlType.put(DOUBLE, java.sql.Types.DOUBLE);
        enumToSqlType.put(DECIMAL, java.sql.Types.DECIMAL);
        enumToSqlType.put(STRING, java.sql.Types.VARCHAR);
        enumToSqlType.put(DATETIME, java.sql.Types.TIMESTAMP);
        enumToSqlType.put(BYTEARRAY, java.sql.Types.BINARY);
    }
    
    public static ParameterType fromInt(int symbol) {
        return intToEnum.get(symbol);
    }
    
    public static int getSqlType(ParameterType param){
    	return enumToSqlType.get(param);
    }

}
