package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.daogen.utils.DatabaseSetUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenTaskBySqlBuilder implements Comparable<GenTaskBySqlBuilder> {
    private int id;

    private int project_id;

    private String allInOneName;

    private String databaseSetName;

    private String table_name;

    private String class_name;

    private String method_name;

    private String sql_style;

    private String crud_type;

    private String fields;

    private String condition;

    private String sql_content;

    private boolean generated;

    private int version;

    private String update_user_no;
    private Timestamp update_time;
    private String str_update_time = "";
    private String comment;

    // 当crud_type取值为select时，此字段才有意义，可取值：Single、First、List，表示select返回的结果类型
    private String scalarType;
    // 是否增加分页方法，true：增加
    private boolean pagination;
    // 存放order by 信息，值demo：id，asc 或者 id，desc
    private String orderby;

    private int approved;
    private String str_approved;
    private String approveMsg;

    private String hints;

    public static GenTaskBySqlBuilder visitRow(ResultSet rs) throws SQLException {
        GenTaskBySqlBuilder task = new GenTaskBySqlBuilder();
        task.setId(rs.getInt(1));
        task.setProject_id(rs.getInt(2));

        String databaseSet = rs.getString(3);
        task.setAllInOneName(DatabaseSetUtils.getAllInOneName(databaseSet));
        task.setDatabaseSetName(databaseSet);

        task.setTable_name(rs.getString(4));
        task.setClass_name(rs.getString(5));
        task.setMethod_name(rs.getString(6));
        task.setSql_style(rs.getString(7));
        task.setCrud_type(rs.getString(8));
        task.setFields(rs.getString(9));
        task.setCondition(rs.getString(10));
        task.setSql_content(rs.getString(11));
        task.setGenerated(rs.getBoolean(12));
        task.setVersion(rs.getInt(13));
        task.setUpdate_user_no(rs.getString(14));
        task.setUpdate_time(rs.getTimestamp(15));
        task.setComment(rs.getString(16));
        task.setScalarType(rs.getString("scalarType"));
        task.setPagination(rs.getBoolean("pagination"));
        task.setOrderby(rs.getString("orderby"));
        task.setApproved(rs.getInt("approved"));
        task.setApproveMsg(rs.getString("approveMsg"));
        task.setHints(rs.getString("hints"));

        try {
            if (task.getApproved() == 1) {
                task.setStr_approved("未审批");
            } else if (task.getApproved() == 2) {
                task.setStr_approved("通过");
            } else if (task.getApproved() == 3) {
                task.setStr_approved("未通过");
            } else {
                task.setStr_approved("通过");
            }
            Date date = new Date(task.getUpdate_time().getTime());
            task.setStr_update_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        } catch (Throwable e) {
        }
        return task;
    }

    @Override
    public int compareTo(GenTaskBySqlBuilder o) {
        int result = this.getAllInOneName().compareTo(o.getAllInOneName());
        if (result != 0) {
            return result;
        }

        result = this.getTable_name().compareTo(o.getTable_name());
        if (result != 0) {
            return result;
        }

        return this.getMethod_name().compareTo(o.getMethod_name());

    }

    public String getDatabaseSetName() {
        return databaseSetName;
    }

    public void setDatabaseSetName(String databaseSetName) {
        this.databaseSetName = databaseSetName;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public String getAllInOneName() {
        return allInOneName;
    }

    public void setAllInOneName(String allInOneName) {
        this.allInOneName = allInOneName;
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

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
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

    public boolean isPagination() {
        return pagination;
    }

    public void setPagination(boolean pagination) {
        this.pagination = pagination;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public String getStr_update_time() {
        return str_update_time;
    }

    public void setStr_update_time(String str_update_time) {
        this.str_update_time = str_update_time;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public String getStr_approved() {
        return str_approved;
    }

    public void setStr_approved(String str_approved) {
        this.str_approved = str_approved;
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
}