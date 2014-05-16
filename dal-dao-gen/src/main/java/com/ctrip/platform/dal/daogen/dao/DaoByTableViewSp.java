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

import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;

public class DaoByTableViewSp {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 * 根据项目主键查询所有任务
	 * 
	 * @param iD
	 * @return
	 */
	public List<GenTaskByTableViewSp> getTasksByProjectId(int iD) {
		try {
			return this.jdbcTemplate
					.query("select id, project_id, db_name,table_names,view_names,sp_names,prefix,suffix,cud_by_sp,pagination,generated,version,update_user_no,update_time,comment from task_table where project_id=?",
							new Object[] { iD },
							new RowMapper<GenTaskByTableViewSp>() {
								public GenTaskByTableViewSp mapRow(
										ResultSet rs, int rowNum)
										throws SQLException {
									return GenTaskByTableViewSp.visitRow(rs);
								}
							});
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public List<GenTaskByTableViewSp> updateAndGetAllTasks(int projectId) {

		final List<GenTaskByTableViewSp> tasks = new ArrayList<GenTaskByTableViewSp>();

		this.jdbcTemplate
				.query("select id, project_id, db_name,table_names,view_names,sp_names,prefix,suffix,cud_by_sp,pagination,generated,version,update_user_no,update_time,comment from task_table where project_id=?",
						new Object[] { projectId }, new RowCallbackHandler() {
							@Override
							public void processRow(ResultSet rs)
									throws SQLException {
								GenTaskByTableViewSp task = GenTaskByTableViewSp
										.visitRow(rs);

								task.setGenerated(true);
								if (updateTask(task) > 0) {
									tasks.add(task);
								}
							}
						});
		return tasks;
	}

	public List<GenTaskByTableViewSp> updateAndGetTasks(int projectId) {

		final List<GenTaskByTableViewSp> tasks = new ArrayList<GenTaskByTableViewSp>();

		this.jdbcTemplate
				.query("select id, project_id, db_name,table_names,view_names,sp_names,prefix,suffix,cud_by_sp,pagination,generated,version,update_user_no,update_time,comment from task_table where project_id=? and generated=false",
						new Object[] { projectId }, new RowCallbackHandler() {
							@Override
							public void processRow(ResultSet rs)
									throws SQLException {
								GenTaskByTableViewSp task = GenTaskByTableViewSp
										.visitRow(rs);

								task.setGenerated(true);
								if (updateTask(task) > 0) {
									tasks.add(task);
								}
							}
						});
		return tasks;
	}

	public int insertTask(GenTaskByTableViewSp task) {
		try {
			return this.jdbcTemplate
					.update("insert into task_table ( project_id,  db_name,table_names,view_names,sp_names,prefix,suffix,cud_by_sp,pagination,generated,version,update_user_no,update_time,comment) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
							task.getProject_id(), task.getDb_name(),
							task.getTable_names(), task.getView_names(),
							task.getSp_names(), task.getPrefix(),
							task.getSuffix(), task.isCud_by_sp(),
							task.isPagination(), task.isGenerated(),
							task.getVersion(),
							task.getUpdate_user_no(),
							task.getUpdate_time(),
							task.getComment());
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	public int getVersionById(int id) {
		try {
			return this.jdbcTemplate.queryForObject(
					"select version from task_table where id =?",
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

	public int updateTask(GenTaskByTableViewSp task) {
		try {
			return this.jdbcTemplate
					.update("update task_table set project_id=?,db_name=?,table_names=?,view_names=?,sp_names=?,prefix=?,suffix=?,cud_by_sp=?,pagination=?,generated=?,version=version+1,update_user_no=?,update_time=?,comment=? where id=? and version=?",

					task.getProject_id(), task.getDb_name(),
							task.getTable_names(), task.getView_names(),
							task.getSp_names(), task.getPrefix(),
							task.getSuffix(), task.isCud_by_sp(),
							task.isPagination(), task.isGenerated(),
							task.getUpdate_user_no(),
							task.getUpdate_time(),
							task.getComment(),
							task.getId(), task.getVersion());
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	public int deleteTask(GenTaskByTableViewSp task) {
		try {
			return this.jdbcTemplate.update(
					"delete from task_table where id=?", task.getId());
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	public int deleteByProjectId(int id) {
		return this.jdbcTemplate.update(
				"delete from task_table where project_id=?", id);
	}

	public int deleteByServerId(int id) {
		return this.jdbcTemplate.update(
				"delete from task_table where server_id=?", id);
	}

}
