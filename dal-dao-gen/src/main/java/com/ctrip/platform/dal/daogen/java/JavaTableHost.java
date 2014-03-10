package com.ctrip.platform.dal.daogen.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;

public class JavaTableHost {
	private DatabaseCategory databaseCategory;
	private String packageName;
	private String dbName;
	private String tableName;
	private String pojoClassName;
	private List<JavaParameterHost> fields;
	private boolean hasIdentity;
	private String identityColumnName;
	private boolean spa;
	private SpaOperationHost spaInsert;
	private SpaOperationHost spaDelete;
	private SpaOperationHost spaUpdate;
	private List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isSpa() {
		return spa;
	}

	public void setSpa(boolean isSpa) {
		this.spa = isSpa;
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

	public Set<String> getDaoImports() {
		Set<String> imports = new TreeSet<String>();
		imports.add(java.sql.ResultSet.class.getName());
		imports.add(java.sql.SQLException.class.getName());
		imports.add(java.util.Map.class.getName());
		imports.add(java.util.LinkedHashMap.class.getName());
		imports.add( java.sql.Types.class.getName());
		imports.add( java.util.ArrayList.class.getName());
		imports.add( java.util.List.class.getName());

		List<JavaParameterHost> allTypes = new ArrayList<JavaParameterHost>(fields);
		for(JavaMethodHost method: methods) {
			allTypes.addAll(method.getParameters());
		}
		
		if(spaInsert != null)
			allTypes.addAll(spaInsert.getParameters());
		if(spaDelete != null)
			allTypes.addAll(spaDelete.getParameters());
		if(spaUpdate != null)
			allTypes.addAll(spaUpdate.getParameters());
		
		for(JavaParameterHost field: allTypes) {
			Class<?> clazz = field.getJavaClass();
			if(byte[].class.equals(clazz))
				continue;
			if(clazz.getPackage().getName().equals(String.class.getPackage().getName()))
				continue;
			imports.add(clazz.getName());
		}
		return imports;
	}
	
	public Set<String> getPojoImports() {
		Set<String> imports = new TreeSet<String>();

		List<JavaParameterHost> allTypes = new ArrayList<JavaParameterHost>(fields);
		for(JavaParameterHost field: allTypes) {
			Class<?> clazz = field.getJavaClass();
			if(byte[].class.equals(clazz))
				continue;
			if(clazz.getPackage().getName().equals(String.class.getPackage().getName()))
				continue;
			imports.add(clazz.getName());
		}
		return imports;
	}

}
