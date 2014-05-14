package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DalGroupDB implements Comparable<DalGroupDB>{
	private int id;
	private String dbname;
	private String comment;
	private int dal_group_id;
	
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
	public static DalGroupDB visitRow(ResultSet rs) throws SQLException {
		DalGroupDB group = new DalGroupDB();
		group.setId(rs.getInt(1));
		group.setDbname(rs.getString(2));
		group.setComment(rs.getString(3));
		group.setDal_group_id(rs.getInt(4));
		return group;
	}
	@Override
	public int compareTo(DalGroupDB o) {
		return this.dbname.compareTo(o.getDbname());
	}
}
