
package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenTaskBySqlBuilder implements Comparable<GenTaskBySqlBuilder> {

	private int id;

	private int project_id;
	
	private int server_id;
	
	private String db_name;
	
	private String table_name;

	private String class_name;

	private String method_name;

	private String sql_style;

	private String crud_type;

	private String fields;

	private String condition;

	private String sql_content;
	
private boolean generated;
	
	private int version;

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

	public int getServer_id() {
		return server_id;
	}

	public void setServer_id(int server_id) {
		this.server_id = server_id;
	}

	public String getDb_name() {
		return db_name;
	}

	public void setDb_name(String db_name) {
		this.db_name = db_name;
	}

	public String getTable_name() {
		return table_name;
	}

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

	public String getCrud_type() {
		return crud_type;
	}

	public void setCrud_type(String crud_type) {
		this.crud_type = crud_type;
	}

	public boolean isGenerated() {
		return generated;
	}

	public void setGenerated(boolean generated) {
		this.generated = generated;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public static GenTaskBySqlBuilder visitRow(ResultSet rs) throws SQLException {
		GenTaskBySqlBuilder task = new GenTaskBySqlBuilder();
		task.setId(rs.getInt(1));
		task.setProject_id(rs.getInt(2));
		task.setServer_id(rs.getInt(3));
		task.setDb_name(rs.getString(4));
		task.setTable_name(rs.getString(5));
		task.setClass_name(rs.getString(6));
		task.setMethod_name(rs.getString(7));
		task.setSql_style(rs.getString(8));
		task.setCrud_type(rs.getString(9));
		task.setFields(rs.getString(10));
		task.setCondition(rs.getString(11));
		task.setSql_content(rs.getString(12));
		task.setGenerated(rs.getBoolean(13));
		task.setVersion(rs.getInt(14));
		return task;
	}

	@Override
	public int compareTo(GenTaskBySqlBuilder o) {
		int result =  this.getDb_name().compareTo(o.getDb_name());
		if(result != 0){
			return result;
		}
		
		result = this.getTable_name().compareTo(o.getTable_name());
		if(result != 0){
			return result;
		}
		
		return this.getMethod_name().compareTo(o.getMethod_name());
		
	}

}
