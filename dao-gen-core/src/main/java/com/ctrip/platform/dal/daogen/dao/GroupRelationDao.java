package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.GroupRelation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class GroupRelationDao {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GroupRelation> getAllGroupRelation() {
        try {
            return jdbcTemplate.query(
                    "SELECT id, current_group_id, child_group_id, child_group_role, adduser, update_user_no ,update_time FROM group_relation",
                    new RowMapper<GroupRelation>() {
                        public GroupRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return GroupRelation.visitRow(rs);
                        }
                    });
        } catch (Throwable e) {
            throw e;
        }
    }

    public List<GroupRelation> getAllGroupRelationByCurrentGroupId(Integer currentGroupId) {
        try {
            return jdbcTemplate.query(
                    "SELECT id, current_group_id, child_group_id, child_group_role, adduser, update_user_no ,update_time FROM group_relation WHERE current_group_id=?",
                    new Object[] {currentGroupId}, new RowMapper<GroupRelation>() {
                        public GroupRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return GroupRelation.visitRow(rs);
                        }
                    });
        } catch (Throwable e) {
            throw e;
        }
    }

    public List<GroupRelation> getAllGroupRelationByChildGroupId(Integer childGroupId) {
        try {
            return jdbcTemplate.query(
                    "SELECT id, current_group_id, child_group_id, child_group_role, adduser, update_user_no ,update_time FROM group_relation WHERE child_group_id=?",
                    new Object[] {childGroupId}, new RowMapper<GroupRelation>() {
                        public GroupRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return GroupRelation.visitRow(rs);
                        }
                    });
        } catch (Throwable e) {
            throw e;
        }
    }

    public GroupRelation getGroupRelationByCurrentGroupIdAndChildGroupId(Integer currentGroupId, Integer childGroupId) {
        try {
            List<GroupRelation> result = jdbcTemplate.query(
                    "SELECT id, current_group_id, child_group_id, child_group_role, adduser, update_user_no ,update_time FROM group_relation WHERE current_group_id=? AND child_group_id=?",
                    new Object[] {currentGroupId, childGroupId}, new RowMapper<GroupRelation>() {
                        public GroupRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return GroupRelation.visitRow(rs);
                        }
                    });
            if (result != null && result.size() > 0)
                return result.get(0);
            return null;
        } catch (Throwable e) {
            throw e;
        }
    }

    public int insertChildGroup(GroupRelation relation) {
        try {
            return jdbcTemplate.update(
                    "INSERT INTO group_relation(current_group_id, child_group_id, child_group_role, adduser, update_user_no, update_time) VALUE(?,?,?,?,?,?)",
                    relation.getCurrent_group_id(), relation.getChild_group_id(), relation.getChild_group_role(),
                    relation.getAdduser(), relation.getUpdate_user_no(), relation.getUpdate_time());
        } catch (Throwable e) {
            throw e;
        }
    }

    public int updateGroupRelation(Integer currentGroupId, Integer childGroupId, Integer childGroupRole,
            Integer adduser, String updateUserNo, Timestamp updateTime) {
        try {
            return jdbcTemplate.update(
                    "UPDATE group_relation SET child_group_role=?, adduser=?, update_user_no=?,update_time=? WHERE current_group_id=? AND child_group_id=?",
                    childGroupRole, adduser, updateUserNo, updateTime, currentGroupId, childGroupId);
        } catch (Throwable e) {
            throw e;
        }
    }

    public int deleteChildGroupByCurrentGroupIdAndChildGroupId(Integer currentGroupId, Integer childGroupId) {
        try {
            return jdbcTemplate.update("DELETE FROM group_relation WHERE current_group_id=? AND child_group_id=?",
                    currentGroupId, childGroupId);
        } catch (Throwable e) {
            throw e;
        }
    }
}
