package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.ctrip.platform.dal.daogen.utils.DatabaseSetUtils;

public class GenTaskBySqlBuilder implements Comparable<GenTaskBySqlBuilder> {

	private int id;

	private int project_id;
	
	private String db_name;
	
	private String databaseSetName;
	
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
	
	private String update_user_no;
	private Timestamp update_time;
	private String comment;

	public String getDatabaseSetName() {
		return databaseSetName;
	}

	public void setDatabaseSetName(String databaseSetName) {
		this.databaseSetName = databaseSetName;
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

	public String getUpdate_user_no() {
		return update_user_no;
	}

	public void setUpdate_user_no(String update_user_no) {
		this.update_user_no = update_user_no;
	}

	public Timestamp getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Timestamp update_time) {
		this.update_time = update_time;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public static GenTaskBySqlBuilder visitRow(ResultSet rs) throws SQLException {
		GenTaskBySqlBuilder task = new GenTaskBySqlBuilder();
		task.setId(rs.getInt(1));
		task.setProject_id(rs.getInt(2));
		
		String databaseSet = rs.getString(3);
		task.setDb_name(DatabaseSetUtils.getDBName(databaseSet));
		task.setDatabaseSetName(databaseSet);
		
		task.setTable_name(rs.getString(4));
		task.setClass_name(rs.getString(5));
		task.setMethod_name(rs.getString(6));
		task.setSql_style(rs.getString(7));
		task.setCrud_type(rs.getString(8));
		task.setFields(rs.getString(9));
		task.setCondition(rs.getString(10));
		task.setSql_content(rs.getString(11));
		task.setGenerated(rs.getBoolean(12));
		task.setVersion(rs.getInt(13));
		task.setUpdate_user_no(rs.getString(14));
		task.setUpdate_time(rs.getTimestamp(15));
		task.setComment(rs.getString(16));

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