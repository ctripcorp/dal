package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.pojo.SpTask;

public class SPTaskDAO {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<SpTask> getAllTasks() {

		return this.jdbcTemplate
				.query("select id, project_id, db_name,class_name,sp_schema,sp_name,sql_style,crud_type,sp_content from task_sp",

				new RowMapper<SpTask>() {
					public SpTask mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						SpTask task = new SpTask();
						task.setId(rs.getInt(1));
						task.setProject_id(rs.getInt(2));
						task.setDb_name(rs.getString(3));
						task.setClass_name(rs.getString(4));
						task.setSp_schema(rs.getString(5));
						task.setSp_name(rs.getString(6));
						task.setSql_style(rs.getString(7));
						task.setCrud_type(rs.getString(8));
						task.setSp_content(rs.getString(9));
						return task;
					}
				});
	}

	public List<SpTask> getTasksByProjectId(int iD) {

		return this.jdbcTemplate
				.query("select id, project_id, db_name,class_name,sp_schema,sp_name,sql_style,crud_type,sp_content from task_sp where project_id=?",
						new Object[] { iD }, new RowMapper<SpTask>() {
							public SpTask mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								SpTask task = new SpTask();
								task.setId(rs.getInt(1));
								task.setProject_id(rs.getInt(2));
								task.setDb_name(rs.getString(3));
								task.setClass_name(rs.getString(4));
								task.setSp_schema(rs.getString(5));
								task.setSp_name(rs.getString(6));
								task.setSql_style(rs.getString(7));
								task.setCrud_type(rs.getString(8));
								task.setSp_content(rs.getString(9));
								return task;
							}
						});
	}

	public int insertTask(SpTask task) {

		return this.jdbcTemplate
				.update("insert into task_sp ( project_id, db_name,class_name,sp_schema,sp_name,sql_style,crud_type,sp_content) values (?,?,?,?,?,?,?,?)",
						task.getProject_id(), task.getDb_name(),
						task.getClass_name(), task.getSp_schema(),
						task.getSp_name(), task.getSql_style(),
						task.getCrud_type(), task.getSp_content());

	}

	public int updateTask(SpTask task) {

		return this.jdbcTemplate
				.update("update task_sp set project_id=?, db_name=?, class_name=?,sp_schema=?,sp_name=?,sql_style=?,crud_type=?,sp_content=? where id=?",
					
						task.getProject_id(), task.getDb_name(),
						task.getClass_name(), task.getSp_schema(),
						task.getSp_name(), task.getSql_style(),
						task.getCrud_type(), task.getSp_content(), task.getId());

	}

	public int deleteTask(SpTask task) {

		return this.jdbcTemplate.update("delete from task_sp where id=?",
				task.getId());
	}

}