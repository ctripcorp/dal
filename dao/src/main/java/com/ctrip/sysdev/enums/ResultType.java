package com.ctrip.sysdev.enums;

import java.util.HashMap;
import java.util.Map;

public enum ResultType {
	
	RETRIEVE(0),
	CUD(1);
	
	private int intVal;

	ResultType(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
	
    private static final Map<Integer, ResultType> intToEnum = new HashMap<Integer, ResultType>();
    static {
        // Initialize map from constant name to enum constant
        for(ResultType blah : values()) {
        	intToEnum.put(blah.getIntVal(), blah);
        }
    }
    
    public static ResultType fromInt(int symbol) {
        return intToEnum.get(symbol);
    }

}
