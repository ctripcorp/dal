package com.ctrip.sysdev.das.enums;

import java.util.HashMap;
import java.util.Map;

public enum ActionType {
	
	SELECT(0),
	INSERT(1),
	UPDATE(2),
	DELETE(3);

	private int intVal;

	ActionType(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
	
	 // Implementing a fromString method on an enum type
    private static final Map<Integer, ActionType> intToEnum = new HashMap<Integer, ActionType>();
    static {
        // Initialize map from constant name to enum constant
        for(ActionType blah : values()) {
        	intToEnum.put(blah.getIntVal(), blah);
        }
    }
    
    public static ActionType fromInt(int symbol) {
        return intToEnum.get(symbol);
    }
}
