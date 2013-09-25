package com.ctrip.sysdev.das.domain.enums;

import java.util.HashMap;
import java.util.Map;

public enum StatementType {

	SQL(0), // T-SQL
	StoredProcedure(1); // Stored Procedure

	private int intVal;

	StatementType(int intVal) {
		this.intVal = intVal;
	}

	public int getIntVal() {
		return intVal;
	}
	
	 // Implementing a fromString method on an enum type
    private static final Map<Integer, StatementType> intToEnum = new HashMap<Integer, StatementType>();
    static {
        // Initialize map from constant name to enum constant
        for(StatementType blah : values()) {
        	intToEnum.put(blah.getIntVal(), blah);
        }
    }
    
    public static StatementType fromInt(int symbol) {
        return intToEnum.get(symbol);
    }

}
