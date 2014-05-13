package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DalGroup implements Comparable<DalGroup>{
	private int Id;
	private String groupName;
	private String groupComment;
	private String createUserNo;
	private Timestamp cteateTime;
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupComment() {
		return groupComment;
	}
	public void setGroupComment(String groupComment) {
		this.groupComment = groupComment;
	}
	public String getCreateUserNo() {
		return createUserNo;
	}
	public void setCreateUserNo(String createUserNo) {
		this.createUserNo = createUserNo;
	}
	public Timestamp getCteateTime() {
		return cteateTime;
	}
	public void setCteateTime(Timestamp cteateTime) {
		this.cteateTime = cteateTime;
	}
	
	public static DalGroup visitRow(ResultSet rs) throws SQLException {
		DalGroup group = new DalGroup();
		group.setId(rs.getInt(1));
		group.setGroupName(rs.getString(2));
		group.setGroupComment(rs.getString(3));
		group.setCreateUserNo(rs.getString(4));
		group.setCteateTime(rs.getTimestamp(5));
		return group;
	}
	@Override
	public int compareTo(DalGroup o) {
		return this.groupName.compareTo(o.getGroupName());
	}
}
