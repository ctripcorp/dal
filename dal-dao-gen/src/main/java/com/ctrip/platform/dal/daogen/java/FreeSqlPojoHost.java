package com.ctrip.platform.dal.daogen.java;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.ctrip.platform.dal.daogen.pojo.ColumnMetaData;

public class FreeSqlPojoHost {
	private String packageName;
	private String tableName;
	private String className;
	private List<JavaParameterHost> columns;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
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
		buildImports();
	}

	private Set<String> imports = new TreeSet<String>();
	private void buildImports() {
		
		for(JavaParameterHost field: columns) {
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
