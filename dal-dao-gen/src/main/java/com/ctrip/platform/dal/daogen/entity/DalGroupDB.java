package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DalGroupDB implements Comparable<DalGroupDB>{
	private int id;
	private String dbname;
	private String comment;
	private int dal_group_id;
	private String db_address;
	private String db_port;             
	private String db_user;             
	private String db_password;        
	private String db_catalog;           
	private String db_providerName;     
	
	public static DalGroupDB visitRow(ResultSet rs) throws SQLException {
		DalGroupDB group = new DalGroupDB();
		group.setId(rs.getInt(1));
		group.setDbname(rs.getString(2));
		group.setComment(rs.getString(3));
		group.setDal_group_id(rs.getInt(4));
		group.setDb_address(rs.getString(5));
		group.setDb_port(rs.getString(6));
		group.setDb_user(rs.getString(7));
		group.setDb_password(rs.getString(8));
		group.setDb_catalog(rs.getString(9));
		group.setDb_providerName(rs.getString(10));
		return group;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getDal_group_id() {
		return dal_group_id;
	}
	public void setDal_group_id(int dal_group_id) {
		this.dal_group_id = dal_group_id;
	}
	public String getDb_address() {
		return db_address;
	}
	public void setDb_address(String db_address) {
		this.db_address = db_address;
	}
	public String getDb_port() {
		return db_port;
	}
	public void setDb_port(String db_port) {
		this.db_port = db_port;
	}
	public String getDb_user() {
		return db_user;
	}
	public void setDb_user(String db_user) {
		this.db_user = db_user;
	}
	public String getDb_password() {
		return db_password;
	}
	public void setDb_password(String db_password) {
		this.db_password = db_password;
	}
	public String getDb_catalog() {
		return db_catalog;
	}
	public void setDb_catalog(String db_catalog) {
		this.db_catalog = db_catalog;
	}
	public String getDb_providerName() {
		return db_providerName;
	}
	public void setDb_providerName(String db_providerName) {
		this.db_providerName = db_providerName;
	}
	
	@Override
	public int compareTo(DalGroupDB o) {
		return this.dbname.compareTo(o.getDbname());
	}

	@Override
	public String toString() {
		return "DalGroupDB [id=" + id + ", dbname=" + dbname + ", comment="
				+ comment + ", dal_group_id=" + dal_group_id + ", db_address="
				+ db_address + ", db_port=" + db_port + ", db_user=" + db_user
				+ ", db_password=" + db_password + ", db_catalog=" + db_catalog
				+ ", db_providerName=" + db_providerName + "]";
	}
	
	
}
