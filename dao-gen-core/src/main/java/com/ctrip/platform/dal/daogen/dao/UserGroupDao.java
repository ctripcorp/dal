package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.UserGroup;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserGroupDao {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<UserGroup> getAllUserGroup() {
        return jdbcTemplate.query("SELECT id, user_id, group_id, role, adduser FROM user_group",
                new RowMapper<UserGroup>() {
                    public UserGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return UserGroup.visitRow(rs);
                    }
                });
    }

    public List<UserGroup> getUserGroupById(Integer id) {
        return jdbcTemplate.query("SELECT id, user_id, group_id, role, adduser FROM user_group WHERE id = ?",
                new Object[] {id}, new RowMapper<UserGroup>() {
                    public UserGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return UserGroup.visitRow(rs);
                    }
                });
    }

    public List<UserGroup> getUserGroupByUserId(Integer userId) {
        return jdbcTemplate.query("SELECT id, user_id, group_id, role, adduser FROM user_group WHERE user_id = ?",
                new Object[] {userId}, new RowMapper<UserGroup>() {
                    public UserGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return UserGroup.visitRow(rs);
                    }
                });
    }

    public List<UserGroup> getUserGroupByGroupId(Integer groupId) {
        return jdbcTemplate.query("SELECT id, user_id, group_id, role, adduser FROM user_group WHERE group_id = ?",
                new Object[] {groupId}, new RowMapper<UserGroup>() {
                    public UserGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return UserGroup.visitRow(rs);
                    }
                });
    }

    public List<UserGroup> getUserGroupByGroupIdAndUserId(Integer groupId, Integer userId) {
        return jdbcTemplate.query(
                "SELECT id, user_id, group_id, role, adduser FROM user_group WHERE group_id = ? AND user_id=?",
                new Object[] {groupId, userId}, new RowMapper<UserGroup>() {
                    public UserGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return UserGroup.visitRow(rs);
                    }
                });
    }

    public int insertUserGroup(UserGroup ug) {
        return jdbcTemplate.update("INSERT INTO user_group(id, user_id, group_id, role, adduser) VALUE(?,?,?,?,?)",
                ug.getId(), ug.getUser_id(), ug.getGroup_id(), ug.getRole(), ug.getAdduser());
    }

    public int insertUserGroup(Integer user_id, Integer group_id, Integer role, Integer adduser) {
        return jdbcTemplate.update("INSERT INTO user_group(user_id, group_id, role, adduser) VALUE(?,?,?,?)", user_id,
                group_id, role, adduser);
    }

    public int deleteUserFromGroup(Integer user_id, Integer group_id) {
        return jdbcTemplate.update("DELETE FROM user_group WHERE user_id=? AND group_id=?", user_id, group_id);
    }

    public int deleteUserFromGroup(Integer ugId) {
        return jdbcTemplate.update("DELETE FROM user_group WHERE id=?", ugId);
    }

    public int updateUserPersimion(Integer userId, Integer groupId, Integer role, Integer adduser) {
        return jdbcTemplate.update("UPDATE user_group SET role=?, adduser=?  WHERE user_id=? AND group_id=?", role,
                adduser, userId, groupId);
    }

}
