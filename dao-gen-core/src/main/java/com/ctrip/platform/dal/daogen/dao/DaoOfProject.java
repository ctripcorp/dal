
package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.Project;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class DaoOfProject {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Project> getAllProjects() {
        try {
            return this.jdbcTemplate.query("SELECT id, name, namespace,dal_group_id,dal_config_name,update_user_no,update_time FROM project",
                    new RowMapper<Project>() {
                        public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return Project.visitRow(rs);
                        }
                    });
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Project> getProjectByIDS(Object[] iD) {
        try {
            return this.jdbcTemplate.query(String.format("select id, name, namespace,dal_group_id,dal_config_name,update_user_no,update_time from project where id in (%s) ", StringUtils.join(iD, ",")),
                    new RowMapper<Project>() {
                        public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return Project.visitRow(rs);
                        }
                    });
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Project> getProjectByGroupId(int groupId) {
        try {
            return this.jdbcTemplate.query("SELECT id, name, namespace,dal_group_id,dal_config_name,update_user_no,update_time FROM project WHERE dal_group_id=? ",
                    new Object[]{groupId}, new RowMapper<Project>() {
                        public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return Project.visitRow(rs);
                        }
                    });
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Project getProjectByID(int iD) {
        try {
            return this.jdbcTemplate.queryForObject("SELECT id, name, namespace,dal_group_id,dal_config_name,update_user_no,update_time FROM project WHERE id=?",
                    new Object[]{iD}, new RowMapper<Project>() {
                        public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return Project.visitRow(rs);
                        }
                    });
        } catch (DataAccessException ex) {
            return null;
        }
    }

    public List<Project> getProjectByConfigname(String dal_config_name) {
        return this.jdbcTemplate.query("SELECT id, name, namespace,dal_group_id,dal_config_name,update_user_no,update_time FROM project WHERE dal_config_name=?",
                new Object[]{dal_config_name}, new RowMapper<Project>() {
                    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return Project.visitRow(rs);
                    }
                });
    }

    public int insertProject(final Project project) {
        KeyHolder holder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO project ( name, namespace, dal_group_id,dal_config_name,update_user_no,update_time) VALUES (?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, project.getName());
                ps.setString(2, project.getNamespace());
                ps.setInt(3, project.getDal_group_id());
                ps.setString(4, project.getDal_config_name());
                ps.setString(5, project.getUpdate_user_no());
                ps.setTimestamp(6, project.getUpdate_time());
                return ps;
            }
        }, holder);

        return holder.getKey().intValue();

    }

    public int updateProject(Project project) {
        try {
            return this.jdbcTemplate.update("UPDATE project SET name=?, namespace=?, dal_config_name=?, update_user_no=?, update_time=? WHERE id=?",
                    project.getName(), project.getNamespace(),
                    project.getDal_config_name(), project.getUpdate_user_no(),
                    project.getUpdate_time(), project.getId());
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public int updateProjectGroupById(int groupId, int id) {
        try {
            return this.jdbcTemplate.update("UPDATE project SET dal_group_id=? WHERE id=?", groupId, id);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public int deleteProject(Project project) {
        try {
            return this.jdbcTemplate.update("DELETE FROM project WHERE id=?", project.getId());
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return -1;
        }
    }
}