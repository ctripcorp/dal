package com.ctrip.sysdev.das.enums;

import java.util.HashMap;
import java.util.Map;

public enum ActionTypeEnum {
	
	SELECT(0),
	INSERT(1),
	UPDATE(2),
	DELETE(3);

	private int intVal;

	ActionTypeEnum(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
	
	 // Implementing a fromString method on an enum type
    private static final Map<Integer, ActionTypeEnum> intToEnum = new HashMap<Integer, ActionTypeEnum>();
    static {
        // Initialize map from constant name to enum constant
        for(ActionTypeEnum blah : values()) {
        	intToEnum.put(blah.getIntVal(), blah);
        }
    }
    
    public static ActionTypeEnum fromInt(int symbol) {
        return intToEnum.get(symbol);
    }
}
