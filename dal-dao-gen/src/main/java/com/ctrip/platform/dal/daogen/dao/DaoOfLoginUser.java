package com.ctrip.platform.dal.daogen.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.entity.Project;

public class DaoOfLoginUser {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<LoginUser> getAllUsers() {
        return this.jdbcTemplate.query("SELECT id, user_no, user_name, user_email, password FROM login_users",
                new RowMapper<LoginUser>() {
                    public LoginUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return LoginUser.visitRow(rs);
                    }
                });
    }

    public LoginUser getUserById(int userId) {
        try {
            return this.jdbcTemplate.queryForObject(
                    "SELECT id, user_no, user_name, user_email, password FROM login_users WHERE id = ?", new Object[]{userId},
                    new RowMapper<LoginUser>() {
                        public LoginUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return LoginUser.visitRow(rs);
                        }
                    });
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LoginUser getUserByNo(String userNo) {
        try {
            return this.jdbcTemplate.queryForObject(
                    "SELECT id, user_no, user_name, user_email, password FROM login_users WHERE user_no = ?",
                    new Object[]{userNo}, new RowMapper<LoginUser>() {
                        public LoginUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return LoginUser.visitRow(rs);
                        }
                    });
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<LoginUser> getUserByGroupId(int groupId) {
        String sql = "SELECT tb2.id, tb2.user_no, tb2.user_name, tb2.user_email, tb2.password, tb1.role, tb1.adduser FROM user_group tb1 "
                + " LEFT JOIN login_users tb2 ON tb1.user_id = tb2.id WHERE  tb1.group_id = ? ";
        try {
            return this.jdbcTemplate.query(sql, new Object[]{groupId}, new RowMapper<LoginUser>() {
                public LoginUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LoginUser user = new LoginUser();
                    user.setId(rs.getInt("id"));
                    user.setUserNo(rs.getString("user_no"));
                    user.setUserName(rs.getString("user_name"));
                    user.setUserEmail(rs.getString("user_email"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    user.setAdduser(rs.getString("adduser"));
                    return user;
                }
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int insertUser(final LoginUser user) {
        KeyHolder holder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO login_users ( user_no, user_name, user_email, password ) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE user_no = ?",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUserNo());
                ps.setString(2, user.getUserName());
                ps.setString(3, user.getUserEmail());
                ps.setString(4, user.getPassword());
                ps.setString(5, user.getPassword());
                return ps;
            }
        }, holder);

        return holder.getKey().intValue();
    }

    public int updateUser(LoginUser user) {
        try {
            return this.jdbcTemplate.update("UPDATE login_users SET user_no=?, user_name=?, user_email=? WHERE id=?",
                    user.getUserNo(), user.getUserName(), user.getUserEmail(), user.getId());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int deleteUser(int userId) {
        try {
            return this.jdbcTemplate.update("DELETE FROM login_users WHERE id=?", userId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
