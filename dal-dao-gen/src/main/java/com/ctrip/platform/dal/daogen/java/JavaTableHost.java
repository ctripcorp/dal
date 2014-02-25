package com.ctrip.platform.dal.daogen.java;

import java.util.List;

import com.ctrip.platform.dal.daogen.pojo.ColumnMetaData;

public class JavaTableHost {
	
	private String namespace;
	
	private String dbName;
	
	private String tableName;
	
	private String pojoClassName;
	
	private List<JavaMethodHost> methods;
	
	private List<JavaParameterHost> fields;
	
	private boolean hasIdentity;
	
	private String identityColumnName;
	
	private boolean isSpa;

	public boolean isSpa() {
		return isSpa;
	}

	public void setSpa(boolean isSpa) {
		this.isSpa = isSpa;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPojoClassName() {
		return pojoClassName;
	}

	public void setPojoClassName(String pojoClassName) {
		this.pojoClassName = pojoClassName;
	}

	public List<JavaMethodHost> getMethods() {
		return methods;
	}

	public void setMethods(List<JavaMethodHost> methods) {
		this.methods = methods;
	}

	public List<JavaParameterHost> getFields() {
		return fields;
	}

	public void setFields(List<JavaParameterHost> fields) {
		this.fields = fields;
	}

	public boolean isHasIdentity() {
		return hasIdentity;
	}

	public void setHasIdentity(boolean hasIdentity) {
		this.hasIdentity = hasIdentity;
	}

	public String getIdentityColumnName() {
		return identityColumnName;
	}

	public void setIdentityColumnName(String identityColumnName) {
		this.identityColumnName = identityColumnName;
	}

}
