package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.LoginUser;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class DaoOfLoginUser {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<LoginUser> getAllUsers() {
        return jdbcTemplate.query("SELECT id, user_no, user_name, user_email, password FROM login_users",
                new RowMapper<LoginUser>() {
                    public LoginUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return LoginUser.visitRow(rs);
                    }
                });
    }

    public LoginUser getUserById(int userId) {
        try {
            return jdbcTemplate.queryForObject(
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
        if (userNo == null || userNo.isEmpty()) {
            return null;
        }
        try {
            return jdbcTemplate.queryForObject(
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
            return jdbcTemplate.query(sql, new Object[]{groupId}, new RowMapper<LoginUser>() {
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
        if (user == null || user.getUserNo() == null) {
            return -1;
        }
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO login_users ( user_no, user_name, user_email, password ) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE user_no = ?",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUserNo());
                ps.setString(2, user.getUserName());
                ps.setString(3, user.getUserEmail());
                ps.setString(4, user.getPassword());
                ps.setString(5, user.getUserNo());
                return ps;
            }
        }, holder);

        return holder.getKey().intValue();
    }

    public int updateUser(LoginUser user) {
        if (user == null) {
            return -1;
        }
        try {
            return jdbcTemplate.update("UPDATE login_users SET user_no=?, user_name=?, user_email=? WHERE id=?",
                    user.getUserNo(), user.getUserName(), user.getUserEmail(), user.getId());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int updateUserPassword(LoginUser user) {
        if (user == null) {
            return -1;
        }
        try {
            return jdbcTemplate.update("UPDATE login_users SET password=? WHERE id=?", user.getPassword(), user.getId());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int deleteUser(int userId) {
        try {
            return jdbcTemplate.update("DELETE FROM login_users WHERE id=?", userId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
