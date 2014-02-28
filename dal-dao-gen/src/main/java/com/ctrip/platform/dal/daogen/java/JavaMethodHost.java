package com.ctrip.platform.dal.daogen.java;

import java.util.List;

import org.apache.commons.lang.WordUtils;

public class JavaMethodHost {
	private String crud_type;
	private String name;
	private String sql;
	private String className;
	private List<JavaParameterHost> parameters;

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
		return WordUtils.uncapitalize(className);
	}
	
	public String getParameterDeclaration() {
		StringBuilder sb = new StringBuilder();
		for(JavaParameterHost parameter: parameters) {
			sb.append(parameter.getClassDisplayName()).append(' ').append(parameter.getName()).append(", ");
		}
		
		sb.delete(sb.length() - 2, sb.length() - 1);
		
		return sb.toString();
	}
}
