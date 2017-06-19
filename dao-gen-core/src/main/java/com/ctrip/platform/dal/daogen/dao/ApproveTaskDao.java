package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.ApproveTask;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ApproveTaskDao {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<ApproveTask> getAllApproveTaskByApproverId(Integer approverId) {
        return jdbcTemplate.query(
                "SELECT id, task_id, task_type, create_time, create_user_id, approve_user_id FROM approve_task WHERE approve_user_id=?",
                new Object[] {approverId}, new RowMapper<ApproveTask>() {
                    public ApproveTask mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ApproveTask.visitRow(rs);
                    }
                });
    }

    public int insertApproveTask(ApproveTask task) {
        return jdbcTemplate.update(
                "INSERT INTO approve_task (task_id, task_type, create_time, create_user_id, approve_user_id) VALUE(?,?,?,?,?)",
                task.getTask_id(), task.getTask_type(), task.getCreate_time(), task.getCreate_user_id(),
                task.getApprove_user_id());
    }

    public int deleteApproveTaskById(Integer id) {
        return jdbcTemplate.update("DELETE FROM approve_task WHERE id=?", id);
    }

    public int deleteApproveTaskByTaskIdAndType(Integer id, String taskType) {
        return jdbcTemplate.update("DELETE FROM approve_task WHERE task_id=? AND task_type=?", id, taskType);
    }

}
