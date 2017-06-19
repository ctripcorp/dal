package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.UserProject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class DaoOfUserProject {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<UserProject> getAllUserProjects() {
        return jdbcTemplate.query("SELECT id, project_id, user_no FROM user_project", new RowMapper<UserProject>() {
            public UserProject mapRow(ResultSet rs, int rowNum) throws SQLException {
                return UserProject.visitRow(rs);
            }
        });
    }

    public List<UserProject> getUserProjectsByUser(String userNo) {
        return jdbcTemplate.query("SELECT id, project_id, user_no FROM user_project WHERE user_no = ?",
                new Object[] {userNo}, new RowMapper<UserProject>() {
                    public UserProject mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return UserProject.visitRow(rs);
                    }
                });
    }

    public UserProject getUserProject(int project_id, String userNo) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id,project_id, user_no FROM user_project WHERE project_id=? AND user_no = ?",
                    new Object[] {project_id, userNo}, new RowMapper<UserProject>() {
                        public UserProject mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return UserProject.visitRow(rs);
                        }
                    });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UserProject getMinUserProjectByProjectId(int project_id) {
        return jdbcTemplate.queryForObject(
                "SELECT id,project_id, user_no FROM user_project WHERE id=(SELECT min(id) FROM user_project WHERE project_id=?)",
                new Object[] {project_id}, new RowMapper<UserProject>() {
                    public UserProject mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return UserProject.visitRow(rs);
                    }
                });
    }

    public int insertUserProject(final UserProject data) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps =
                        connection.prepareStatement("INSERT INTO user_project (project_id, user_no ) VALUES (?,?)",
                                Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, data.getProject_id());
                ps.setString(2, data.getUserNo());
                return ps;
            }
        }, holder);

        return holder.getKey().intValue();
    }

    public int deleteUserProject(int project_id) {
        return jdbcTemplate.update("DELETE FROM user_project WHERE project_id=?", new Object[] {project_id});
    }

}
