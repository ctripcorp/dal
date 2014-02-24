
package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.pojo.GenTaskByFreeSql;

public class DaoByFreeSql {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<GenTaskByFreeSql> getAllTasks() {

		return this.jdbcTemplate
				.query("select id, project_id,server_id,  db_name,class_name,method_name,crud_type,sql_content,parameters from task_sql",

				new RowMapper<GenTaskByFreeSql>() {
					public GenTaskByFreeSql mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return GenTaskByFreeSql.visitRow(rs);
					}
				});
	}

	public List<GenTaskByFreeSql> getTasksByProjectId(int iD) {

		return this.jdbcTemplate
				.query("select id, project_id,server_id,  db_name,class_name,method_name,crud_type,sql_content,parameters from task_sql where project_id=?",
						new Object[] { iD }, new RowMapper<GenTaskByFreeSql>() {
							public GenTaskByFreeSql mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return GenTaskByFreeSql.visitRow(rs);
							}
						});
	}

	public int insertTask(GenTaskByFreeSql task) {

		return this.jdbcTemplate
				.update("insert into task_sql (project_id,server_id,  db_name,class_name,method_name,crud_type,sql_content,parameters) values (?,?,?,?,?,?,?,?)",
						task.getProject_id(), task.getServer_id(),task.getDb_name(),
						task.getClass_name(), task.getMethod_name(),
						task.getCrud_type(), task.getSql_content(),task.getParameters());

	}

	public int updateTask(GenTaskByFreeSql task) {

		return this.jdbcTemplate
				.update("update task_sql set project_id=?,server_id=?,  db_name=?,class_name=?,method_name=?,crud_type=?,sql_content=?,parameters=? where id=?",
						task.getProject_id(), task.getServer_id(),task.getDb_name(),
						task.getClass_name(), task.getMethod_name(),
						task.getCrud_type(), task.getSql_content(),task.getParameters(),
						task.getId());

	}

	public int deleteTask(GenTaskByFreeSql task) {
		return this.jdbcTemplate.update("delete from task_sql where id=?",
				task.getId());
	}


}