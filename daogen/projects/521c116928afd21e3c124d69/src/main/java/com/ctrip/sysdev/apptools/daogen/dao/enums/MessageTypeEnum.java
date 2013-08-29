package com.ctrip.sysdev.apptools.daogen.dao.enums;

import java.util.HashMap;
import java.util.Map;

public enum MessageTypeEnum {

	SQL(0), // T-SQL
	SP(1); // Stored Procedure

	private int intVal;

	MessageTypeEnum(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
	
	 // Implementing a fromString method on an enum type
    private static final Map<Integer, MessageTypeEnum> intToEnum = new HashMap<Integer, MessageTypeEnum>();
    static {
        // Initialize map from constant name to enum constant
        for(MessageTypeEnum blah : values()) {
        	intToEnum.put(blah.getIntVal(), blah);
        }
    }
    
    public static MessageTypeEnum fromInt(int symbol) {
        return intToEnum.get(symbol);
    }

}
