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
    private String group_name;

    @Column(name = "group_comment")
    @Type(value = Types.LONGVARCHAR)
    private String group_comment;

    @Column(name = "create_user_no")
    @Type(value = Types.VARCHAR)
    private String create_user_no;

    @Column(name = "create_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp create_time;

    private String text;

    private String icon;

    private boolean children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_comment() {
        return group_comment;
    }

    public void setGroup_comment(String group_comment) {
        this.group_comment = group_comment;
    }

    public String getCreate_user_no() {
        return create_user_no;
    }

    public void setCreate_user_no(String create_user_no) {
        this.create_user_no = create_user_no;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
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
        return group_name.compareTo(o.getGroup_name());
    }

}
