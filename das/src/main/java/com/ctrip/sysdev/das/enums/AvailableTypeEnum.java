package com.ctrip.sysdev.das.enums;

import java.util.HashMap;
import java.util.Map;

public enum AvailableTypeEnum {
	
	BOOL(0),
	BYTE(1),
	SHORT(2),
	INT(3),
	LONG(4),
	FLOAT(5),
	DOUBLE(6),
	DECIMAL(7),
//	CHAR(8),
	STRING(8),
	DATETIME(9),
	BYTEARR(10); 

	private int intVal;

	AvailableTypeEnum(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
	
	 // Implementing a fromString method on an enum type
    private static final Map<Integer, AvailableTypeEnum> intToEnum = new HashMap<Integer, AvailableTypeEnum>();
    static {
        // Initialize map from constant name to enum constant
        for(AvailableTypeEnum blah : values()) {
        	intToEnum.put(blah.getIntVal(), blah);
        }
    }
    
    public static AvailableTypeEnum fromInt(int symbol) {
        return intToEnum.get(symbol);
    }

}
