package com.ctrip.platform.dal.daogen.cs;

import java.util.List;

public class CSharpMethodHost {
	
	private String crud_type;
	
	private String name;
	
	private String sql;
	
	private List<CSharpParameterHost> parameters;

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

	public List<CSharpParameterHost> getParameters() {
		return parameters;
	}

	public void setParameters(List<CSharpParameterHost> parameters) {
		this.parameters = parameters;
	}

}
