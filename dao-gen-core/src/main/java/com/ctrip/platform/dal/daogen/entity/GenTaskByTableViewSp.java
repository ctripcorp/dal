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
    private Integer project_id;

    @Column(name = "db_name")
    @Type(value = Types.VARCHAR)
    private String databaseSetName;

    @Column(name = "table_names")
    @Type(value = Types.LONGVARCHAR)
    private String table_names;

    @Column(name = "view_names")
    @Type(value = Types.LONGVARCHAR)
    private String view_names;

    @Column(name = "sp_names")
    @Type(value = Types.LONGVARCHAR)
    private String sp_names;

    @Column(name = "prefix")
    @Type(value = Types.VARCHAR)
    private String prefix;

    @Column(name = "suffix")
    @Type(value = Types.VARCHAR)
    private String suffix;

    @Column(name = "cud_by_sp")
    @Type(value = Types.BIT)
    private Boolean cud_by_sp;

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
    private String update_user_no;

    @Column(name = "update_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp update_time;

    @Column(name = "comment")
    @Type(value = Types.LONGVARCHAR)
    private String comment;

    @Column(name = "sql_style")
    @Type(value = Types.VARCHAR)
    private String sql_style;

    @Column(name = "api_list")
    @Type(value = Types.LONGVARCHAR)
    private String api_list;

    @Column(name = "approved")
    @Type(value = Types.INTEGER)
    private Integer approved;

    @Column(name = "approveMsg")
    @Type(value = Types.LONGVARCHAR)
    private String approveMsg;

    @Column(name = "length")
    @Type(value = Types.TINYINT)
    private Boolean length;

    private String allInOneName;

    private String str_approved;

    private String str_update_time = "";

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

    public String getTable_names() {
        return table_names;
    }

    public void setTable_names(String table_names) {
        this.table_names = table_names;
    }

    public String getView_names() {
        return view_names;
    }

    public void setView_names(String view_names) {
        this.view_names = view_names;
    }

    public String getSp_names() {
        return sp_names;
    }

    public void setSp_names(String sp_names) {
        this.sp_names = sp_names;
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

    public Boolean getCud_by_sp() {
        return cud_by_sp;
    }

    public void setCud_by_sp(Boolean cud_by_sp) {
        this.cud_by_sp = cud_by_sp;
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

    public String getSql_style() {
        return sql_style;
    }

    public void setSql_style(String sql_style) {
        this.sql_style = sql_style;
    }

    public String getApi_list() {
        return api_list;
    }

    public void setApi_list(String api_list) {
        this.api_list = api_list;
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
        String str = getTable_names();
        if (getView_names() != null && !getView_names().isEmpty()) {
            str += "," + getView_names();
        }
        if (getSp_names() != null && !getSp_names().isEmpty()) {
            str += "," + getSp_names();
        }
        return str;
    }

    public Boolean getLength() {
        return length;
    }

    public void setLength(Boolean length) {
        this.length = length;
    }

    @Override
    public int compareTo(GenTaskByTableViewSp o) {
        return getAllInOneName().compareTo(o.getAllInOneName());
    }

}
