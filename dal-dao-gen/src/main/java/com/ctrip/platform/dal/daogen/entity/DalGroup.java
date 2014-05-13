package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DalGroup implements Comparable<DalGroup>{
	private int id;
	private String group_name;
	private String group_comment;
	private String create_user_no;
	private Timestamp create_time;
	
	private String text;
	
	private String icon;
	
	private boolean children;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public String getGroup_comment() {
		return group_comment;
	}
	public void setGroup_comment(String group_comment) {
		this.group_comment = group_comment;
	}
	public String getCreate_user_no() {
		return create_user_no;
	}
	public void setCreate_user_no(String create_user_no) {
		this.create_user_no = create_user_no;
	}
	public Timestamp getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public boolean isChildren() {
		return children;
	}
	public void setChildren(boolean children) {
		this.children = children;
	}
	
	public static DalGroup visitRow(ResultSet rs) throws SQLException {
		DalGroup group = new DalGroup();
		group.setId(rs.getInt(1));
		group.setGroup_name(rs.getString(2));
		group.setGroup_comment(rs.getString(3));
		group.setCreate_user_no(rs.getString(4));
		group.setCreate_time(rs.getTimestamp(5));
		return group;
	}
	@Override
	public int compareTo(DalGroup o) {
		return this.group_name.compareTo(o.getGroup_name());
	}
}
