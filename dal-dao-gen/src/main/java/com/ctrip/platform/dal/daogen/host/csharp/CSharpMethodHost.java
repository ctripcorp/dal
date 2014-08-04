package com.ctrip.platform.dal.daogen.host.csharp;

import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.utils.SqlBuilder;

public class CSharpMethodHost {
	
	private String crud_type;
	
	private String name;
	
	private String sql;
	
	private List<CSharpParameterHost> parameters;
	private CSharpFreeSqlPojoHost pojohost;
	
	private String pojoName;
	
	private String scalarType;
	private String pojoType;
	
	private boolean paging;

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

	public boolean isPaging() {
		return paging;
	}

	public void setPaging(boolean paging) {
		this.paging = paging;
	}

	public void setPojohost(CSharpFreeSqlPojoHost pojohost) {
		this.pojohost = pojohost;
	}

	public boolean isFirstOrSingle(){
		return (this.scalarType!= null) && 
				(this.scalarType.equalsIgnoreCase("First") ||
						this.scalarType.equalsIgnoreCase("Single"));
	}
	
	public boolean isScalar(){
		return this.pojoType != null && this.pojoType.equalsIgnoreCase("SimpleType");
	}
	
	public CSharpParameterHost  getSinglePojoFieldHost(){
		return this.pojohost.getColumns().get(0);
	}
	
	public String getPagingSql(DatabaseCategory dbType) 
			throws Exception{
        return SqlBuilder.pagingQuerySql(sql, dbType, CurrentLanguage.CSharp);
	}
}
