package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenTaskByTableViewSp implements Comparable<GenTaskByTableViewSp> {
	
	private int id;
	
	private int server_id;
	
	private int project_id;
	
	private String db_name;
	
	private String table_names;
	
	private String view_names;
	
	private String sp_names;

	private String prefix;
	
	private String suffix;
	
	private boolean cud_by_sp;
	
	private boolean pagination;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getServer_id() {
		return server_id;
	}

	public void setServer_id(int server_id) {
		this.server_id = server_id;
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

	public String getTable_names() {
		return table_names;
	}

	public void setTable_names(String table_names) {
		this.table_names = table_names;
	}

	public String getView_names() {
		return view_names;
	}

	public void setView_names(String view_names) {
		this.view_names = view_names;
	}

	public String getSp_names() {
		return sp_names;
	}

	public void setSp_names(String sp_names) {
		this.sp_names = sp_names;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public boolean isCud_by_sp() {
		return cud_by_sp;
	}

	public void setCud_by_sp(boolean cud_by_sp) {
		this.cud_by_sp = cud_by_sp;
	}

	public boolean isPagination() {
		return pagination;
	}

	public void setPagination(boolean pagination) {
		this.pagination = pagination;
	}

	/**
	 * 根据Resultset返回GenTaskByTableView实体对象
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static GenTaskByTableViewSp visitRow(ResultSet rs) throws SQLException {
		GenTaskByTableViewSp task = new GenTaskByTableViewSp();
		task.setId(rs.getInt(1));
		task.setProject_id(rs.getInt(2));
		task.setServer_id(rs.getInt(3));
		task.setDb_name(rs.getString(4));
		task.setTable_names(rs.getString(5));
		task.setView_names(rs.getString(6));
		task.setSp_names(rs.getString(7));
		task.setPrefix(rs.getString(8));
		task.setSuffix(rs.getString(9));
		task.setCud_by_sp(rs.getBoolean(10));
		task.setPagination(rs.getBoolean(11));
		return task;
	}

	@Override
	public int compareTo(GenTaskByTableViewSp o) {
		return this.getDb_name().compareTo(o.getDb_name());
	}

}
