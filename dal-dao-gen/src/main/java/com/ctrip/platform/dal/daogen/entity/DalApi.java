package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DalApi implements Comparable<DalApi>{

	private int id;
	private String language;
	private String db_type;
	private String crud_type;
	private String method_declaration;
	private String method_description;
	private String sp_type;
	
	public static DalApi visitRow(ResultSet rs) throws SQLException {
		DalApi api = new DalApi();
		api.setId(rs.getInt("id"));
		api.setLanguage(rs.getString("language"));
		api.setDb_type(rs.getString("db_type"));
		api.setCrud_type(rs.getString("crud_type"));
		api.setMethod_declaration(rs.getString("method_declaration"));
		api.setMethod_description(rs.getString("method_description"));
		api.setSp_type(rs.getString("sp_type"));
		return api;
	}
	
	@Override
	public int compareTo(DalApi api) {
		String str1 = this.language+this.db_type+this.crud_type+
				this.method_declaration+this.method_description+this.sp_type;
		String str2 = api.getLanguage()+api.getDb_type()+api.getCrud_type()+api.getMethod_declaration()
				+api.getMethod_description()+api.getSp_type();
		return str1.compareTo(str2);
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getDb_type() {
		return db_type;
	}
	public void setDb_type(String db_type) {
		this.db_type = db_type;
	}
	public String getCrud_type() {
		return crud_type;
	}
	public void setCrud_type(String crud_type) {
		this.crud_type = crud_type;
	}
	public String getMethod_declaration() {
		return method_declaration;
	}
	public void setMethod_declaration(String method_declaration) {
		this.method_declaration = method_declaration;
	}
	public String getMethod_description() {
		return method_description;
	}
	public void setMethod_description(String method_description) {
		this.method_description = method_description;
	}
	public String getSp_type() {
		return sp_type;
	}
	public void setSp_type(String sp_type) {
		this.sp_type = sp_type;
	}
	
}
