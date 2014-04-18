package com.ctrip.platform.dal.ext.parser;

import java.lang.reflect.Field;

public abstract class Loader {
	public abstract Object load(Field field, Object value, Object defaultVal) 
			throws ReflectiveOperationException;
}
