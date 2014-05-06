package com.ctrip.platform.dal.ext.parser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;

import com.ctrip.fx.enteroctopus.common.jpa.DBColumn;

public class EnteroctopusLoader extends Loader {
	private static HashMap<Class<?>, Class<?>> unboxingMap = null;
	private static HashMap<Class<?>, Object> defaultMap = null;

	private static final Class<?>[] UNBOXED_CLASS = { boolean.class,
			byte.class, char.class, short.class, int.class, long.class,
			float.class, double.class, };

	private static final Class<?>[] BOXED_CLASS = { Boolean.class, Byte.class,
			Character.class, Short.class, Integer.class, Long.class,
			Float.class, Double.class, };

	private static final Object[] DEFAULT_VALUE = { Boolean.FALSE,
			Byte.valueOf((byte) 0), Character.valueOf((char) 0),
			Short.valueOf((short) 0), Integer.valueOf(0), Long.valueOf(0),
			Float.valueOf(0), Double.valueOf(0), };

	private static final int CLASS_NUM = UNBOXED_CLASS.length;

	static {
		unboxingMap = new HashMap<Class<?>, Class<?>>();
		defaultMap = new HashMap<Class<?>, Object>();
		for (int i = 0; i < CLASS_NUM; i++) {
			unboxingMap.put(BOXED_CLASS[i], UNBOXED_CLASS[i]);
			defaultMap.put(UNBOXED_CLASS[i], DEFAULT_VALUE[i]);
			defaultMap.put(BOXED_CLASS[i], DEFAULT_VALUE[i]);
		}
		defaultMap.put(String.class, "");
	}

	@Override
	public Object load(Field field, Object value)
			throws ReflectiveOperationException {
		Class<?> type = field.getType();
		Object defaultVal = defaultMap.containsKey(type) ? defaultMap.get(type) : null;
		if (type.isPrimitive()) {
			if (value == null) {
				return defaultVal;
			}
			if (type.equals(unboxingMap.get(value.getClass()))) {
				return value;
			}
			if (value instanceof Number) {
				if (type.equals(boolean.class) || type.equals(char.class)) {
					return defaultVal;
				}
				return numberValue(type, value);
			}
			if (value instanceof Date && type.equals(long.class)) {
				return Long.valueOf(((Date) value).getTime());
			}
			return defaultVal;
		}
		if (value == null || value.getClass().equals(type)) {
			return value;
		}
		if (Number.class.isAssignableFrom(type) && value instanceof Number) {
			if (type.equals(BigInteger.class)) {
				return BigInteger.valueOf(((Number) value).longValue());
			}
			if (type.equals(BigDecimal.class)) {
				return BigDecimal.valueOf(((Number) value).doubleValue());
			}
			Class<?> primitiveType = unboxingMap.get(type);
			if (primitiveType == null) {
				return null;
			}
			return type.getDeclaredMethod("valueOf", primitiveType).invoke(
					null, numberValue(primitiveType, value));
		}
		return null;
	}

	public Object save(Field field, Object entity)
			throws ReflectiveOperationException {
		Class<?> type = field.getType();		
		Object value = defaultMap.containsKey(type) ? defaultMap.get(type) : null;
		DBColumn annot = field.getAnnotation(DBColumn.class);
		Constructor<?> constr = annot.wrapperType().getConstructor(field.getType());
		long now_ = (new Date()).getTime();
		if (annot.isTimeStamp()) {
			field.set(entity, now_);
			value = now_;
		} else {
			value = field.get(entity);
		}
		if (value == null) {
			if (annot.nullable()) {
				return null;
			}
			if (constr == null) {

				return value;
			}
			return constr.newInstance(value);
		}
		if (constr != null) {
			return constr.newInstance(value);
		}
		int limit = annot.length();
		if (limit == 0 || !(value instanceof String)) {
			return value;
		}
		String str = (String) value;
		if (str.length() > limit) {
			str = str.substring(0, limit);
		}
		return str;
	}

	private Object numberValue(Class<?> type, Object value)
			throws ReflectiveOperationException {
		return value.getClass().getMethod(type.getName() + "Value")
				.invoke(value);
	}
}
