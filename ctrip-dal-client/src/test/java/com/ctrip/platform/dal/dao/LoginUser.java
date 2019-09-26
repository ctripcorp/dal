package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.sql.Types;

/**
 * Created by taochen on 2019/8/28.
 */
@Entity
@Database(name = "dao_test")
@Table(name = "login_users")
public class LoginUser implements DalPojo {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private int id;

    @Column(name = "user_no")
    @Type(value = Types.VARCHAR)
    private String user_no;

    @Column(name = "user_name")
    @Type(value = Types.VARCHAR)
    private String user_name;

    @Column(name = "user_email")
    @Type(value = Types.VARCHAR)
    private String user_email;

    @Column(name = "password")
    @Type(value = Types.VARCHAR)
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_no() {
        return user_no;
    }

    public void setUser_no(String user_no) {
        this.user_no = user_no;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
