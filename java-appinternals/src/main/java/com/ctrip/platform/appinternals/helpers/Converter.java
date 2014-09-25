package com.ctrip.platform.appinternals.helpers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Converter {
	@SuppressWarnings("rawtypes")
	private static Map<Class, Class> primitiveMap = new HashMap<Class, Class>();
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss");
	static {
		primitiveMap.put(boolean.class, Boolean.class);
		primitiveMap.put(byte.class, Byte.class);
		primitiveMap.put(char.class, Character.class);
		primitiveMap.put(short.class, Short.class);
		primitiveMap.put(int.class, Integer.class);
		primitiveMap.put(long.class, Long.class);
		primitiveMap.put(float.class, Float.class);
		primitiveMap.put(double.class, Double.class);
	}

	public static Object convert(String value, Class<?> destClass) throws Exception{
		if ((value == null) || "".equals(value) || destClass.equals(String.class)) {
			return value;
		}
		if(destClass.equals(Date.class)){
			return sdf.parse(value);
		}
		if (primitiveMap.containsKey(destClass)) {
			destClass = primitiveMap.get(destClass);
		}

		try {
			Method m = destClass.getMethod("valueOf", String.class);
			int mods = m.getModifiers();
			if (Modifier.isStatic(mods) && Modifier.isPublic(mods)) {
				return m.invoke(null, value);
			}
		} catch (Exception e) {
			throw new Exception(String.format("Convert value[%s] of [%s] exception.", 
					value, destClass));
		}

		return value;
	}
}
