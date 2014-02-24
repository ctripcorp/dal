package com.ctrip.platform.dal.daogen.java;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.ctrip.platform.dal.daogen.pojo.FieldMeta;


public class JavaParserGenHost {
	
	private String dbName;

	private String className;
	
	private String tableName;
	
	private boolean hasIdentity;
	
	private String identityColumnName;
	
	private List<FieldMeta> fields;
	
	private Set<String> imports = new TreeSet<String>();

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

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
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
	
	public List<FieldMeta> getFields() {
		return fields;
	}

	public void setFields(List<FieldMeta> fields) {
		this.fields = fields;
		buildImports();
	}
	
	private void buildImports() {
		imports.add(java.sql.ResultSet.class.getName());
		imports.add(java.sql.SQLException.class.getName());
		imports.add(java.util.Map.class.getName());
		imports.add(java.util.LinkedHashMap.class.getName());
		
		for(FieldMeta field: fields) {
			Class clazz = field.getJavaClass();
			if(byte[].class.equals(clazz))
				continue;
			if(clazz.getPackage().getName().equals(String.class.getPackage().getName()))
				continue;
			imports.add(clazz.getName());
		}
	}
	
	public Set<String> getImports() {
		return imports;
	}

}
