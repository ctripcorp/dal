package com.ctrip.platform.dao.enums;

import java.util.HashMap;
import java.util.Map;

public enum ParameterType {
	
	NULL(0), 
	BOOL(1),
	BYTE(2),
	SHORT(3),
	INT(4),
	LONG(5),
	FLOAT(6),
	DOUBLE(7),
	DECIMAL(8),
	STRING(9),
	DATETIME(10),
	BYTEARRAY(11),
	INTARRAY(12),
	STRINGARRAY(13);
	
	private int intVal;

	ParameterType(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
	
	 // Implementing a fromString method on an enum type
    private static final Map<Integer, ParameterType> intToEnum = new HashMap<Integer, ParameterType>();
    static {
        // Initialize map from constant name to enum constant
        for(ParameterType blah : values()) {
        	intToEnum.put(blah.getIntVal(), blah);
        }
    }
    
    public static ParameterType fromInt(int symbol) {
        return intToEnum.get(symbol);
    }

}
