package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DaoByFreeSql {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GenTaskByFreeSql> getAllTasks() {
        return this.jdbcTemplate.query("SELECT id, project_id, db_name, class_name,pojo_name,method_name,crud_type,sql_content,"
                        + "parameters,`generated`,version,update_user_no,update_time,comment,scalarType,pojoType,"
                        + "pagination,sql_style,approved,approveMsg,hints FROM task_sql",
                new RowMapper<GenTaskByFreeSql>() {
                    public GenTaskByFreeSql mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return GenTaskByFreeSql.visitRow(rs);
                    }
                });
    }

    public int getVersionById(int id) {
        try {
            return this.jdbcTemplate.queryForObject("SELECT version FROM task_sql WHERE id =?",
                    new Object[]{id}, new RowMapper<Integer>() {
                        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getInt(1);
                        }
                    });
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public List<GenTaskByFreeSql> getTasksByProjectId(int iD) {
        return this.jdbcTemplate.query("SELECT id, project_id, db_name, class_name,pojo_name,method_name,crud_type,"
                        + "sql_content,parameters,`generated`,version,update_user_no,update_time,comment,"
                        + "scalarType,pojoType,pagination,sql_style,approved,approveMsg,hints"
                        + " FROM task_sql WHERE project_id=?",
                new Object[]{iD}, new RowMapper<GenTaskByFreeSql>() {
                    public GenTaskByFreeSql mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return GenTaskByFreeSql.visitRow(rs);
                    }
                });
    }

    public GenTaskByFreeSql getTasksByTaskId(int taskId) {
        List<GenTaskByFreeSql> list = this.jdbcTemplate.query("SELECT id, project_id, db_name, class_name,pojo_name,method_name,crud_type,"
                        + "sql_content,parameters,`generated`,version,update_user_no,update_time,comment,"
                        + "scalarType,pojoType,pagination,sql_style,approved,approveMsg,hints"
                        + " FROM task_sql WHERE id=?",
                new Object[]{taskId}, new RowMapper<GenTaskByFreeSql>() {
                    public GenTaskByFreeSql mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return GenTaskByFreeSql.visitRow(rs);
                    }
                });
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public List<GenTaskByFreeSql> updateAndGetAllTasks(int projectId) {
        final List<GenTaskByFreeSql> tasks = new ArrayList<>();
        this.jdbcTemplate.query("SELECT id, project_id,db_name,class_name,pojo_name,method_name,crud_type,"
                        + "sql_content,parameters,`generated`,version,update_user_no,update_time,comment,"
                        + "scalarType,pojoType,pagination,sql_style,approved,approveMsg,hints"
                        + " FROM task_sql WHERE project_id=?",
                new Object[]{projectId}, new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        GenTaskByFreeSql task = GenTaskByFreeSql.visitRow(rs);
                        task.setGenerated(true);
                        if (updateTask(task) > 0) {
                            tasks.add(task);
                        }
                    }
                });
        return tasks;
    }

    public List<GenTaskByFreeSql> updateAndGetTasks(int projectId) {
        final List<GenTaskByFreeSql> tasks = new ArrayList<>();
        this.jdbcTemplate.query("SELECT id, project_id,db_name,class_name,pojo_name,method_name,crud_type,"
                        + " sql_content,parameters,`generated`,version,update_user_no,update_time,"
                        + " comment,scalarType,pojoType,pagination,sql_style,approved,approveMsg,hints"
                        + " FROM task_sql "
                        + " WHERE project_id=? AND `generated`=FALSE",
                new Object[]{projectId}, new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        GenTaskByFreeSql task = GenTaskByFreeSql.visitRow(rs);
                        task.setGenerated(true);
                        if (updateTask(task) > 0) {
                            tasks.add(task);
                        }
                    }
                });
        return tasks;
    }

    public int insertTask(GenTaskByFreeSql task) {
        return this.jdbcTemplate.update("INSERT INTO task_sql (project_id,db_name,class_name,pojo_name,method_name,crud_type,sql_content,parameters,`generated`,version,update_user_no,update_time,comment,scalarType,pojoType,pagination,sql_style,approved,approveMsg,hints)"
                        + " SELECT * FROM (SELECT ? AS p1,? AS p2,? AS p3,? AS p4,? AS p5,? AS p6,? AS p7,? AS p8,? AS p9,? AS p10,? AS p11,? AS p12,? AS p13,? AS p14,? AS p15,? AS p16,? AS p17,? AS p18,? AS p19,? AS p20) tmp WHERE NOT exists "
                        + "(SELECT 1 FROM task_sql WHERE project_id=? AND db_name=? AND class_name=? AND method_name=? LIMIT 1)",
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
        final List<Integer> counts = new ArrayList<>();
        this.jdbcTemplate.query("SELECT 1 FROM task_sql WHERE id != ? AND project_id=? AND db_name=? AND class_name=? AND method_name=? LIMIT 1",
                new Object[]{task.getId(), task.getProject_id(), task.getDatabaseSetName(), task.getClass_name(), task.getMethod_name()},
                new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        counts.add(1);
                    }
                });

        if (counts.size() > 0)
            return -1;

        return this.jdbcTemplate.update("UPDATE task_sql SET project_id=?, db_name=?,class_name=?,pojo_name=?,"
                        + "method_name=?,crud_type=?,sql_content=?,parameters=?,`generated`=?,"
                        + "version=version+1,update_user_no=?,update_time=?,comment=?,"
                        + "scalarType=?,pojoType=?,pagination=?,sql_style=?,approved=?,approveMsg=?,hints=?"
                        + " WHERE id=? AND version=?", task.getProject_id(),
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
            return this.jdbcTemplate.update("UPDATE task_sql SET approved=?, approveMsg=? WHERE id=?", approved, approveMsg, taskId);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public int deleteTask(GenTaskByFreeSql task) {
        return this.jdbcTemplate.update("DELETE FROM task_sql WHERE id=?", task.getId());
    }

    public int deleteByProjectId(int id) {
        return this.jdbcTemplate.update("DELETE FROM task_sql WHERE project_id=?", id);
    }

    public int deleteByServerId(int id) {
        return this.jdbcTemplate.update("DELETE FROM task_sql WHERE server_id=?", id);
    }

}