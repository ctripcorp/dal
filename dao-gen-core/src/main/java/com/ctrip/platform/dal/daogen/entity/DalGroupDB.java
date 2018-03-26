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
@Table(name = "alldbs")
public class DalGroupDB implements Comparable<DalGroupDB>, DalPojo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "dbname")
    @Type(value = Types.VARCHAR)
    private String dbname;

    @Column(name = "comment")
    @Type(value = Types.LONGVARCHAR)
    private String comment;

    @Column(name = "dal_group_id")
    @Type(value = Types.INTEGER)
    private Integer dal_group_id;

    @Column(name = "db_address")
    @Type(value = Types.VARCHAR)
    private String db_address;

    @Column(name = "db_port")
    @Type(value = Types.VARCHAR)
    private String db_port;

    @Column(name = "db_user")
    @Type(value = Types.VARCHAR)
    private String db_user;

    @Column(name = "db_password")
    @Type(value = Types.VARCHAR)
    private String db_password;

    @Column(name = "db_catalog")
    @Type(value = Types.VARCHAR)
    private String db_catalog;

    @Column(name = "db_providerName")
    @Type(value = Types.VARCHAR)
    private String db_providerName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getDal_group_id() {
        return dal_group_id;
    }

    public void setDal_group_id(Integer dal_group_id) {
        this.dal_group_id = dal_group_id;
    }

    public String getDb_address() {
        return db_address;
    }

    public void setDb_address(String db_address) {
        this.db_address = db_address;
    }

    public String getDb_port() {
        return db_port;
    }

    public void setDb_port(String db_port) {
        this.db_port = db_port;
    }

    public String getDb_user() {
        return db_user;
    }

    public void setDb_user(String db_user) {
        this.db_user = db_user;
    }

    public String getDb_password() {
        return db_password;
    }

    public void setDb_password(String db_password) {
        this.db_password = db_password;
    }

    public String getDb_catalog() {
        return db_catalog;
    }

    public void setDb_catalog(String db_catalog) {
        this.db_catalog = db_catalog;
    }

    public String getDb_providerName() {
        return db_providerName;
    }

    public void setDb_providerName(String db_providerName) {
        this.db_providerName = db_providerName;
    }

    @Override
    public int compareTo(DalGroupDB o) {
        return dbname.compareTo(o.getDbname());
    }

    @Override
    public String toString() {
        return "DalGroupDB [id=" + id + ", dbname=" + dbname + ", comment=" + comment + ", dal_group_id=" + dal_group_id
                + ", db_address=" + db_address + ", db_port=" + db_port + ", db_user=" + db_user + ", db_password="
                + db_password + ", db_catalog=" + db_catalog + ", db_providerName=" + db_providerName + "]";
    }

}
