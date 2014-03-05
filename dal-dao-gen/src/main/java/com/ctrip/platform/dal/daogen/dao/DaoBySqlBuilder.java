package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;

public class DaoBySqlBuilder {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<GenTaskBySqlBuilder> getAllTasks() {

		return this.jdbcTemplate
				.query("select id, project_id,server_id, db_name, table_name,class_name,method_name,sql_style,crud_type,fields,condition,sql_content from task_auto",
						new RowMapper<GenTaskBySqlBuilder>() {
							public GenTaskBySqlBuilder mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								return GenTaskBySqlBuilder.visitRow(rs);
							}
						});

	}

	public List<GenTaskBySqlBuilder> getTasksByProjectId(int iD) {

		return this.jdbcTemplate
				.query("select id, project_id,server_id,  db_name, table_name,class_name,method_name,sql_style,crud_type,fields,where_condition,sql_content from task_auto where project_id=?",
						new Object[] { iD },
						new RowMapper<GenTaskBySqlBuilder>() {
							public GenTaskBySqlBuilder mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								return GenTaskBySqlBuilder.visitRow(rs);
							}
						});
	}

	public int insertTask(GenTaskBySqlBuilder task) {

		return this.jdbcTemplate
				.update("insert into task_auto ( project_id,server_id,  db_name, table_name,class_name,method_name,sql_style,crud_type,fields,where_condition,sql_content) values (?,?,?,?,?,?,?,?,?,?,?)",
						task.getProject_id(), task.getServer_id(),
						task.getDb_name(), task.getTable_name(),
						task.getClass_name(), task.getMethod_name(),
						task.getSql_style(), task.getCrud_type(),
						task.getFields(), task.getCondition(),
						task.getSql_content());

	}

	public int updateTask(GenTaskBySqlBuilder task) {

		return this.jdbcTemplate
				.update("update task_auto set  project_id=?,server_id=?,  db_name=?, table_name=?,class_name=?,method_name=?,sql_style=?,crud_type=?,fields=?,where_condition=?,sql_content=? where id=?",
						task.getProject_id(), task.getServer_id(),
						task.getDb_name(), task.getTable_name(),
						task.getClass_name(), task.getMethod_name(),
						task.getSql_style(), 
						task.getCrud_type(), task.getFields(),
						task.getCondition(), task.getSql_content(),
						task.getId());

	}

	public int deleteTask(GenTaskBySqlBuilder task) {

		return this.jdbcTemplate.update("delete from task_auto where id=?",
				task.getId());
	}

}
