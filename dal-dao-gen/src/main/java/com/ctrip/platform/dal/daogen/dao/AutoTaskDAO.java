package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.pojo.AutoTask;

public class AutoTaskDAO {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<AutoTask> getAllTasks() {

		return this.jdbcTemplate
				.query("select id, project_id, db_name, table_name,class_name,method_name,sql_style,sql_type,crud_type,fields,condition,sql_content from task_auto",
						new RowMapper<AutoTask>() {
							public AutoTask mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return AutoTask.visitRow(rs);
							}
						});

	}

	public List<AutoTask> getTasksByProjectId(int iD) {

		return this.jdbcTemplate
				.query("select id, project_id, db_name, table_name,class_name,method_name,sql_style,sql_type,crud_type,fields,where_condition,sql_content from task_auto where project_id=?",
						new Object[] { iD }, new RowMapper<AutoTask>() {
							public AutoTask mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return AutoTask.visitRow(rs);
							}
						});
	}

	public int insertTask(AutoTask task) {

		return this.jdbcTemplate
				.update("insert into task_auto ( project_id, db_name, table_name,class_name,method_name,sql_style,sql_type,crud_type,fields,where_condition,sql_content) values (?,?,?,?,?,?,?,?,?,?,?)",
						task.getProject_id(), task.getDb_name(),
						task.getTable_name(), task.getClass_name(),
						task.getMethod_name(), task.getSql_style(),
						task.getSql_type(), task.getCrud_type(),
						task.getFields(), task.getCondition(),
						task.getSql_content());

	}

	public int updateTask(AutoTask task) {

		return this.jdbcTemplate
				.update("update task_auto set  project_id=?, db_name=?, table_name=?,class_name=?,method_name=?,sql_style=?,sql_type=?,crud_type=?,fields=?,where_condition=?,sql_content=? where id=?",
						task.getProject_id(), task.getDb_name(),
						task.getTable_name(), task.getClass_name(),
						task.getMethod_name(), task.getSql_style(),
						task.getSql_type(), task.getCrud_type(),
						task.getFields(), task.getCondition(),
						task.getSql_content(), task.getId());

	}

	public int deleteTask(AutoTask task) {

		return this.jdbcTemplate.update("delete from task_auto where id=?",
				task.getId());
	}

}
