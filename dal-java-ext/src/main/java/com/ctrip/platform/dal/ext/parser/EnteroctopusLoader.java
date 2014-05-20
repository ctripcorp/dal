package com.ctrip.platform.dal.ext.parser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.fx.enteroctopus.common.jpa.DBColumn;

@Deprecated
public class EnteroctopusLoader extends Loader {
	private static HashMap<Class<?>, Class<?>> unboxingMap = null;
	private static HashMap<Class<?>, Object> defaultMap = null;

	private static Map<Class<?>, Integer> java2SqlTypeMaper = null;
	
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
		
		java2SqlTypeMaper = new HashMap<Class<?>, Integer>();
		java2SqlTypeMaper.put(Integer.class, Types.INTEGER);
		java2SqlTypeMaper.put(int.class, Types.INTEGER);
		java2SqlTypeMaper.put(String.class, Types.VARCHAR);
		java2SqlTypeMaper.put(Timestamp.class, Types.TIMESTAMP);
		java2SqlTypeMaper.put(byte[].class, Types.BINARY);
		java2SqlTypeMaper.put(Float.class, Types.FLOAT);
		java2SqlTypeMaper.put(float.class, Types.FLOAT);
		java2SqlTypeMaper.put(Double.class, Types.DOUBLE);
		java2SqlTypeMaper.put(double.class, Types.DOUBLE);
		java2SqlTypeMaper.put(Long.class, Types.BIGINT);
		java2SqlTypeMaper.put(long.class, Types.BIGINT);
		
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
	public void setValue(Field field, Object entity, Object value)
			throws ReflectiveOperationException {
		Class<?> type = field.getType();
		Object defaultVal = defaultMap.containsKey(type) ? defaultMap.get(type) : null;
		if (type.isPrimitive()) {
			if (value == null) {
				field.set(entity, defaultVal);
				return;
			}
			if (type.equals(unboxingMap.get(value.getClass()))) {
				field.set(entity, value);
				return;
			}
			if (value instanceof Number) {
				if (type.equals(boolean.class) || type.equals(char.class)) {
					field.set(entity, defaultVal);
					return;
				}
				field.set(entity, numberValue(type, value));
			}
			if (value instanceof Date && type.equals(long.class)) {
				field.set(entity, Long.valueOf(((Date) value).getTime()));
			}
			field.set(entity, defaultVal);
			return;
		}
		if (value == null || value.getClass().equals(type)) {
			field.set(entity, value);
			return;
		}
		if (Number.class.isAssignableFrom(type) && value instanceof Number) {
			if (type.equals(BigInteger.class)) {
				field.set(entity, BigInteger.valueOf(((Number) value).longValue()));
				return;
			}
			if (type.equals(BigDecimal.class)) {
				field.set(entity, BigDecimal.valueOf(((Number) value).doubleValue()));
				return;
			}
			Class<?> primitiveType = unboxingMap.get(type);
			if (primitiveType == null) {
				field.set(entity, null);
				return;
			}
			field.set(entity, type.getDeclaredMethod("valueOf", primitiveType).invoke(
					null, numberValue(primitiveType, value))); ;
		}
	}

	public Object getValue(Field field, Object entity)
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

	@Override
	public int getSqlType(Class<?> javaType) {
		return java2SqlTypeMaper.get(javaType);
	}
}
