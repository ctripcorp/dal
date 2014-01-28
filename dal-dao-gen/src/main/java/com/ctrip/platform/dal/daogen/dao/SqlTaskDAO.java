package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.pojo.SqlTask;

public class SqlTaskDAO {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<SqlTask> getAllTasks() {

		return this.jdbcTemplate
				.query("select id, project_id, db_name,class_name,method_name,crud_type,sql_content from task_sql",

				new RowMapper<SqlTask>() {
					public SqlTask mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						SqlTask task = new SqlTask();
						task.setId(rs.getInt(1));
						task.setProject_id(rs.getInt(2));
						task.setDb_name(rs.getString(3));
						task.setClass_name(rs.getString(4));
						task.setMethod_name(rs.getString(5));
						task.setCrud_type(rs.getString(6));
						task.setSql_content(rs.getString(7));
						return task;
					}
				});
	}

	public List<SqlTask> getTasksByProjectId(int iD) {

		return this.jdbcTemplate
				.query("select id, project_id, db_name,class_name,method_name,crud_type,sql_content from task_sql where project_id=?",
						new Object[] { iD }, new RowMapper<SqlTask>() {
							public SqlTask mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								SqlTask task = new SqlTask();
								task.setId(rs.getInt(1));
								task.setProject_id(rs.getInt(2));
								task.setDb_name(rs.getString(3));
								task.setClass_name(rs.getString(4));
								task.setMethod_name(rs.getString(5));
								task.setCrud_type(rs.getString(6));
								task.setSql_content(rs.getString(7));
								return task;
							}
						});
	}

	public int insertTask(SqlTask task) {

		return this.jdbcTemplate
				.update("insert into task_sql (project_id, db_name,class_name,method_name,crud_type,sql_content) values (?,?,?,?,?,?)",
						task.getProject_id(), task.getDb_name(),
						task.getClass_name(), task.getMethod_name(),
						task.getCrud_type(), task.getSql_content());

	}

	public int updateTask(SqlTask task) {

		return this.jdbcTemplate
				.update("update task_sql set project_id=?, db_name=?,class_name=?,method_name=?,crud_type=?,sql_content=? where id=?",
						task.getProject_id(), task.getDb_name(),
						task.getClass_name(), task.getMethod_name(),
						task.getCrud_type(), task.getSql_content(),
						task.getId());

	}

	public int deleteTask(SqlTask task) {
		return this.jdbcTemplate.update("delete from task_sql where id=?",
				task.getId());
	}

}