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
    private Integer dalGroupId;

    @Column(name = "db_address")
    @Type(value = Types.VARCHAR)
    private String dbAddress;

    @Column(name = "db_port")
    @Type(value = Types.VARCHAR)
    private String dbPort;

    @Column(name = "db_user")
    @Type(value = Types.VARCHAR)
    private String dbUser;

    @Column(name = "db_password")
    @Type(value = Types.VARCHAR)
    private String dbPassword;

    @Column(name = "db_catalog")
    @Type(value = Types.VARCHAR)
    private String dbCatalog;

    @Column(name = "db_providerName")
    @Type(value = Types.VARCHAR)
    private String dbProvidername;

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

    public Integer getDalGroupId() {
        return dalGroupId;
    }

    public void setDalGroupId(Integer dalGroupId) {
        this.dalGroupId = dalGroupId;
    }

    public String getDbAddress() {
        return dbAddress;
    }

    public void setDbAddress(String dbAddress) {
        this.dbAddress = dbAddress;
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbCatalog() {
        return dbCatalog;
    }

    public void setDbCatalog(String dbCatalog) {
        this.dbCatalog = dbCatalog;
    }

    public String getDbProvidername() {
        return dbProvidername;
    }

    public void setDbProvidername(String dbProvidername) {
        this.dbProvidername = dbProvidername;
    }

    @Override
    public int compareTo(DalGroupDB o) {
        return dbname.compareTo(o.getDbname());
    }

    @Override
    public String toString() {
        return "DalGroupDB [id=" + id + ", dbname=" + dbname + ", comment=" + comment + ", dal_group_id=" + dalGroupId
                + ", db_address=" + dbAddress + ", db_port=" + dbPort + ", db_user=" + dbUser + ", db_password="
                + dbPassword + ", db_catalog=" + dbCatalog + ", db_providerName=" + dbProvidername + "]";
    }

}
