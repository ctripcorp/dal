package com.ctrip.platform.dal.daogen.pojo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenTaskBySP  extends GenTaskAbstract{

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
	
	public static GenTaskBySP visitRow(ResultSet rs) throws SQLException {
		GenTaskBySP task = new GenTaskBySP();
		task.setId(rs.getInt(1));
		task.setProject_id(rs.getInt(2));
		task.setServer_id(rs.getInt(3));
		task.setDb_name(rs.getString(4));
		task.setClass_name(rs.getString(5));
		task.setSp_schema(rs.getString(6));
		task.setSp_name(rs.getString(7));
		task.setSql_style(rs.getString(8));
		task.setCrud_type(rs.getString(9));
		task.setSp_content(rs.getString(10));
		return task;
	}
}
