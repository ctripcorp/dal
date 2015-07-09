package com.ctrip.platform.dal.ext.parser;

import java.lang.reflect.Field;

/**
 * Load/Save the value from/to specified Entity field
 * @author wcyuan
 * @version 2014-04-24
 */
public abstract class Loader {
	
	/**
	 * Sets the specified value into The specified Entity Field.
	 * If some re-map action needed, do it here. 
	 * @param field
	 * 		The specified Entity Field
	 * @param entity
	 * 		Entity from which the represented field's value is to be extracted
	 * @param value
	 * 		The specified value
	 * @throws ReflectiveOperationException
	 */
	public abstract void setValue(Field field,  Object entity, Object value) 
			throws ReflectiveOperationException;
	
	/**
	 * Returns the value of the field represented by the specified entity
	 * @param field
	 * 		The specified Entity Field
	 * @param entity
	 * 		Entity from which the represented field's value is to be extracted
	 * @return
	 * 		The represented field's value.
	 * @throws ReflectiveOperationException
	 */
	public abstract Object getValue(Field field, Object entity)
			throws ReflectiveOperationException;
	
	/**
	 * Map the java type to SQL type according to the specified java type
	 * @param javaType
	 * 		The specified java type
	 * @return
	 * 		Integer value indicates java.sql.TYPES
	 */
	public abstract int getSqlType(Class<?> javaType);
}
