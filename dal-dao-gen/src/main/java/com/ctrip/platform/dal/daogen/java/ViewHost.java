package com.ctrip.platform.dal.daogen.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ViewHost {
	private String packageName;
	private String dbName;
	private String pojoClassName;
	private String ViewName;
	private List<JavaParameterHost> fields = new ArrayList<JavaParameterHost>();
	private Set<String> imports = new TreeSet<String>();
	
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
	public String getPojoClassName() {
		return pojoClassName;
	}
	public void setPojoClassName(String pojoClassName) {
		this.pojoClassName = pojoClassName;
	}
	public String getViewName() {
		return ViewName;
	}
	public void setViewName(String viewName) {
		ViewName = viewName;
	}
	public List<JavaParameterHost> getFields() {
		return fields;
	}
	public void setFields(List<JavaParameterHost> fields) {
		this.fields = fields;
	}
	public Set<String> getImports() {
		return imports;
	}
	public void setImports(Set<String> imports) {
		this.imports = imports;
	}
}
