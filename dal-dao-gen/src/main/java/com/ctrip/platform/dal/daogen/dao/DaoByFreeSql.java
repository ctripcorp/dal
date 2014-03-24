package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;

public class DaoByFreeSql {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<GenTaskByFreeSql> getAllTasks() {

		return this.jdbcTemplate
				.query("select id, project_id,server_id,  db_name,class_name,pojo_name,method_name,crud_type,sql_content,parameters,generated,version from task_sql",

				new RowMapper<GenTaskByFreeSql>() {
					public GenTaskByFreeSql mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return GenTaskByFreeSql.visitRow(rs);
					}
				});
	}

	public List<GenTaskByFreeSql> getTasksByProjectId(int iD) {

		return this.jdbcTemplate
				.query("select id, project_id,server_id,  db_name,class_name,pojo_name,method_name,crud_type,sql_content,parameters,generated,version from task_sql where project_id=?",
						new Object[] { iD }, new RowMapper<GenTaskByFreeSql>() {
							public GenTaskByFreeSql mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								return GenTaskByFreeSql.visitRow(rs);
							}
						});
	}

	public List<GenTaskByFreeSql> updateAndGetAllTasks(int projectId) {

		final List<GenTaskByFreeSql> tasks = new ArrayList<GenTaskByFreeSql>();

		this.jdbcTemplate
				.query("select id, project_id,server_id,  db_name,class_name,pojo_name,method_name,crud_type,sql_content,parameters,generated,version from task_sql where project_id=?",
						new Object[] { projectId }, new RowCallbackHandler() {
							@Override
							public void processRow(ResultSet rs)
									throws SQLException {
								GenTaskByFreeSql task = GenTaskByFreeSql
										.visitRow(rs);

								task.setGenerated(true);
								if (updateTask(task) > 0) {
									tasks.add(task);
								}
							}
						});
		return tasks;
	}

	public List<GenTaskByFreeSql> updateAndGetTasks(int projectId) {

		final List<GenTaskByFreeSql> tasks = new ArrayList<GenTaskByFreeSql>();

		this.jdbcTemplate
				.query("select id, project_id,server_id,  db_name,class_name,pojo_name,method_name,crud_type,sql_content,parameters,generated,version from task_sql where project_id=?  and generated=false",
						new Object[] { projectId }, new RowCallbackHandler() {
							@Override
							public void processRow(ResultSet rs)
									throws SQLException {
								GenTaskByFreeSql task = GenTaskByFreeSql
										.visitRow(rs);

								task.setGenerated(true);
								if (updateTask(task) > 0) {
									tasks.add(task);
								}
							}
						});
		return tasks;
	}

	public int insertTask(GenTaskByFreeSql task) {

		return this.jdbcTemplate
				.update("insert into task_sql (project_id,server_id,  db_name,class_name,pojo_name,method_name,crud_type,sql_content,parameters,generated,version)"
						+ " select * from (select ? as p1,? as p2,? as p3,? as p4,? as p5,? as p6,? as p7,? as p8,? as p9,? as p10,? as p11) tmp where not exists "
						+ "(select 1 from task_sql where project_id=? and db_name=? and class_name=? and method_name=? limit 1)",
						task.getProject_id(), task.getServer_id(),
						task.getDb_name(), task.getClass_name(),
						task.getPojo_name(), task.getMethod_name(),
						task.getCrud_type(), task.getSql_content(),
						task.getParameters(), task.isGenerated(),
						task.getVersion(), task.getProject_id(),
						task.getDb_name(), task.getClass_name(),
						task.getMethod_name());

	}

	public int updateTask(GenTaskByFreeSql task) {

		final List<Integer> counts = new ArrayList<Integer>();
		this.jdbcTemplate
				.query("select 1 from task_sql where id != ? and project_id=? and db_name=? and class_name=? and method_name=? limit 1",
						new Object[] { task.getId(), task.getProject_id(),
								task.getDb_name(), task.getClass_name(),
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
				.update("update task_sql set project_id=?,server_id=?,  db_name=?,class_name=?,pojo_name=?,method_name=?,crud_type=?,sql_content=?,parameters=?,generated=?,version=version+1 where id=? and version=?",
						task.getProject_id(), task.getServer_id(),
						task.getDb_name(), task.getClass_name(),
						task.getPojo_name(), task.getMethod_name(),
						task.getCrud_type(), task.getSql_content(),
						task.getParameters(), task.isGenerated(), task.getId(),
						task.getVersion());

	}

	public int deleteTask(GenTaskByFreeSql task) {
		return this.jdbcTemplate.update("delete from task_sql where id=?",
				task.getId());
	}

	public int deleteByProjectId(int id) {
		return this.jdbcTemplate.update(
				"delete from task_sql where project_id=?", id);
	}

	public int deleteByServerId(int id) {
		return this.jdbcTemplate.update(
				"delete from task_sql where server_id=?", id);
	}

}