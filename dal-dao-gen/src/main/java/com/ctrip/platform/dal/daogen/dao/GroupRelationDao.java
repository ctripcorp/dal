package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.entity.GroupRelation;

public class GroupRelationDao {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<GroupRelation> getAllGroupRelation() {
		return this.jdbcTemplate
				.query("SELECT id, current_group_id, child_group_id, child_group_role, adduser, update_user_no "
						+ ",update_time FROM group_relation",
						new RowMapper<GroupRelation>() {
							public GroupRelation mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return GroupRelation.visitRow(rs);
							}
						});
	}
	
	public List<GroupRelation> getAllGroupRelationByCurrentGroupId(Integer currentGroupId) {
		return this.jdbcTemplate
				.query("SELECT id, current_group_id, child_group_id, child_group_role, adduser, update_user_no "
						+ ",update_time FROM group_relation where current_group_id=?",
						new Object[]{currentGroupId},
						new RowMapper<GroupRelation>() {
							public GroupRelation mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return GroupRelation.visitRow(rs);
							}
						});
	}
	
	public GroupRelation getGroupRelationByCurrentGroupIdAndChildGroupId(Integer currentGroupId, Integer childGroupId) {
		List<GroupRelation> result = this.jdbcTemplate
				.query("SELECT id, current_group_id, child_group_id, child_group_role, adduser, update_user_no "
						+ ",update_time FROM group_relation where current_group_id=? and child_group_id=?",
						new Object[]{currentGroupId, childGroupId},
						new RowMapper<GroupRelation>() {
							public GroupRelation mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return GroupRelation.visitRow(rs);
							}
						});
		if (result!=null && result.size()>0) {
			return result.get(0);
		}
		return null;
	}
	
	public int insertChildGroup(GroupRelation relation) {
		return this.jdbcTemplate.update(
				"insert into group_relation("
				+ "current_group_id, "
				+ "child_group_id, "
				+ "child_group_role, "
				+ "adduser, "
				+ "update_user_no, "
				+ "update_time) "
				+ "value(?,?,?,?,?,?)",
				relation.getCurrent_group_id(),
				relation.getChild_group_id(),
				relation.getChild_group_role(),
				relation.getAdduser(),
				relation.getUpdate_user_no(),
				relation.getUpdate_time());
	}
	
	public int updateGroupRelation(Integer currentGroupId, Integer childGroupId, Integer childGroupRole, Integer adduser,
			String updateUserNo, Timestamp updateTime) {
		try {
			return this.jdbcTemplate.update("update group_relation set child_group_role=?, adduser=?, update_user_no=?"
					+ ",update_time=? where current_group_id=? and child_group_id=?", 
					childGroupRole,
					adduser,
					updateUserNo,
					updateTime,
					currentGroupId,
					childGroupId);
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	public int deleteChildGroupByCurrentGroupIdAndChildGroupId(
			Integer currentGroupId, Integer childGroupId) {
		try {
			return this.jdbcTemplate.update("delete from group_relation "
					+ "where current_group_id=? and child_group_id=?",
							currentGroupId, 
							childGroupId);
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
}
