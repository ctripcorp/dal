package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class GenTaskByTableViewSp implements Comparable<GenTaskByTableViewSp> {
	
	private int id;
	
	private int project_id;
	
	private String db_name;
	
	private String databaseSet_name;
	
	private String table_names;
	
	private String view_names;
	
	private String sp_names;

	private String prefix;
	
	private String suffix;
	
	private boolean cud_by_sp;
	
	private boolean pagination;
	
	private boolean generated;
	
	private int version;
	
	private String update_user_no;
	private Timestamp update_time;
	private String comment;

	public int getId() {
		return id;
	}

	public String getDatabaseSet_name() {
		return databaseSet_name;
	}

	public void setDatabaseSet_name(String databaseSet_name) {
		this.databaseSet_name = databaseSet_name;
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
		task.setDb_name(rs.getString(3));
		task.setTable_names(rs.getString(4));
		task.setView_names(rs.getString(5));
		task.setSp_names(rs.getString(6));
		task.setPrefix(rs.getString(7));
		task.setSuffix(rs.getString(8));
		task.setCud_by_sp(rs.getBoolean(9));
		task.setPagination(rs.getBoolean(10));
		task.setGenerated(rs.getBoolean(11));
		task.setVersion(rs.getInt(12));
		task.setUpdate_user_no(rs.getString(13));
		task.setUpdate_time(rs.getTimestamp(14));
		task.setComment(rs.getString(15));
		task.setDatabaseSet_name(rs.getString(16));
		return task;
	}

	@Override
	public int compareTo(GenTaskByTableViewSp o) {
		return this.getDb_name().compareTo(o.getDb_name());
	}

}