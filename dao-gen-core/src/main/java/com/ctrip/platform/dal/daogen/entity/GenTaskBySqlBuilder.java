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
@Table(name = "task_auto")
public class GenTaskBySqlBuilder implements Comparable<GenTaskBySqlBuilder>, DalPojo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "project_id")
    @Type(value = Types.INTEGER)
    private Integer project_id;

    @Column(name = "db_name")
    @Type(value = Types.VARCHAR)
    private String databaseSetName;

    @Column(name = "table_name")
    @Type(value = Types.VARCHAR)
    private String table_name;

    @Column(name = "class_name")
    @Type(value = Types.VARCHAR)
    private String class_name;

    @Column(name = "method_name")
    @Type(value = Types.VARCHAR)
    private String method_name;

    @Column(name = "sql_style")
    @Type(value = Types.VARCHAR)
    private String sql_style;

    @Column(name = "crud_type")
    @Type(value = Types.VARCHAR)
    private String crud_type;

    @Column(name = "fields")
    @Type(value = Types.LONGVARCHAR)
    private String fields;

    @Column(name = "where_condition")
    @Type(value = Types.LONGVARCHAR)
    private String condition;

    @Column(name = "sql_content")
    @Type(value = Types.LONGVARCHAR)
    private String sql_content;

    @Column(name = "generated")
    @Type(value = Types.BIT)
    private Boolean generated;

    @Column(name = "version")
    @Type(value = Types.INTEGER)
    private Integer version;

    @Column(name = "update_user_no")
    @Type(value = Types.VARCHAR)
    private String update_user_no;

    @Column(name = "update_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp update_time;

    @Column(name = "comment")
    @Type(value = Types.LONGVARCHAR)
    private String comment;

    @Column(name = "scalarType")
    @Type(value = Types.VARCHAR)
    private String scalarType;

    @Column(name = "pagination")
    @Type(value = Types.BIT)
    private Boolean pagination;

    @Column(name = "orderby")
    @Type(value = Types.VARCHAR)
    private String orderby;

    @Column(name = "approved")
    @Type(value = Types.INTEGER)
    private Integer approved;

    @Column(name = "approveMsg")
    @Type(value = Types.LONGVARCHAR)
    private String approveMsg;

    @Column(name = "hints")
    @Type(value = Types.VARCHAR)
    private String hints;

    @Column(name = "length")
    @Type(value = Types.TINYINT)
    private Boolean length;

    private String allInOneName;
    private String str_update_time;
    private String str_approved;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public String getDatabaseSetName() {
        return databaseSetName;
    }

    public void setDatabaseSetName(String databaseSetName) {
        this.databaseSetName = databaseSetName;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getMethod_name() {
        return method_name;
    }

    public void setMethod_name(String method_name) {
        this.method_name = method_name;
    }

    public String getSql_style() {
        return sql_style;
    }

    public void setSql_style(String sql_style) {
        this.sql_style = sql_style;
    }

    public String getCrud_type() {
        return crud_type;
    }

    public void setCrud_type(String crud_type) {
        this.crud_type = crud_type;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getSql_content() {
        return sql_content;
    }

    public void setSql_content(String sql_content) {
        this.sql_content = sql_content;
    }

    public Boolean getGenerated() {
        return generated;
    }

    public void setGenerated(Boolean generated) {
        this.generated = generated;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getScalarType() {
        return scalarType;
    }

    public void setScalarType(String scalarType) {
        this.scalarType = scalarType;
    }

    public Boolean getPagination() {
        return pagination;
    }

    public void setPagination(Boolean pagination) {
        this.pagination = pagination;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public Integer getApproved() {
        return approved;
    }

    public void setApproved(Integer approved) {
        this.approved = approved;
    }

    public String getApproveMsg() {
        return approveMsg;
    }

    public void setApproveMsg(String approveMsg) {
        this.approveMsg = approveMsg;
    }

    public String getHints() {
        return hints;
    }

    public void setHints(String hints) {
        this.hints = hints;
    }

    public Boolean getLength() {
        return length;
    }

    public void setLength(Boolean length) {
        this.length = length;
    }

    public String getAllInOneName() {
        return allInOneName;
    }

    public void setAllInOneName(String allInOneName) {
        this.allInOneName = allInOneName;
    }

    public void setStr_update_time(String str_update_time) {
        this.str_update_time = str_update_time;
    }

    public String getStr_update_time() {
        return str_update_time;
    }

    public void setStr_approved(String str_approved) {
        this.str_approved = str_approved;
    }

    public String getStr_approved() {
        return str_approved;
    }

    @Override
    public int compareTo(GenTaskBySqlBuilder o) {
        int result = getAllInOneName().compareTo(o.getAllInOneName());
        if (result != 0)
            return result;

        result = getTable_name().compareTo(o.getTable_name());
        if (result != 0)
            return result;

        return getMethod_name().compareTo(o.getMethod_name());
    }
}
