package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.daogen.utils.ConnectionStringUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Database(name = "dao")
@Table(name = "databasesetentry")
public class DatabaseSetEntry implements Comparable<DatabaseSetEntry>, DalPojo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "name")
    @Type(value = Types.LONGVARCHAR)
    private String name;

    @Column(name = "databaseType")
    @Type(value = Types.VARCHAR)
    private String databaseType;

    @Column(name = "sharding")
    @Type(value = Types.VARCHAR)
    private String sharding;

    @Column(name = "connectionString")
    @Type(value = Types.VARCHAR)
    private String connectionString;

    @Column(name = "databaseSet_Id")
    @Type(value = Types.INTEGER)
    private Integer databaseSet_Id;

    @Column(name = "update_user_no")
    @Type(value = Types.VARCHAR)
    private String update_user_no;

    @Column(name = "update_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp update_time;

    private String str_update_time;

    private String providerName;

    private String userName;

    private String password;

    private String dbAddress;

    private String dbPort;

    private String dbCatalog;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getSharding() {
        return sharding;
    }

    public void setSharding(String sharding) {
        this.sharding = sharding;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public Integer getDatabaseSet_Id() {
        return databaseSet_Id;
    }

    public void setDatabaseSet_Id(Integer databaseSet_Id) {
        this.databaseSet_Id = databaseSet_Id;
    }

    public String getUpdate_user_no() {
        return update_user_no;
    }

    public void setUpdate_user_no(String update_user_no) {
        this.update_user_no = update_user_no;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public String getStr_update_time() {
        return str_update_time;
    }

    public void setStr_update_time(String str_update_time) {
        this.str_update_time = str_update_time;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getDbCatalog() {
        return dbCatalog;
    }

    public void setDbCatalog(String dbCatalog) {
        this.dbCatalog = dbCatalog;
    }

    public String getAllInOneConnectionString() {
        return ConnectionStringUtil.GetConnectionString(getProviderName().toLowerCase(), getDbAddress(), getDbPort(),
                getUserName(), getPassword(), getDbCatalog());
    }

    @Override
    public int compareTo(DatabaseSetEntry o) {
        return (id + name + databaseType + sharding + connectionString)
                .compareTo(o.getId() + o.getName() + o.getDatabaseType() + o.getSharding() + o.getConnectionString());
    }

}
