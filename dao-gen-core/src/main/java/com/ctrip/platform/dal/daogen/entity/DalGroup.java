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
@Table(name = "dal_group")
public class DalGroup implements Comparable<DalGroup>, DalPojo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "group_name")
    @Type(value = Types.VARCHAR)
    private String groupName;

    @Column(name = "group_comment")
    @Type(value = Types.LONGVARCHAR)
    private String groupComment;

    @Column(name = "create_user_no")
    @Type(value = Types.VARCHAR)
    private String createUserNo;

    @Column(name = "create_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp createTime;

    private String text;

    private String icon;

    private boolean children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupComment() {
        return groupComment;
    }

    public void setGroupComment(String groupComment) {
        this.groupComment = groupComment;
    }

    public String getCreateUserNo() {
        return createUserNo;
    }

    public void setCreateUserNo(String createUserNo) {
        this.createUserNo = createUserNo;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isChildren() {
        return children;
    }

    public void setChildren(boolean children) {
        this.children = children;
    }

    @Override
    public int compareTo(DalGroup o) {
        return groupName.compareTo(o.getGroupName());
    }

}
