package com.ctrip.platform.dal.daogen.java;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.ctrip.platform.dal.daogen.pojo.DatabaseCategory;

public class JavaTableHost {
	private DatabaseCategory databaseCategory;
	private String packageName;
	private String dbName;
	private String tableName;
	private String pojoClassName;
	private List<JavaParameterHost> fields;
	private boolean hasIdentity;
	private String identityColumnName;
	private boolean isSpa;
	private SpaOperationHost spaInsert;
	private SpaOperationHost spaDelete;
	private SpaOperationHost spaUpdate;
	private List<JavaMethodHost> methods;
	private Set<String> imports = new TreeSet<String>();

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isSpa() {
		return isSpa;
	}

	public void setSpa(boolean isSpa) {
		this.isSpa = isSpa;
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
		buildImports();
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
	
	public SpaOperationHost getSpaInsert() {
		return spaInsert;
	}

	public void setSpaInsert(SpaOperationHost spaInsert) {
		this.spaInsert = spaInsert;
	}

	public SpaOperationHost getSpaDelete() {
		return spaDelete;
	}

	public void setSpaDelete(SpaOperationHost spaDelete) {
		this.spaDelete = spaDelete;
	}

	public SpaOperationHost getSpaUpdate() {
		return spaUpdate;
	}

	public void setSpaUpdate(SpaOperationHost spaUpdate) {
		this.spaUpdate = spaUpdate;
	}
	
	public DatabaseCategory getDatabaseCategory() {
		return databaseCategory;
	}

	public void setDatabaseCategory(DatabaseCategory databaseCategory) {
		this.databaseCategory = databaseCategory;
	}

	private void buildImports() {
		imports.add(java.sql.ResultSet.class.getName());
		imports.add(java.sql.SQLException.class.getName());
		imports.add(java.util.Map.class.getName());
		imports.add(java.util.LinkedHashMap.class.getName());
		
		for(JavaParameterHost field: fields) {
			Class<?> clazz = field.getJavaClass();
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
