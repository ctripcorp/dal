package com.ctrip.platform.dal.daogen.host.csharp;

import java.util.List;

public class CSharpMethodHost {
	
	private String crud_type;
	
	private String name;
	
	private String sql;
	
	private List<CSharpParameterHost> parameters;
	private CSharpFreeSqlPojoHost pojohost;
	
	private String pojoName;
	
	private String scalarType;
	private String pojoType;

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

	public String getPojoName() {
		return pojoName;
	}

	public void setPojoName(String pojoName) {
		this.pojoName = pojoName;
	}

	public String getScalarType() {
		return scalarType;
	}

	public void setScalarType(String scalarType) {
		this.scalarType = scalarType;
	}

	public String getPojoType() {
		return pojoType;
	}

	public void setPojoType(String pojoType) {
		this.pojoType = pojoType;
	}

	public CSharpFreeSqlPojoHost getPojohost() {
		return pojohost;
	}

	public void setPojohost(CSharpFreeSqlPojoHost pojohost) {
		this.pojohost = pojohost;
	}

	public boolean isScalar(){
		return this.pojoType.equalsIgnoreCase("SimpleType") && 
				(this.scalarType.equalsIgnoreCase("First") ||
						this.scalarType.equalsIgnoreCase("Single"));
	}
	
	public CSharpParameterHost  getSinglePojoFieldHost(){
		return this.pojohost.getColumns().get(0);
	}
	
}
