

package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class GenTaskByFreeSql implements Comparable<GenTaskByFreeSql> {

	private int id;
	
	private int project_id;
	
	private String db_name;
	
	private String class_name;
	
	private String pojo_name;
	
	private String method_name;
	
	private String crud_type;
	
	private String sql_content;
	
	private String parameters;
	
	private boolean generated;
	
	private int version;
	
	private String update_user_no;
	private Timestamp update_time;
	private String comment;

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
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

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getPojo_name() {
		return pojo_name;
	}

	public void setPojo_name(String pojo_name) {
		this.pojo_name = pojo_name;
	}

	public String getMethod_name() {
		return method_name;
	}

	public void setMethod_name(String method_name) {
		this.method_name = method_name;
	}

	public String getCrud_type() {
		return crud_type;
	}

	public void setCrud_type(String crud_type) {
		this.crud_type = crud_type;
	}

	public String getSql_content() {
		return sql_content;
	}

	public void setSql_content(String sql_content) {
		this.sql_content = sql_content;
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

	public static GenTaskByFreeSql visitRow(ResultSet rs) throws SQLException {
		GenTaskByFreeSql task = new GenTaskByFreeSql();
		task.setId(rs.getInt(1));
		task.setProject_id(rs.getInt(2));
		task.setDb_name(rs.getString(3));
		task.setClass_name(rs.getString(4));
		task.setPojo_name(rs.getString(5));
		task.setMethod_name(rs.getString(6));
		task.setCrud_type(rs.getString(7));
		task.setSql_content(rs.getString(8));
		task.setParameters(rs.getString(9));
		task.setGenerated(rs.getBoolean(10));
		task.setVersion(rs.getInt(11));
		task.setUpdate_user_no(rs.getString(12));
		task.setUpdate_time(rs.getTimestamp(13));
		task.setComment(rs.getString(14));
		return task;
	}

	@Override
	public int compareTo(GenTaskByFreeSql o) {
		int result =  this.getDb_name().compareTo(o.getDb_name());
		if(result != 0){
			return result;
		}
		
		result = this.getClass_name().compareTo(o.getClass_name());
		if(result != 0){
			return result;
		}
		
		return this.getMethod_name().compareTo(o.getMethod_name());
	}
	
}
