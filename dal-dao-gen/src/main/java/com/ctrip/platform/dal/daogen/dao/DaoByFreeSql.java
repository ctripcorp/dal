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

import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;

public class DaoByFreeSql {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<GenTaskByFreeSql> getAllTasks() {
		return this.jdbcTemplate
				.query("select id, project_id, db_name, class_name,pojo_name,method_name,crud_type,sql_content,"
						+ "parameters,generated,version,update_user_no,update_time,comment,scalarType,pojoType,"
						+ "pagination,sql_style,approved,approveMsg,hints from task_sql",

				new RowMapper<GenTaskByFreeSql>() {
					public GenTaskByFreeSql mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return GenTaskByFreeSql.visitRow(rs);
					}
				});
	}

	public int getVersionById(int id) {
		try {
			return this.jdbcTemplate.queryForObject(
					"select version from task_sql where id =?",
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

	public List<GenTaskByFreeSql> getTasksByProjectId(int iD) {
		return this.jdbcTemplate
				.query("select id, project_id, db_name, class_name,pojo_name,method_name,crud_type,"
						+ "sql_content,parameters,generated,version,update_user_no,update_time,comment,"
						+ "scalarType,pojoType,pagination,sql_style,approved,approveMsg,hints"
						+ " from task_sql where project_id=?",
						new Object[] { iD }, new RowMapper<GenTaskByFreeSql>() {
							public GenTaskByFreeSql mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								return GenTaskByFreeSql.visitRow(rs);
							}
						});
	}

	public GenTaskByFreeSql getTasksByTaskId(int taskId) {
		List<GenTaskByFreeSql> list = this.jdbcTemplate
				.query("select id, project_id, db_name, class_name,pojo_name,method_name,crud_type,"
						+ "sql_content,parameters,generated,version,update_user_no,update_time,comment,"
						+ "scalarType,pojoType,pagination,sql_style,approved,approveMsg,hints"
						+ " from task_sql where id=?", new Object[] { taskId },
						new RowMapper<GenTaskByFreeSql>() {
							public GenTaskByFreeSql mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								return GenTaskByFreeSql.visitRow(rs);
							}
						});
		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	public List<GenTaskByFreeSql> updateAndGetAllTasks(int projectId) {
		final List<GenTaskByFreeSql> tasks = new ArrayList<GenTaskByFreeSql>();
		this.jdbcTemplate
				.query("select id, project_id,db_name,class_name,pojo_name,method_name,crud_type,"
						+ "sql_content,parameters,generated,version,update_user_no,update_time,comment,"
						+ "scalarType,pojoType,pagination,sql_style,approved,approveMsg,hints"
						+ " from task_sql where project_id=?",
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
				.query("select id, project_id,db_name,class_name,pojo_name,method_name,crud_type,"
						+ " sql_content,parameters,generated,version,update_user_no,update_time,"
						+ " comment,scalarType,pojoType,pagination,sql_style,approved,approveMsg,hints"
						+ " from task_sql "
						+ " where project_id=? and generated=false",
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
				.update("insert into task_sql (project_id,db_name,class_name,pojo_name,method_name,crud_type,sql_content,parameters,generated,version,update_user_no,update_time,comment,scalarType,pojoType,pagination,sql_style,approved,approveMsg,hints)"
						+ " select * from (select ? as p1,? as p2,? as p3,? as p4,? as p5,? as p6,? as p7,? as p8,? as p9,? as p10,? as p11,? as p12,? as p13,? as p14,? as p15,? as p16,? as p17,? as p18,? as p19,? as p20) tmp where not exists "
						+ "(select 1 from task_sql where project_id=? and db_name=? and class_name=? and method_name=? limit 1)",
						task.getProject_id(), task.getDatabaseSetName(),
						task.getClass_name(), task.getPojo_name(),
						task.getMethod_name(), task.getCrud_type(),
						task.getSql_content(), task.getParameters(),
						task.isGenerated(), task.getVersion(),
						task.getUpdate_user_no(), task.getUpdate_time(),
						task.getComment(), task.getScalarType(),
						task.getPojoType(), task.isPagination(),
						task.getSql_style(), task.getApproved(),
						task.getApproveMsg(), task.getHints(),
						task.getProject_id(), task.getDatabaseSetName(),
						task.getClass_name(), task.getMethod_name());
	}

	public int updateTask(GenTaskByFreeSql task) {
		final List<Integer> counts = new ArrayList<Integer>();
		this.jdbcTemplate
				.query("select 1 from task_sql where id != ? and project_id=? and db_name=? and class_name=? and method_name=? limit 1",
						new Object[] { task.getId(), task.getProject_id(),
								task.getDatabaseSetName(),
								task.getClass_name(), task.getMethod_name() },
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
				.update("update task_sql set project_id=?, db_name=?,class_name=?,pojo_name=?,"
						+ "method_name=?,crud_type=?,sql_content=?,parameters=?,generated=?,"
						+ "version=version+1,update_user_no=?,update_time=?,comment=?,"
						+ "scalarType=?,pojoType=?,pagination=?,sql_style=?,approved=?,approveMsg=?,hints=?"
						+ " where id=? and version=?", task.getProject_id(),
						task.getDatabaseSetName(), task.getClass_name(),
						task.getPojo_name(), task.getMethod_name(),
						task.getCrud_type(), task.getSql_content(),
						task.getParameters(), task.isGenerated(),
						task.getUpdate_user_no(), task.getUpdate_time(),
						task.getComment(), task.getScalarType(),
						task.getPojoType(), task.isPagination(),
						task.getSql_style(), task.getApproved(),
						task.getApproveMsg(), task.getHints(), task.getId(),
						task.getVersion());
	}

	public int updateTask(int taskId, int approved, String approveMsg) {
		try {
			return this.jdbcTemplate.update(
					"update task_sql set approved=?, approveMsg=? where id=?",
					approved, approveMsg, taskId);
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
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