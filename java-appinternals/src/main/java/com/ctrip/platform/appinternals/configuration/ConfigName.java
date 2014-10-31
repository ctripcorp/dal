package com.ctrip.platform.appinternals.configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ConfigName {
	private String name;
	private String alias;
	private Class<?> clazz;
	private Method getMethod;
	private Method setMethod;
	private boolean persistence = true;
	
	public boolean isRead() {
		return this.getMethod != null && this.getMethod.getModifiers() == Modifier.PUBLIC;
	}

	public boolean isWrite() {
		return this.setMethod != null && this.setMethod.getModifiers() == Modifier.PUBLIC;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Method getGetMethod() {
		return getMethod;
	}

	public void setGetMethod(Method getMethod) {
		this.getMethod = getMethod;
	}

	public Method getSetMethod() {
		return setMethod;
	}

	public void setSetMethod(Method setMethod) {
		this.setMethod = setMethod;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isPersistence() {
		return persistence;
	}

	public void setPersistence(boolean persistence) {
		this.persistence = persistence;
	}
	
}
