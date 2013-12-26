package com.ctrip.platform.daogen.gen;

import java.util.List;

public class Method {
	
	private String action;
	
	private List<Parameter> parameters;
	
	private String methodName;
	
	private String sqlSPName;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public String getSqlSPName() {
		return sqlSPName;
	}

	public void setSqlSPName(String sqlSPName) {
		this.sqlSPName = sqlSPName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	

}
