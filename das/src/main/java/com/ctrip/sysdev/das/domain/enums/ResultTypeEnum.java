package com.ctrip.sysdev.das.domain.enums;

import java.util.HashMap;
import java.util.Map;

public enum ResultTypeEnum {
	
	RETRIEVE(0),
	CUD(1);
	
	private int intVal;

	ResultTypeEnum(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
	
    private static final Map<Integer, ResultTypeEnum> intToEnum = new HashMap<Integer, ResultTypeEnum>();
    static {
        // Initialize map from constant name to enum constant
        for(ResultTypeEnum blah : values()) {
        	intToEnum.put(blah.getIntVal(), blah);
        }
    }
    
    public static ResultTypeEnum fromInt(int symbol) {
        return intToEnum.get(symbol);
    }

}
