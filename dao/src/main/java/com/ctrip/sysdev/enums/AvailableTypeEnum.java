package com.ctrip.sysdev.enums;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
    private static final Map<Integer, AvailableTypeEnum> intToEnum = 
    		new TreeMap<Integer, AvailableTypeEnum>();
    private static final Map<Class<?>, AvailableTypeEnum> classToEnum = 
    		new  HashMap<Class<?>, AvailableTypeEnum>();
    
    static {
        // Initialize map from constant name to enum constant
        for(AvailableTypeEnum blah : values()) {
        	intToEnum.put(blah.getIntVal(), blah);
        }
        classToEnum.put(Boolean.class, BOOL);
        classToEnum.put(Byte.class, BYTE);
        classToEnum.put(Short.class, SHORT);
        classToEnum.put(Integer.class, INT);
        classToEnum.put(Long.class, LONG);
        classToEnum.put(Float.class, FLOAT);
        classToEnum.put(Double.class, DOUBLE);
        classToEnum.put(BigDecimal.class, DECIMAL);
        classToEnum.put(String.class, STRING);
        classToEnum.put(Timestamp.class, DATETIME);
        classToEnum.put(byte[].class, BYTEARR);
    }
    
    public static AvailableTypeEnum fromInt(int symbol) {
        return intToEnum.get(symbol);
    }
    
    public static AvailableTypeEnum fromClass(Class<?> classType){
    	return classToEnum.get(classType);
    }

}
