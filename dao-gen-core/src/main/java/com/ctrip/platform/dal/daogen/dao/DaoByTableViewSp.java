package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.utils.DatabaseSetUtils;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DaoByTableViewSp extends BaseDao {
    private DalTableDao<GenTaskByTableViewSp> client;
    private DalRowMapper<GenTaskByTableViewSp> genTaskByTableViewSpRowMapper = null;

    public DaoByTableViewSp() throws SQLException {
        client = new DalTableDao<>(new DalDefaultJpaParser<>(GenTaskByTableViewSp.class));
        genTaskByTableViewSpRowMapper = new DalDefaultJpaMapper<>(GenTaskByTableViewSp.class);
    }

    private void processList(List<GenTaskByTableViewSp> list) throws SQLException {
        if (list == null || list.size() == 0)
            return;

        for (GenTaskByTableViewSp entity : list) {
            processGenTaskByTableViewSp(entity);
        }
    }

    private void processGenTaskByTableViewSp(GenTaskByTableViewSp entity) throws SQLException {
        if (entity.getApproved() != null) {
            if (entity.getApproved() == 1) {
                entity.setStr_approved("未审批");
            } else if (entity.getApproved() == 2) {
                entity.setStr_approved("通过");
            } else if (entity.getApproved() == 3) {
                entity.setStr_approved("未通过");
            } else {
                entity.setStr_approved("通过");
            }
        }

        if (entity.getUpdate_time() != null) {
            Date date = new Date(entity.getUpdate_time().getTime());
            entity.setStr_update_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        }

        entity.setAllInOneName(DatabaseSetUtils.getAllInOneName(entity.getDatabaseSetName()));
    }

    public int getVersionById(int id) throws SQLException {
        DalHints hints = DalHints.createIfAbsent(null);
        GenTaskByTableViewSp entity = client.queryByPk(id, hints);
        if (entity == null)
            return 0;
        return entity.getVersion();
    }

    /**
     * 根据项目主键查询所有任务
     *
     * @param projectId
     * @return
     */
    public List<GenTaskByTableViewSp> getTasksByProjectId(int projectId) throws SQLException {
        FreeSelectSqlBuilder<List<GenTaskByTableViewSp>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT id, project_id,db_name,table_names,view_names,sp_names,prefix,suffix, cud_by_sp,pagination,`generated`,version,update_user_no,update_time,comment,sql_style,api_list,approved,approveMsg,length ");
        sb.append("FROM task_table WHERE project_id=? order by id");
        builder.setTemplate(sb.toString());
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "project_id", Types.INTEGER, projectId);
        builder.mapWith(genTaskByTableViewSpRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        List<GenTaskByTableViewSp> list = queryDao.query(builder, parameters, hints);
        processList(list);
        return list;
    }

    /**
     * 根据task主键查询任务
     *
     * @param id
     * @return
     */
    public GenTaskByTableViewSp getTasksByTaskId(int id) throws SQLException {
        DalHints hints = DalHints.createIfAbsent(null);
        return client.queryByPk(id, hints);
    }

    public List<GenTaskByTableViewSp> updateAndGetAllTasks(int projectId) throws SQLException {
        List<GenTaskByTableViewSp> result = new ArrayList<>();
        List<GenTaskByTableViewSp> list = getTasksByProjectId(projectId);
        if (list == null || list.size() == 0)
            return result;

        for (GenTaskByTableViewSp entity : list) {
            entity.setGenerated(true);
            if (updateTask(entity) > 0) {
                result.add(entity);
            }
        }

        return result;
    }

    public List<GenTaskByTableViewSp> updateAndGetTasks(int projectId) throws SQLException {
        FreeSelectSqlBuilder<List<GenTaskByTableViewSp>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT id, project_id,db_name,table_names,view_names,sp_names,prefix,suffix,cud_by_sp,pagination,`generated`,version,update_user_no,update_time,comment,sql_style,api_list,approved,approveMsg,length ");
        sb.append("FROM task_table WHERE project_id=? AND `generated`=FALSE");
        builder.setTemplate(sb.toString());
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "project_id", Types.INTEGER, projectId);
        builder.mapWith(genTaskByTableViewSpRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        List<GenTaskByTableViewSp> list = queryDao.query(builder, parameters, hints);
        List<GenTaskByTableViewSp> result = new ArrayList<>();
        if (list == null || list.size() == 0)
            return result;
        processList(list);
        for (GenTaskByTableViewSp entity : list) {
            entity.setGenerated(true);
            if (updateTask(entity) > 0) {
                result.add(entity);
            }
        }
        return result;
    }

    public int insertTask(GenTaskByTableViewSp task) throws SQLException {
        if (null == task)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.insert(hints, task);
    }

    public int updateTask(GenTaskByTableViewSp task) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE task_table SET project_id=?,db_name=?,table_names=?,view_names=?,sp_names=?,");
        sb.append("prefix=?,suffix=?,cud_by_sp=?,pagination=?,`generated`=?,version=version+1,");
        sb.append("update_user_no=?,update_time=?,comment=?,sql_style=?,");
        sb.append("api_list=?,approved=?,approveMsg=?,length=? ");
        sb.append("WHERE id=? AND version=?");
        builder.setTemplate(sb.toString());
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "project_id", Types.INTEGER, task.getProject_id());
        parameters.set(i++, "db_name", Types.VARCHAR, task.getDatabaseSetName());
        parameters.set(i++, "table_names", Types.LONGVARCHAR, task.getTable_names());
        parameters.set(i++, "view_names", Types.LONGVARCHAR, task.getView_names());
        parameters.set(i++, "sp_names", Types.LONGVARCHAR, task.getSp_names());
        parameters.set(i++, "prefix", Types.VARCHAR, task.getPrefix());
        parameters.set(i++, "suffix", Types.VARCHAR, task.getSuffix());
        parameters.set(i++, "cud_by_sp", Types.BIT, task.getCud_by_sp());
        parameters.set(i++, "pagination", Types.BIT, task.getPagination());
        parameters.set(i++, "generated", Types.BIT, task.getGenerated());
        parameters.set(i++, "update_user_no", Types.VARCHAR, task.getUpdate_user_no());
        parameters.set(i++, "update_time", Types.TIMESTAMP, task.getUpdate_time());
        parameters.set(i++, "comment", Types.LONGVARCHAR, task.getComment());
        parameters.set(i++, "sql_style", Types.VARCHAR, task.getSql_style());
        parameters.set(i++, "api_list", Types.LONGVARCHAR, task.getApi_list());
        parameters.set(i++, "approved", Types.INTEGER, task.getApproved());
        parameters.set(i++, "approveMsg", Types.LONGVARCHAR, task.getApproveMsg());
        parameters.set(i++, "length", Types.TINYINT, task.getLength());
        parameters.set(i++, "id", Types.INTEGER, task.getId());
        parameters.set(i++, "version", Types.INTEGER, task.getVersion());

        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

    public int updateTask(int id, int approved, String approveMsg) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("UPDATE task_table SET approved=?, approveMsg=? WHERE id=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "approved", Types.INTEGER, approved);
        parameters.set(i++, "approveMsg", Types.VARCHAR, approveMsg);
        parameters.set(i++, "id", Types.INTEGER, id);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

    public int deleteTask(GenTaskByTableViewSp task) throws SQLException {
        if (null == task)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.delete(hints, task);
    }

    public int deleteByProjectId(int id) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("DELETE FROM task_table WHERE project_id=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "project_id", Types.INTEGER, id);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

}
