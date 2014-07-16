package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;

public class DaoBySqlBuilder {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public int getVersionById(int id) {
		try {
			return this.jdbcTemplate.queryForObject(
					"select version from task_auto where id =?",
					new Object[] { id }, new RowMapper<Integer>() {
						public Integer mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return rs.getInt(1);
						}
					});
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	public List<GenTaskBySqlBuilder> getAllTasks() {

		return this.jdbcTemplate
				.query("select id, project_id, db_name,table_name,class_name,method_name,sql_style,crud_type,fields,where_condition,sql_content,generated,version,update_user_no,update_time,comment,scalarType from task_auto",
						new RowMapper<GenTaskBySqlBuilder>() {
							public GenTaskBySqlBuilder mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								return GenTaskBySqlBuilder.visitRow(rs);
							}
						});

	}

	public List<GenTaskBySqlBuilder> getTasksByProjectId(int iD) {

		return this.jdbcTemplate
				.query("select id, project_id,db_name, table_name,class_name,method_name,sql_style,crud_type,fields,where_condition,sql_content,generated,version,update_user_no,update_time,comment,scalarType from task_auto where project_id=?",
						new Object[] { iD },
						new RowMapper<GenTaskBySqlBuilder>() {
							public GenTaskBySqlBuilder mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								return GenTaskBySqlBuilder.visitRow(rs);
							}
						});
	}

	public List<GenTaskBySqlBuilder> updateAndGetAllTasks(int projectId) {

		final List<GenTaskBySqlBuilder> tasks = new ArrayList<GenTaskBySqlBuilder>();

		this.jdbcTemplate
				.query("select  id, project_id, db_name,table_name,class_name,method_name,sql_style,crud_type,fields,where_condition,sql_content,generated,version,update_user_no,update_time,comment,scalarType from task_auto where project_id=?",
						new Object[] { projectId }, new RowCallbackHandler() {
							@Override
							public void processRow(ResultSet rs)
									throws SQLException {
								GenTaskBySqlBuilder task = GenTaskBySqlBuilder
										.visitRow(rs);

								task.setGenerated(true);
								if (updateTask(task) > 0) {
									tasks.add(task);
								}
							}
						});
		return tasks;
	}

	public List<GenTaskBySqlBuilder> updateAndGetTasks(int projectId) {

		final List<GenTaskBySqlBuilder> tasks = new ArrayList<GenTaskBySqlBuilder>();

		this.jdbcTemplate
				.query("select  id, project_id, db_name,table_name,class_name,method_name,sql_style,crud_type,fields,where_condition,sql_content,generated,version,update_user_no,update_time,comment,scalarType from task_auto  where project_id=? and generated=false",
						new Object[] { projectId }, new RowCallbackHandler() {
							@Override
							public void processRow(ResultSet rs)
									throws SQLException {
								GenTaskBySqlBuilder task = GenTaskBySqlBuilder
										.visitRow(rs);

								task.setGenerated(true);
								if (updateTask(task) > 0) {
									tasks.add(task);
								}
							}
						});
		return tasks;
	}

	public int insertTask(GenTaskBySqlBuilder task) {

		return this.jdbcTemplate
				.update("insert into task_auto "
						+ "( project_id, db_name, table_name,class_name,method_name,sql_style,crud_type,fields,where_condition,sql_content,generated,version,update_user_no,update_time,comment,scalarType)"
						+ " select * from (select ? as p1,? as p2,? as p3,? as p4,? as p5,? as p6,? as p7,? as p8,? as p9,? as p10,? as p11,? as p12,? as p13,? as p14,? as p15,? as p16) tmp where not exists "
						+ "(select 1 from task_auto where project_id=? and db_name=? and table_name=? and method_name=? limit 1)",
						task.getProject_id(),
						task.getDatabaseSetName(), task.getTable_name(),
						task.getClass_name(), task.getMethod_name(),
						task.getSql_style(), task.getCrud_type(),
						task.getFields(), task.getCondition(),
						task.getSql_content(), task.isGenerated(),
						task.getVersion(), 
						task.getUpdate_user_no(),
						task.getUpdate_time(),
						task.getComment(),
						task.getScalarType(),
						task.getProject_id(),
						task.getDatabaseSetName(), 
						task.getTable_name(), 
						task.getMethod_name());

	}

	public int updateTask(GenTaskBySqlBuilder task) {

		final List<Integer> counts = new ArrayList<Integer>();
		this.jdbcTemplate
				.query("select 1 from task_auto where id != ? and project_id=? and db_name=? and table_name=? and method_name=? limit 1",
						new Object[] { task.getId(), task.getProject_id(),
								task.getDatabaseSetName(), 
								task.getTable_name(),
								task.getMethod_name() },
						new RowCallbackHandler() {
							@Override
							public void processRow(ResultSet rs)
									throws SQLException {
								counts.add(1);
							}
						});

		if (counts.size() > 0)
			return -1;

		return this.jdbcTemplate
				.update("update task_auto set  project_id=?,db_name=?, table_name=?, class_name=?,method_name=?,sql_style=?,crud_type=?,fields=?,where_condition=?,sql_content=?,generated=?,version=version+1,update_user_no=?,update_time=?,comment=?,scalarType=? where id=? and version = ?",
						task.getProject_id(),
						task.getDatabaseSetName(),
						task.getTable_name(),
						task.getClass_name(), task.getMethod_name(),
						task.getSql_style(), task.getCrud_type(),
						task.getFields(), task.getCondition(),
						task.getSql_content(), task.isGenerated(),
						task.getUpdate_user_no(),
						task.getUpdate_time(),
						task.getComment(),
						task.getScalarType(),
						task.getId(), 
						task.getVersion());

	}

	public int deleteTask(GenTaskBySqlBuilder task) {

		return this.jdbcTemplate.update("delete from task_auto where id=?",
				task.getId());
	}

	public int deleteByProjectId(int id) {
		return this.jdbcTemplate.update(
				"delete from task_auto where project_id=?", id);
	}

	public int deleteByServerId(int id) {
		return this.jdbcTemplate.update(
				"delete from task_auto where server_id=?", id);
	}

}