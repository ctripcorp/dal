package com.ctrip.platform.daogen.pojo;

public class SpTask  extends AbstractTask{

	private int id;
	
	private int project_id;
	
	private String db_name;
	
	private String class_name;
	
	private String sp_schema;
	
	private String sp_name;
	
	private String sql_style;
	
	private String crud_type;
	
	private String sp_content;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProject_id() {
		return project_id;
	}

	public void setProject_id(int project_id) {
		this.project_id = project_id;
	}

	public String getDb_name() {
		return db_name;
	}

	public void setDb_name(String db_name) {
		this.db_name = db_name;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getSp_schema() {
		return sp_schema;
	}

	public void setSp_schema(String sp_schema) {
		this.sp_schema = sp_schema;
	}

	public String getSp_name() {
		return sp_name;
	}

	public void setSp_name(String sp_name) {
		this.sp_name = sp_name;
	}

	public String getSql_style() {
		return sql_style;
	}

	public void setSql_style(String sql_style) {
		this.sql_style = sql_style;
	}

	public String getCrud_type() {
		return crud_type;
	}

	public void setCrud_type(String crud_type) {
		this.crud_type = crud_type;
	}

	public String getSp_content() {
		return sp_content;
	}

	public void setSp_content(String sp_content) {
		this.sp_content = sp_content;
	}
	
}
