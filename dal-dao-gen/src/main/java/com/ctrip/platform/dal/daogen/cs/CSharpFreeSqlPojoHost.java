package com.ctrip.platform.dal.daogen.cs;

import java.util.List;

public class CSharpFreeSqlPojoHost {
	
	private String nameSpaceDao;
	
	private String nameSpaceEntity;
	
	public String getNameSpaceEntity() {
		return nameSpaceEntity;
	}

	public void setNameSpaceEntity(String nameSpaceEntity) {
		this.nameSpaceEntity = nameSpaceEntity;
	}

	private String tableName;
	
	private String className;
	
	private List<CSharpParameterHost> columns;

	public String getNameSpaceDao() {
		return nameSpaceDao;
	}

	public void setNameSpaceDao(String nameSpaceDao) {
		this.nameSpaceDao = nameSpaceDao;
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
