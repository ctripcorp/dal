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
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<DalGroup> getAllGroups() {
        try {
            return jdbcTemplate.query("SELECT id, group_name, group_comment,create_user_no, create_time FROM dal_group",
                    new RowMapper<DalGroup>() {
                        public DalGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return DalGroup.visitRow(rs);
                        }
                    });
        } catch (Throwable e) {
            throw e;
        }
    }

    public DalGroup getDalGroupById(Integer id) {
        try {
            List<DalGroup> groups = jdbcTemplate.query(
                    "SELECT id, group_name, group_comment,create_user_no, create_time FROM dal_group WHERE id = ?",
                    new Object[] {id}, new RowMapper<DalGroup>() {
                        public DalGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return DalGroup.visitRow(rs);
                        }
                    });
            return null != groups && groups.size() > 0 ? groups.get(0) : null;
        } catch (Throwable e) {
            throw e;
        }
    }

    public boolean isDalGroupExisted() {
        try {
            return jdbcTemplate
                    .query("SELECT id, group_name, group_comment,create_user_no, create_time FROM dal_group LIMIT 1",
                            new RowMapper<DalGroup>() {
                                public DalGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                                    return DalGroup.visitRow(rs);
                                }
                            })
                    .size() > 0;
        } catch (Throwable e) {
            throw e;
        }
    }

    public int insertDalGroup(DalGroup group) {
        try {
            return jdbcTemplate.update(
                    "INSERT INTO dal_group(id, group_name, group_comment,create_user_no, create_time) VALUE(?,?,?,?,?)",
                    group.getId(), group.getGroup_name(), group.getGroup_comment(), group.getCreate_user_no(),
                    group.getCreate_time());
        } catch (Throwable e) {
            throw e;
        }
    }

    public int updateDalGroup(DalGroup group) {
        try {
            return jdbcTemplate.update(
                    "UPDATE dal_group SET group_name=?, group_comment=?, create_user_no=?, create_time=? WHERE id=?",
                    group.getGroup_name(), group.getGroup_comment(), group.getCreate_user_no(), group.getCreate_time(),
                    group.getId());
        } catch (Throwable e) {
            throw e;
        }
    }

    public int deleteDalGroup(Integer groupId) {
        try {
            return jdbcTemplate.update("DELETE FROM dal_group WHERE id=?", groupId);
        } catch (Throwable e) {
            throw e;
        }
    }

}
