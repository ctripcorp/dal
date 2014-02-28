package com.ctrip.platform.dal.daogen.cs;

import java.util.List;

public class CSharpFreeSqlHost {

	private String nameSpace;
	
	private String dbSetName;
	
	private String className;
	
	private List<CSharpMethodHost> methods;

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
	
}

