package com.ctrip.platform.dal.daogen.enums;

import java.util.HashMap;
import java.util.Map;

public enum ParameterDirection {

	Input(1),
	Output(2),
	InputOutput(3),
	ReturnValue(6);

	private int intVal;

	ParameterDirection(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
	
	 // Implementing a fromString method on an enum type
    private static final Map<Integer, ParameterDirection> intToEnum = new HashMap<Integer, ParameterDirection>();
    static {
        // Initialize map from constant name to enum constant
        for(ParameterDirection blah : values()) {
        	intToEnum.put(blah.getIntVal(), blah);
        }
    }
    
    public static ParameterDirection fromInt(int symbol) {
        return intToEnum.get(symbol);
    }
	
}
