package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseSet implements Comparable<DatabaseSet> {
    private int id;
    private String name;
    private String provider;
    private String shardingStrategy;
    private int groupId;

    private String update_user_no;
    private Timestamp update_time;
    private String str_update_time = "";

    public static DatabaseSet visitRow(ResultSet rs) throws SQLException {
        DatabaseSet set = new DatabaseSet();
        set.setId(rs.getInt(1));
        set.setName(rs.getString(2));
        set.setProvider(rs.getString(3));
        set.setShardingStrategy(rs.getString(4));
        set.setGroupId(rs.getInt(5));
        set.setUpdate_user_no(rs.getString("update_user_no"));
        set.setUpdate_time(rs.getTimestamp("update_time"));
        try {
            Date date = new Date(set.getUpdate_time().getTime());
            set.setStr_update_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        } catch (Throwable e) {
        }
        return set;
    }

    @Override
    public int compareTo(DatabaseSet o) {
        return this.name.compareTo(o.getName());
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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getShardingStrategy() {
        return shardingStrategy;
    }

    public void setShardingStrategy(String shardingStrategy) {
        this.shardingStrategy = shardingStrategy;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public boolean hasShardingStrategy() {
        return this.shardingStrategy != null && !this.shardingStrategy.isEmpty();
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

}
