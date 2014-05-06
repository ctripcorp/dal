package com.ctrip.platform.dal.ext.parser;

import java.lang.reflect.Field;

public abstract class Loader {
	public abstract Object load(Field field, Object value) 
			throws ReflectiveOperationException;
	public abstract Object save(Field field, Object entity)
			throws ReflectiveOperationException;
}
