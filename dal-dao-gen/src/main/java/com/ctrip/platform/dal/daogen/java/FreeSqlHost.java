package com.ctrip.platform.dal.daogen.java;

import java.util.ArrayList;
import java.util.List;

public class FreeSqlHost {
	private String packageName;
	private String dbName;
	private String className;
	private List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();
	private List<JavaParameterHost> fields;
	
	public List<JavaParameterHost> getFields() {
		return fields;
	}

	public void setFields(List<JavaParameterHost> fields) {
		this.fields = fields;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<JavaMethodHost> getMethods() {
		return methods;
	}

	public void setMethods(List<JavaMethodHost> methods) {
		this.methods = methods;
	}
	

}
