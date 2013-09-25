package com.ctrip.sysdev.das.domain.enums;

import java.util.HashMap;
import java.util.Map;

public enum OperationType {
	
	Default(0),
	Read(1),
	Write(2);

	private int intVal;

	OperationType(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
	
	 // Implementing a fromString method on an enum type
    private static final Map<Integer, OperationType> intToEnum = new HashMap<Integer, OperationType>();
    static {
        // Initialize map from constant name to enum constant
        for(OperationType blah : values()) {
        	intToEnum.put(blah.getIntVal(), blah);
        }
    }
    
    public static OperationType fromInt(int symbol) {
        return intToEnum.get(symbol);
    }
}
