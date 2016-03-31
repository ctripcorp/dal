package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.ApproveTask;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ApproveTaskDao {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<ApproveTask> getAllApproveTaskByApproverId(Integer approverId) {
		return this.jdbcTemplate.query(
				"SELECT id, task_id, task_type, create_time, create_user_id, approve_user_id "
						+ " FROM approve_task where approve_user_id=?",
				new Object[] { approverId }, new RowMapper<ApproveTask>() {
					public ApproveTask mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return ApproveTask.visitRow(rs);
					}
				});
	}

	public int insertApproveTask(ApproveTask task) {
		return this.jdbcTemplate.update(
				"insert into approve_task (" + "task_id, " + "task_type, "
						+ "create_time, " + "create_user_id, "
						+ "approve_user_id) " + "value(?,?,?,?,?)",
				task.getTask_id(), task.getTask_type(), task.getCreate_time(),
				task.getCreate_user_id(), task.getApprove_user_id());
	}

	public int deleteApproveTaskById(Integer id) {
		try {
			return this.jdbcTemplate.update(
					"delete from approve_task where id=?", id);
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	public int deleteApproveTaskByTaskIdAndType(Integer id, String taskType) {
		try {
			return this.jdbcTemplate.update(
					"delete from approve_task where task_id=? and task_type=?",
					id, taskType);
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
}
