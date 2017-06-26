package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name = "dao")
@Table(name = "login_users")
public class LoginUser implements DalPojo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "user_no")
    @Type(value = Types.VARCHAR)
    private String userNo;

    @Column(name = "user_name")
    @Type(value = Types.VARCHAR)
    private String userName;

    @Column(name = "user_email")
    @Type(value = Types.VARCHAR)
    private String userEmail;

    @Column(name = "password")
    @Type(value = Types.VARCHAR)
    private String password;

    // 以下属性仅在组员管理界面中使用
    private String role;// 组员的权限
    private String adduser;// 是否可以添加组员
    private boolean isDalTeam = false;// true:是DAL Team，false:是正常用户

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

}
