package com.ctrip.platform.dal.daogen.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.ctrip.platform.dal.daogen.pojo.DatabaseCategory;

public class SpHost {
	private DatabaseCategory databaseCategory;
	private String packageName;
	private String dbName;
	private String className;
	private String spName;
	private List<JavaParameterHost> parameters = new ArrayList<JavaParameterHost>();
	private Set<String> imports = new TreeSet<String>();
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public DatabaseCategory getDatabaseCategory() {
		return databaseCategory;
	}
	public void setDatabaseCategory(DatabaseCategory databaseCategory) {
		this.databaseCategory = databaseCategory;
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
	public String getSpName() {
		return spName;
	}
	public void setSpName(String spName) {
		this.spName = spName;
	}
	public List<JavaParameterHost> getParameters() {
		return parameters;
	}
	public void setParameters(List<JavaParameterHost> parameters) {
		this.parameters = parameters;
	}
	
	public Set<String> getDaoImports() {
		Set<String> imports = new TreeSet<String>();
		
		imports.add(java.sql.SQLException.class.getName());
		imports.add(java.util.Map.class.getName());
		imports.add(java.sql.Types.class.getName());
		imports.addAll(getPojoImports());

		return imports;
	}
	
	public Set<String> getPojoImports() {
		Set<String> imports = new TreeSet<String>();

		List<JavaParameterHost> allTypes = new ArrayList<JavaParameterHost>(parameters);
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
