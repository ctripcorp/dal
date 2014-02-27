package com.ctrip.platform.dal.daogen.java;

import java.util.List;

public class FreeSqlPojoHost {
	
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
	
	private List<JavaParameterHost> columns;

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

	public List<JavaParameterHost> getColumns() {
		return columns;
	}

	public void setColumns(List<JavaParameterHost> columns) {
		this.columns = columns;
	}


}
