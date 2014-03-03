package com.ctrip.platform.dal.daogen.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

public class JavaMethodHost {
	private String crud_type;
	private String name;
	private String sql;
	private String packageName;
	// DAO class name
	private String className;
	private String pojoClassName;
	private List<JavaParameterHost> parameters;
	// Only for free sql query dao
	private List<JavaParameterHost> fields;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPojoClassName() {
		return pojoClassName;
	}

	public void setPojoClassName(String pojoClassName) {
		this.pojoClassName = pojoClassName;
	}

	public List<JavaParameterHost> getFields() {
		return fields;
	}

	public void setFields(List<JavaParameterHost> fields) {
		this.fields = fields;
	}

	public String getCrud_type() {
		return crud_type;
	}

	public void setCrud_type(String crud_type) {
		this.crud_type = crud_type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<JavaParameterHost> getParameters() {
		return parameters;
	}

	public void setParameters(List<JavaParameterHost> parameters) {
		this.parameters = parameters;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getVariableName() {
		return WordUtils.uncapitalize(pojoClassName);
	}
	
	public String getParameterNames() {
		String[] params = new String[parameters.size()];
		int i = 0;
		for(JavaParameterHost parameter: parameters) {
			params[i++] = parameter.getName();
		}
		
		return StringUtils.join(params, ", ");
	}
	
	public String getParameterDeclaration() {
		StringBuilder sb = new StringBuilder();
		for(JavaParameterHost parameter: parameters) {
			sb.append(parameter.getClassDisplayName()).append(' ').append("param" + parameter.getName()).append(", ");
		}
		
		sb.delete(sb.length() - 2, sb.length() - 1);
		
		return sb.toString();
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
