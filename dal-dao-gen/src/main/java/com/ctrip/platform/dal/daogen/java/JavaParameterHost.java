package com.ctrip.platform.dal.daogen.java;

import com.ctrip.platform.dal.daogen.AbstractParameterHost;

public class JavaParameterHost extends AbstractParameterHost {
	
	private int index;
	
	private int sqlType;
	
	private Class<?> javaClass;
	
	private String name;
	
	private boolean identity;
	
	private boolean primary;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getSqlType() {
		return sqlType;
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}

	public Class<?> getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(Class<?> javaClass) {
		this.javaClass = javaClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isIdentity() {
		return identity;
	}

	public void setIdentity(boolean identity) {
		this.identity = identity;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	
	

}
