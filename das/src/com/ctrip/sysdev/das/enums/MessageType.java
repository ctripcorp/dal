package com.ctrip.sysdev.das.enums;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {

	SQL(0), // T-SQL
	SP(1); // Stored Procedure

	private int intVal;

	MessageType(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
	
	 // Implementing a fromString method on an enum type
    private static final Map<Integer, MessageType> intToEnum = new HashMap<Integer, MessageType>();
    static {
        // Initialize map from constant name to enum constant
        for(MessageType blah : values()) {
        	intToEnum.put(blah.getIntVal(), blah);
        }
    }
    
    public static MessageType fromInt(int symbol) {
        return intToEnum.get(symbol);
    }

}
