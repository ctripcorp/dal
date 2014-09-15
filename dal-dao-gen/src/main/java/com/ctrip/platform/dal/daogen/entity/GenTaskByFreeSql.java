

package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.ctrip.platform.dal.daogen.utils.DatabaseSetUtils;

public class GenTaskByFreeSql implements Comparable<GenTaskByFreeSql> {

	private int id;
	private int project_id;
	private String allInOneName;
	private String databaseSetName;
	private String class_name;
	private String pojo_name;
	private String method_name;
	//操作类型，可取值:select、update
	private String crud_type;
	private String sql_content;
	private String parameters;
	private boolean generated;
	private int version;
	private String update_user_no;
	private Timestamp update_time;
	private String comment;
	//可取值：Single、First、List，表示select返回的结果类型
	private String scalarType;
	//实体类型，取值：EntityType、SimpleType，分别表示实体类型、简单类型
	//若取值为SimpleType，则pojo_name的值为：简单类型
	private String pojoType;
	//是否增加分页方法，true：增加
	private boolean pagination;
	//csharp 或者 java，表示C#风格或者Java风格，@Name or ?
	private String sql_style;
	
	public static GenTaskByFreeSql visitRow(ResultSet rs) throws SQLException {
		GenTaskByFreeSql task = new GenTaskByFreeSql();
		task.setId(rs.getInt(1));
		task.setProject_id(rs.getInt(2));
		
		String databaseSet = rs.getString(3);
		
		task.setAllInOneName(DatabaseSetUtils.getAllInOneName(databaseSet));
		task.setDatabaseSetName(databaseSet);
		task.setClass_name(rs.getString(4));
		task.setPojo_name(rs.getString(5));
		task.setMethod_name(rs.getString(6));
		task.setCrud_type(rs.getString(7));
		task.setSql_content(rs.getString(8));
		task.setParameters(rs.getString(9));
		task.setGenerated(rs.getBoolean(10));
		task.setVersion(rs.getInt(11));
		task.setUpdate_user_no(rs.getString(12));
		task.setUpdate_time(rs.getTimestamp(13));
		task.setComment(rs.getString(14));
		task.setScalarType(rs.getString("scalarType"));
		task.setPojoType(rs.getString("pojoType"));
		task.setPagination(rs.getBoolean("pagination"));
		task.setSql_style(rs.getString("sql_style"));

		return task;
	}

	@Override
	public int compareTo(GenTaskByFreeSql o) {
		int result =  this.getAllInOneName().compareTo(o.getAllInOneName());
		if(result != 0){
			return result;
		}
		
		result = this.getClass_name().compareTo(o.getClass_name());
		if(result != 0){
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

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
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
	
	public void setAllInOneName(String allInOneName){
		this.allInOneName = allInOneName;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getPojo_name() {
		return pojo_name;
	}

	public void setPojo_name(String pojo_name) {
		this.pojo_name = pojo_name;
	}

	public String getMethod_name() {
		return method_name;
	}

	public void setMethod_name(String method_name) {
		this.method_name = method_name;
	}

	public String getCrud_type() {
		return crud_type;
	}

	public void setCrud_type(String crud_type) {
		this.crud_type = crud_type;
	}

	public String getSql_content() {
		return sql_content;
	}

	public void setSql_content(String sql_content) {
		this.sql_content = sql_content;
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

	public String getPojoType() {
		return pojoType;
	}

	public void setPojoType(String pojoType) {
		this.pojoType = pojoType;
	}

	public boolean isPagination() {
		return pagination;
	}

	public void setPagination(boolean pagination) {
		this.pagination = pagination;
	}

	public String getSql_style() {
		return sql_style;
	}

	public void setSql_style(String sql_style) {
		this.sql_style = sql_style;
	}

}
