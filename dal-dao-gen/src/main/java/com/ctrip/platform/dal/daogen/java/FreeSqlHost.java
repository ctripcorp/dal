package com.ctrip.platform.dal.daogen.java;

import java.util.List;

public class FreeSqlHost {

	private String nameSpaceEntity;

	private String nameSpaceDao;
	
	private String dbSetName;
	
	private String className;
	
	private List<JavaMethodHost> methods;

	public String getNameSpaceEntity() {
		return nameSpaceEntity;
	}

	public void setNameSpaceEntity(String nameSpaceEntity) {
		this.nameSpaceEntity = nameSpaceEntity;
	}

	public String getNameSpaceDao() {
		return nameSpaceDao;
	}

	public void setNameSpaceDao(String nameSpaceDao) {
		this.nameSpaceDao = nameSpaceDao;
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

	public List<JavaMethodHost> getMethods() {
		return methods;
	}

	public void setMethods(List<JavaMethodHost> methods) {
		this.methods = methods;
	}
	

}
