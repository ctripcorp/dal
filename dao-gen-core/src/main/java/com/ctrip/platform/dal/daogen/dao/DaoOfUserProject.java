package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.UserProject;
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
        try {
            return jdbcTemplate.query("SELECT id, project_id, user_no FROM user_project", new RowMapper<UserProject>() {
                public UserProject mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return UserProject.visitRow(rs);
                }
            });
        } catch (Throwable e) {
            throw e;
        }
    }

    public List<UserProject> getUserProjectsByUser(String userNo) {
        try {
            return jdbcTemplate.query("SELECT id, project_id, user_no FROM user_project WHERE user_no = ?",
                    new Object[] {userNo}, new RowMapper<UserProject>() {
                        public UserProject mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return UserProject.visitRow(rs);
                        }
                    });
        } catch (Throwable e) {
            throw e;
        }
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
        } catch (Throwable e) {
            throw e;
        }
    }

    public UserProject getMinUserProjectByProjectId(int project_id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id,project_id, user_no FROM user_project WHERE id=(SELECT min(id) FROM user_project WHERE project_id=?)",
                    new Object[] {project_id}, new RowMapper<UserProject>() {
                        public UserProject mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return UserProject.visitRow(rs);
                        }
                    });
        } catch (Throwable e) {
            throw e;
        }
    }

    public int insertUserProject(final UserProject data) {
        try {
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
        } catch (Throwable e) {
            throw e;
        }
    }

    public int deleteUserProject(int project_id) {
        try {
            return jdbcTemplate.update("DELETE FROM user_project WHERE project_id=?", new Object[] {project_id});
        } catch (Throwable e) {
            throw e;
        }
    }

}
