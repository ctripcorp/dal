package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.DalGroup;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DalGroupDao {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<DalGroup> getAllGroups() {
        return this.jdbcTemplate.query("SELECT id, group_name, group_comment,create_user_no, create_time FROM dal_group",
                new RowMapper<DalGroup>() {
                    public DalGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return DalGroup.visitRow(rs);
                    }
                });
    }

    public DalGroup getDalGroupById(Integer id) {
        List<DalGroup> groups = this.jdbcTemplate.query("SELECT id, group_name, group_comment,create_user_no, create_time FROM dal_group WHERE id = ?",
                new Object[]{id}, new RowMapper<DalGroup>() {
                    public DalGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return DalGroup.visitRow(rs);
                    }
                });
        return null != groups && groups.size() > 0 ? groups.get(0) : null;
    }

    public boolean isDalGroupExisted() {
        return this.jdbcTemplate.query("SELECT id, group_name, group_comment,create_user_no, create_time FROM dal_group LIMIT 1",
                new RowMapper<DalGroup>() {
                    public DalGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return DalGroup.visitRow(rs);
                    }
                }).size() > 0;
    }

    public int insertDalGroup(DalGroup group) {
        return this.jdbcTemplate.update("INSERT INTO dal_group(id, group_name, group_comment,create_user_no, create_time) VALUE(?,?,?,?,?)", group.getId(),
                group.getGroup_name(), group.getGroup_comment(),
                group.getCreate_user_no(), group.getCreate_time());
    }

    public int updateDalGroup(DalGroup group) {
        return this.jdbcTemplate.update("UPDATE dal_group SET group_name=?, group_comment=?, create_user_no=?, create_time=? WHERE id=?",
                group.getGroup_name(), group.getGroup_comment(),
                group.getCreate_user_no(), group.getCreate_time(), group.getId());
    }

    public int deleteDalGroup(Integer groupId) {
        return this.jdbcTemplate.update("DELETE FROM dal_group WHERE id=?", groupId);
    }
}
