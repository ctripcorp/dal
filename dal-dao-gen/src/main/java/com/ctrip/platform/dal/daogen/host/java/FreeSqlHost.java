package com.ctrip.platform.dal.daogen.host.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class FreeSqlHost {
	private String packageName;
	private String dbName;
	private String className;
	private List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();
	private List<JavaParameterHost> fields;
	private DatabaseCategory databaseCategory;
	
	public List<JavaParameterHost> getFields() {
		return fields;
	}

	public void setFields(List<JavaParameterHost> fields) {
		this.fields = fields;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

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

	public List<JavaMethodHost> getMethods() {
		return methods;
	}

	public void setMethods(List<JavaMethodHost> methods) {
		this.methods = methods;
	}
	
	public DatabaseCategory getDatabaseCategory() {
		return databaseCategory;
	}

	public void setDatabaseCategory(DatabaseCategory databaseCategory) {
		this.databaseCategory = databaseCategory;
	}

	public Set<String> getDaoImports() {
		Set<String> imports = new TreeSet<String>();
		imports.add("com.ctrip.platform.dal.dao.*");
		
		imports.add(java.sql.ResultSet.class.getName());
		imports.add(java.sql.SQLException.class.getName());
		imports.add( java.sql.Types.class.getName());
		imports.add( java.util.List.class.getName());

		List<JavaParameterHost> allTypes = new ArrayList<JavaParameterHost>();
		for(JavaMethodHost method: methods) {
			if(null != method.getParameters())
				allTypes.addAll(method.getParameters());
			if(null != method.getFields())
				allTypes.addAll(method.getFields());
		}
		
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
	
	public Set<String> getTestImports()
	{
		Set<String> imports = new TreeSet<String>();
		imports.add(java.util.List.class.getName());
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
