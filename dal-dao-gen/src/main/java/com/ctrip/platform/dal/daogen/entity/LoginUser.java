package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginUser {
    private int id;

    private String userNo;

    private String userName;

    private String userEmail;

    private String password;

    // 以下属性仅在组员管理界面中使用
    private String role;// 组员的权限
    private String adduser;// 是否可以添加组员
    private boolean isDalTeam = false;// true:是DAL Team，false:是正常用户

    public static LoginUser visitRow(ResultSet rs) throws SQLException {
        if (rs == null) {
            return null;
        }
        LoginUser user = new LoginUser();
        user.setId(rs.getInt(1));
        user.setUserNo(rs.getString(2));
        user.setUserName(rs.getString(3));
        user.setUserEmail(rs.getString(4));
        user.setPassword(rs.getString(5));
        return user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAdduser() {
        return adduser;
    }

    public void setAdduser(String adduser) {
        this.adduser = adduser;
    }

    public boolean isDalTeam() {
        return isDalTeam;
    }

    public void setDalTeam(boolean isDalTeam) {
        this.isDalTeam = isDalTeam;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
