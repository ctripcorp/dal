package com.ctrip.platform.dal.daogen.host.csharp;

import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class CSharpFreeSqlHost {

	private String nameSpace;
	
	private String dbSetName;
	
	private String className;
	
	private List<CSharpMethodHost> methods;
	
	private DatabaseCategory databaseCategory;

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public String getDbSetName() {
		return dbSetName;
	}

	public void setDbSetName(String dbSetName) {
		this.dbSetName = dbSetName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<CSharpMethodHost> getMethods() {
		return methods;
	}

	public void setMethods(List<CSharpMethodHost> methods) {
		this.methods = methods;
	}

	public DatabaseCategory getDatabaseCategory() {
		return databaseCategory;
	}

	public void setDatabaseCategory(DatabaseCategory databaseCategory) {
		this.databaseCategory = databaseCategory;
	}
	
}

