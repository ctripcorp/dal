package com.ctrip.platform.dal.daogen.pojo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AutoTask extends AbstractTask {

	private int id;

	private int project_id;

	private String class_name;

	private String method_name;

	private String sql_style;

	private String sql_type;

	private String crud_type;

	private String fields;

	private String condition;

	private String sql_content;
	
	private int server_id;

	public int getServer_id() {
		return server_id;
	}

	public void setServer_id(int server_id) {
		this.server_id = server_id;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getSql_content() {
		return sql_content;
	}

	public void setSql_content(String sql_content) {
		this.sql_content = sql_content;
	}

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

	@Override
	public String getDb_name() {
		return db_name;
	}

	@Override
	public void setDb_name(String db_name) {
		this.db_name = db_name;
	}

	@Override
	public String getTable_name() {
		return table_name;
	}

	@Override
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getMethod_name() {
		return method_name;
	}

	public void setMethod_name(String method_name) {
		this.method_name = method_name;
	}

	public String getSql_style() {
		return sql_style;
	}

	public void setSql_style(String sql_style) {
		this.sql_style = sql_style;
	}

	public String getSql_type() {
		return sql_type;
	}

	public void setSql_type(String sql_type) {
		this.sql_type = sql_type;
	}

	public String getCrud_type() {
		return crud_type;
	}

	public void setCrud_type(String crud_type) {
		this.crud_type = crud_type;
	}

	public static AutoTask visitRow(ResultSet rs) throws SQLException {
		AutoTask task = new AutoTask();
		task.setId(rs.getInt(1));
		task.setProject_id(rs.getInt(2));
		task.setDb_name(rs.getString(3));
		task.setTable_name(rs.getString(4));
		task.setClass_name(rs.getString(5));
		task.setMethod_name(rs.getString(6));
		task.setSql_style(rs.getString(7));
		task.setSql_type(rs.getString(8));
		task.setCrud_type(rs.getString(9));
		task.setFields(rs.getString(10));
		task.setCondition(rs.getString(11));
		task.setSql_content(rs.getString(12));

		return task;
	}

}
