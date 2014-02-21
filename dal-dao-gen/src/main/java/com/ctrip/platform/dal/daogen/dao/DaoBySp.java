package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.pojo.GenTaskBySP;

public class DaoBySp {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<GenTaskBySP> getAllTasks() {

		return this.jdbcTemplate
				.query("select id, project_id,server_id,  db_name,class_name,sp_schema,sp_name,sql_style,crud_type,sp_content from task_sp",

				new RowMapper<GenTaskBySP>() {
					public GenTaskBySP mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return GenTaskBySP.visitRow(rs);
					}
				});
	}

	public List<GenTaskBySP> getTasksByProjectId(int iD) {

		return this.jdbcTemplate
				.query("select id, project_id, server_id, db_name,class_name,sp_schema,sp_name,sql_style,crud_type,sp_content from task_sp where project_id=?",
						new Object[] { iD }, new RowMapper<GenTaskBySP>() {
							public GenTaskBySP mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return GenTaskBySP.visitRow(rs);
							}
						});
	}

	public int insertTask(GenTaskBySP task) {

		return this.jdbcTemplate
				.update("insert into task_sp ( project_id,server_id,  db_name,class_name,sp_schema,sp_name,sql_style,crud_type,sp_content) values (?,?,?,?,?,?,?,?,?)",
						task.getProject_id(),task.getServer_id(), task.getDb_name(),
						task.getClass_name(), task.getSp_schema(),
						task.getSp_name(), task.getSql_style(),
						task.getCrud_type(), task.getSp_content());

	}

	public int updateTask(GenTaskBySP task) {

		return this.jdbcTemplate
				.update("update task_sp set project_id=?,server_id=?,  db_name=?, class_name=?,sp_schema=?,sp_name=?,sql_style=?,crud_type=?,sp_content=? where id=?",
					
						task.getProject_id(),task.getServer_id(), task.getDb_name(),
						task.getClass_name(), task.getSp_schema(),
						task.getSp_name(), task.getSql_style(),
						task.getCrud_type(), task.getSp_content(), task.getId());

	}

	public int deleteTask(GenTaskBySP task) {

		return this.jdbcTemplate.update("delete from task_sp where id=?",
				task.getId());
	}

}