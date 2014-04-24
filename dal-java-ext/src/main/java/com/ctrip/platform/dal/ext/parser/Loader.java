package com.ctrip.platform.dal.ext.parser;

import java.lang.reflect.Field;

/**
 * Load/Save the value from/to specified Entity field
 * @author wcyuan
 * @version 2014-04-24
 */
public abstract class Loader {
	
	/**
	 * Returns the value of the field represented by this Field, on the specified entity. 
	 * The value is automatically wrapped in an object if it has a primitive type.
	 * The returned value will be filled into business objects,
	 * If some re-map action needed, do it here. 
	 * @param field
	 * 		The specified Entity Field
	 * @param entity
	 * 		Entity from which the represented field's value is to be extracted
	 * @return
	 * 		the value of the field represented by this Field
	 * @throws ReflectiveOperationException
	 */
	public abstract Object load(Field field, Object entity) 
			throws ReflectiveOperationException;
	
	/**
	 * Returns the value of the field represented by this Field, on the specified entity. 
	 * The value is automatically wrapped in an object if it has a primitive type.
	 * The returned value will be set into columns, inserted or updated.
	 * If some re-map action needed, do it here
	 * @param field
	 * 		The specified Entity Field
	 * @param entity
	 * 		Entity from which the represented field's value is to be extracted
	 * @return
	 * 		the value of the field represented by this Field
	 * @throws ReflectiveOperationException
	 */
	public abstract Object save(Field field, Object entity)
			throws ReflectiveOperationException;
	
	/**
	 * Map the java type to SQL type according to the specified java type
	 * @param javaType
	 * 		The specified java type
	 * @return
	 * 		Integer value indicats java.sql.TYPES
	 */
	public abstract int getSqlType(Class<?> javaType);
	
	
	/**
	 * Database type
	 */
	public static enum DBType {
		mysql,
		sqlserver
	}
}
