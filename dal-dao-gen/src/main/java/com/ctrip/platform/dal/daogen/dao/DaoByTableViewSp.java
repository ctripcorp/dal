package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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
					.query("select id, project_id, server_id, db_name,table_names,view_names,sp_names,prefix,suffix,cud_by_sp,pagination from task_table where project_id=?",
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

	public int insertTask(GenTaskByTableViewSp task) {
		try {
			return this.jdbcTemplate
					.update("insert into task_table ( project_id,server_id,  db_name,table_names,view_names,sp_names,prefix,suffix,cud_by_sp,pagination) values (?,?,?,?,?,?,?,?,?,?)",
							task.getProject_id(), task.getServer_id(),
							task.getDb_name(), task.getTable_names(),
							task.getView_names(), task.getSp_names(),
							task.getPrefix(), task.getSuffix(),
							task.isCud_by_sp(), task.isPagination());
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	public int updateTask(GenTaskByTableViewSp task) {
		try {
			return this.jdbcTemplate
					.update("update task_table set project_id=?,server_id=?,  db_name=?, table_names=?,view_names=?,sp_names=?,prefix=?,suffix=?,cud_by_sp=?,pagination=? where id=?",

					task.getProject_id(), task.getServer_id(),
							task.getDb_name(), task.getTable_names(),
							task.getView_names(), task.getSp_names(),
							task.getPrefix(), task.getSuffix(),
							task.isCud_by_sp(), task.isPagination(),
							task.getId());
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

}
