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
@Table(name = "task_sql")
public class GenTaskByFreeSql implements Comparable<GenTaskByFreeSql>, DalPojo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "db_name")
    @Type(value = Types.VARCHAR)
    private String dbName;

    @Column(name = "class_name")
    @Type(value = Types.VARCHAR)
    private String className;

    @Column(name = "pojo_name")
    @Type(value = Types.VARCHAR)
    private String pojoName;

    @Column(name = "method_name")
    @Type(value = Types.VARCHAR)
    private String methodName;

    @Column(name = "crud_type")
    @Type(value = Types.VARCHAR)
    private String crudType;

    @Column(name = "sql_content")
    @Type(value = Types.LONGVARCHAR)
    private String sqlContent;

    @Column(name = "project_id")
    @Type(value = Types.INTEGER)
    private Integer projectId;

    @Column(name = "parameters")
    @Type(value = Types.LONGVARCHAR)
    private String parameters;

    @Column(name = "generated")
    @Type(value = Types.BIT)
    private Boolean generated;

    @Column(name = "version")
    @Type(value = Types.INTEGER)
    private Integer version;

    @Column(name = "update_user_no")
    @Type(value = Types.VARCHAR)
    private String updateUserNo;

    @Column(name = "update_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp updateTime;

    @Column(name = "comment")
    @Type(value = Types.LONGVARCHAR)
    private String comment;

    @Column(name = "scalarType")
    @Type(value = Types.VARCHAR)
    private String scalarType;

    @Column(name = "pojoType")
    @Type(value = Types.VARCHAR)
    private String pojoType;

    @Column(name = "pagination")
    @Type(value = Types.BIT)
    private Boolean pagination;

    @Column(name = "sql_style")
    @Type(value = Types.VARCHAR)
    private String sqlStyle;

    @Column(name = "approved")
    @Type(value = Types.INTEGER)
    private Integer approved;

    @Column(name = "approveMsg")
    @Type(value = Types.LONGVARCHAR)
    private String approveMsg;

    @Column(name = "hints")
    @Type(value = Types.VARCHAR)
    private String hints;

    private String allInOneName;

    private String str_approved;

    private String str_update_time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPojoName() {
        return pojoName;
    }

    public void setPojoName(String pojoName) {
        this.pojoName = pojoName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getCrudType() {
        return crudType;
    }

    public void setCrudType(String crudType) {
        this.crudType = crudType;
    }

    public String getSqlContent() {
        return sqlContent;
    }

    public void setSqlContent(String sqlContent) {
        this.sqlContent = sqlContent;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
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

    public String getUpdateUserNo() {
        return updateUserNo;
    }

    public void setUpdateUserNo(String updateUserNo) {
        this.updateUserNo = updateUserNo;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
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

    public String getPojoType() {
        return pojoType;
    }

    public void setPojoType(String pojoType) {
        this.pojoType = pojoType;
    }

    public Boolean getPagination() {
        return pagination;
    }

    public void setPagination(Boolean pagination) {
        this.pagination = pagination;
    }

    public String getSqlStyle() {
        return sqlStyle;
    }

    public void setSqlStyle(String sqlStyle) {
        this.sqlStyle = sqlStyle;
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
    public int compareTo(GenTaskByFreeSql o) {
        int result = getAllInOneName().compareTo(o.getAllInOneName());
        if (result != 0)
            return result;

        result = getClassName().compareTo(o.getClassName());
        if (result != 0)
            return result;

        return getMethodName().compareTo(o.getMethodName());
    }

}
