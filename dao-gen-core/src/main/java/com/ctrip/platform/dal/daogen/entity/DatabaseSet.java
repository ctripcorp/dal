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
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Database(name = "dao")
@Table(name = "databaseset")
public class DatabaseSet implements Comparable<DatabaseSet>, DalPojo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "name")
    @Type(value = Types.VARCHAR)
    private String name;

    @Column(name = "provider")
    @Type(value = Types.VARCHAR)
    private String provider;

    @Column(name = "shardingStrategy")
    @Type(value = Types.LONGVARCHAR)
    private String shardingStrategy;

    @Column(name = "groupId")
    @Type(value = Types.INTEGER)
    private Integer groupId;

    @Column(name = "update_user_no")
    @Type(value = Types.VARCHAR)
    private String update_user_no;

    @Column(name = "update_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp update_time;

    private String str_update_time;

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

    public boolean hasShardingStrategy() {
        return shardingStrategy != null && !shardingStrategy.isEmpty();
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
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

    @Override
    public int compareTo(DatabaseSet o) {
        return name.compareTo(o.getName());
    }

}
