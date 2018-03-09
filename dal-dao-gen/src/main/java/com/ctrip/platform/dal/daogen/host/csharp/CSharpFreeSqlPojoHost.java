package com.ctrip.platform.dal.daogen.host.csharp;

import java.util.List;

public class CSharpFreeSqlPojoHost {
	
	private String nameSpace;

	private String tableName;
	
	private String className;
	
	private List<CSharpParameterHost> columns;

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<CSharpParameterHost> getColumns() {
		return columns;
	}

	public void setColumns(List<CSharpParameterHost> columns) {
		this.columns = columns;
	}

}
