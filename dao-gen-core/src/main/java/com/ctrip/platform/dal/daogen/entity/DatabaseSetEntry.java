package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.daogen.utils.ConnectionStringUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseSetEntry implements Comparable<DatabaseSetEntry> {
    private int id;
    private String name;
    private String databaseType;
    private String sharding;
    private String connectionString;
    //private String allInOneConnectionString;
    private String providerName;

    private int databaseSet_Id;

    private String update_user_no;
    private Timestamp update_time;
    private String str_update_time = "";

    private String userName;
    private String password;
    private String dbAddress;
    private String dbPort;
    private String dbCatalog;

    public static DatabaseSetEntry visitRow(ResultSet rs) throws SQLException {
        DatabaseSetEntry entry = new DatabaseSetEntry();
        entry.setId(rs.getInt(1));
        entry.setName(rs.getString(2));
        entry.setDatabaseType(rs.getString(3));
        entry.setSharding(rs.getString(4));
        entry.setConnectionString(rs.getString(5));
        entry.setDatabaseSet_Id(rs.getInt(6));
        entry.setUpdate_user_no(rs.getString("update_user_no"));
        entry.setUpdate_time(rs.getTimestamp("update_time"));
        try {
            Date date = new Date(entry.getUpdate_time().getTime());
            entry.setStr_update_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        } catch (Throwable e) {
        }
        return entry;
    }

    @Override
    public int compareTo(DatabaseSetEntry o) {
        return (this.id + this.name + this.databaseType + this.sharding + this.connectionString).compareTo(o.getId() + o.getName() + o.getDatabaseType() + o.getSharding() + o.getConnectionString());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        return sharding == null ? "" : sharding;
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

    public String getAllInOneConnectionString() {
        return ConnectionStringUtil.GetConnectionString(getProviderName().toLowerCase(), getDbAddress(), getDbPort(), getUserName(), getPassword(), getDbCatalog());
    }

    /*
    public void setAllInOneConnectionString(String allInOneConnectionString) {
        this.allInOneConnectionString = allInOneConnectionString;
    }
    */

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public int getDatabaseSet_Id() {
        return databaseSet_Id;
    }

    public void setDatabaseSet_Id(int databaseSet_Id) {
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
}
