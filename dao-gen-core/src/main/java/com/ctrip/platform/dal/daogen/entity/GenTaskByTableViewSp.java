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
@Table(name = "task_table")
public class GenTaskByTableViewSp implements Comparable<GenTaskByTableViewSp>, DalPojo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "project_id")
    @Type(value = Types.INTEGER)
    private Integer projectId;

    @Column(name = "db_name")
    @Type(value = Types.VARCHAR)
    private String dbName;

    @Column(name = "table_names")
    @Type(value = Types.LONGVARCHAR)
    private String tableNames;

    @Column(name = "view_names")
    @Type(value = Types.LONGVARCHAR)
    private String viewNames;

    @Column(name = "sp_names")
    @Type(value = Types.LONGVARCHAR)
    private String spNames;

    @Column(name = "prefix")
    @Type(value = Types.VARCHAR)
    private String prefix;

    @Column(name = "suffix")
    @Type(value = Types.VARCHAR)
    private String suffix;

    @Column(name = "cud_by_sp")
    @Type(value = Types.BIT)
    private Boolean cudBySp;

    @Column(name = "pagination")
    @Type(value = Types.BIT)
    private Boolean pagination;

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

    @Column(name = "sql_style")
    @Type(value = Types.VARCHAR)
    private String sqlStyle;

    @Column(name = "api_list")
    @Type(value = Types.LONGVARCHAR)
    private String apiList;

    @Column(name = "approved")
    @Type(value = Types.INTEGER)
    private Integer approved;

    @Column(name = "approveMsg")
    @Type(value = Types.LONGVARCHAR)
    private String approveMsg;

    private String allInOneName;

    private String str_approved;

    private String str_update_time = "";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableNames() {
        return tableNames;
    }

    public void setTableNames(String tableNames) {
        this.tableNames = tableNames;
    }

    public String getViewNames() {
        return viewNames;
    }

    public void setViewNames(String viewNames) {
        this.viewNames = viewNames;
    }

    public String getSpNames() {
        return spNames;
    }

    public void setSpNames(String spNames) {
        this.spNames = spNames;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Boolean getCudBySp() {
        return cudBySp;
    }

    public void setCudBySp(Boolean cudBySp) {
        this.cudBySp = cudBySp;
    }

    public Boolean getPagination() {
        return pagination;
    }

    public void setPagination(Boolean pagination) {
        this.pagination = pagination;
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

    public String getSqlStyle() {
        return sqlStyle;
    }

    public void setSqlStyle(String sqlStyle) {
        this.sqlStyle = sqlStyle;
    }

    public String getApiList() {
        return apiList;
    }

    public void setApiList(String apiList) {
        this.apiList = apiList;
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

    public String getApprovePreview() {
        String str = getTableNames();
        if (getViewNames() != null && !getViewNames().isEmpty()) {
            str += "," + getViewNames();
        }
        if (getSpNames() != null && !getSpNames().isEmpty()) {
            str += "," + getSpNames();
        }
        return str;
    }

    @Override
    public int compareTo(GenTaskByTableViewSp o) {
        return getAllInOneName().compareTo(o.getAllInOneName());
    }

}
