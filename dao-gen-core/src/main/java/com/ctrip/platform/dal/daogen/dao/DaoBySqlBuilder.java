package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.utils.DatabaseSetUtils;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DaoBySqlBuilder extends BaseDao {
    private DalTableDao<GenTaskBySqlBuilder> client;
    private DalRowMapper<GenTaskBySqlBuilder> genTaskBySqlBuilderRowMapper = null;

    public DaoBySqlBuilder() throws SQLException {
        client = new DalTableDao<>(new DalDefaultJpaParser<>(GenTaskBySqlBuilder.class));
        genTaskBySqlBuilderRowMapper = new DalDefaultJpaMapper<>(GenTaskBySqlBuilder.class);
    }

    public List<GenTaskBySqlBuilder> getAllTasks() throws SQLException {
        DalHints hints = DalHints.createIfAbsent(null);
        SelectSqlBuilder builder = new SelectSqlBuilder().selectAll();
        List<GenTaskBySqlBuilder> list = client.query(builder, hints);
        processList(list);
        return list;
    }

    private void processList(List<GenTaskBySqlBuilder> list) throws SQLException {
        if (list == null || list.isEmpty())
            return;

        for (GenTaskBySqlBuilder entity : list) {
            processGenTaskBySqlBuilder(entity);
        }
    }

    private void processGenTaskBySqlBuilder(GenTaskBySqlBuilder entity) throws SQLException {
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
        GenTaskBySqlBuilder entity = client.queryByPk(id, hints);
        if (entity == null)
            return 0;
        return entity.getVersion();
    }

    public List<GenTaskBySqlBuilder> getTasksByProjectId(int projectId) throws SQLException {
        FreeSelectSqlBuilder<List<GenTaskBySqlBuilder>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT id, project_id,db_name, table_name,class_name,method_name,sql_style,crud_type,fields,where_condition,sql_content,`generated`,version,update_user_no,update_time,comment,scalarType,pagination,orderby,approved,approveMsg,hints,length ");
        sb.append("FROM task_auto WHERE project_id=? order by id ");
        builder.setTemplate(sb.toString());
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "project_id", Types.INTEGER, projectId);
        builder.mapWith(genTaskBySqlBuilderRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        List<GenTaskBySqlBuilder> list = queryDao.query(builder, parameters, hints);
        processList(list);
        return list;
    }

    public GenTaskBySqlBuilder getTasksByTaskId(int id) throws SQLException {
        DalHints hints = DalHints.createIfAbsent(null);
        return client.queryByPk(id, hints);
    }

    public List<GenTaskBySqlBuilder> updateAndGetAllTasks(int projectId) throws SQLException {
        List<GenTaskBySqlBuilder> result = new ArrayList<>();
        List<GenTaskBySqlBuilder> list = getTasksByProjectId(projectId);
        if (list == null || list.isEmpty())
            return result;

        for (GenTaskBySqlBuilder entity : list) {
            entity.setGenerated(true);
            result.add(entity);
        }

        return result;
    }

    public List<GenTaskBySqlBuilder> updateAndGetTasks(int projectId) throws SQLException {
        FreeSelectSqlBuilder<List<GenTaskBySqlBuilder>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT  id, project_id, db_name,table_name,class_name,method_name,sql_style,crud_type,fields,where_condition,sql_content,`generated`,version,update_user_no,update_time,comment,scalarType,pagination,orderby,approved,approveMsg,hints,length ");
        sb.append("FROM task_auto WHERE project_id=? AND `generated`=FALSE");
        builder.setTemplate(sb.toString());
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "project_id", Types.INTEGER, projectId);
        builder.mapWith(genTaskBySqlBuilderRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        List<GenTaskBySqlBuilder> list = queryDao.query(builder, parameters, hints);
        List<GenTaskBySqlBuilder> result = new ArrayList<>();
        if (list == null || list.size() == 0)
            return result;
        processList(list);
        for (GenTaskBySqlBuilder entity : list) {
            entity.setGenerated(true);
            if (updateTask(entity) > 0) {
                result.add(entity);
            }
        }
        return result;
    }

    public int insertTask(GenTaskBySqlBuilder task) throws SQLException {
        if (null == task)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.insert(hints, task);
    }

    public int updateTask(GenTaskBySqlBuilder task) throws SQLException {
        {
            FreeSelectSqlBuilder<GenTaskBySqlBuilder> builder = new FreeSelectSqlBuilder<>(dbCategory);
            builder.setTemplate(
                    "SELECT 1 FROM task_auto WHERE id != ? AND project_id=? AND db_name=? AND table_name=? AND method_name=? LIMIT 1");
            StatementParameters parameters = new StatementParameters();
            int i = 1;
            parameters.set(i++, "id", Types.INTEGER, task.getId());
            parameters.set(i++, "project_id", Types.INTEGER, task.getProject_id());
            parameters.set(i++, "db_name", Types.VARCHAR, task.getDatabaseSetName());
            parameters.set(i++, "table_name", Types.VARCHAR, task.getTable_name());
            parameters.set(i++, "method_name", Types.VARCHAR, task.getMethod_name());
            builder.mapWith(genTaskBySqlBuilderRowMapper).requireFirst().nullable();
            DalHints hints = DalHints.createIfAbsent(null).allowPartial();
            GenTaskBySqlBuilder entity = queryDao.query(builder, parameters, hints);
            if (entity != null)
                return 0;
        }
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE task_auto SET project_id=?,db_name=?, table_name=?, class_name=?,method_name=?,");
        sb.append("sql_style=?,crud_type=?,fields=?,where_condition=?,sql_content=?,`generated`=?,");
        sb.append("version=version+1,update_user_no=?,update_time=?,comment=?,scalarType=?,");
        sb.append("pagination=?,orderby=?,approved=?,approveMsg=?,hints=?,length=? ");
        sb.append("WHERE id=? AND version = ?");
        builder.setTemplate(sb.toString());
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "project_id", Types.INTEGER, task.getProject_id());
        parameters.set(i++, "db_name", Types.VARCHAR, task.getDatabaseSetName());
        parameters.set(i++, "table_name", Types.VARCHAR, task.getTable_name());
        parameters.set(i++, "class_name", Types.VARCHAR, task.getClass_name());
        parameters.set(i++, "method_name", Types.VARCHAR, task.getMethod_name());
        parameters.set(i++, "sql_style", Types.VARCHAR, task.getSql_style());
        parameters.set(i++, "crud_type", Types.VARCHAR, task.getCrud_type());
        parameters.set(i++, "fields", Types.LONGVARCHAR, task.getFields());
        parameters.set(i++, "where_condition", Types.LONGVARCHAR, task.getCondition());
        parameters.set(i++, "sql_content", Types.LONGVARCHAR, task.getSql_content());
        parameters.set(i++, "generated", Types.BIT, task.getGenerated());
        parameters.set(i++, "update_user_no", Types.VARCHAR, task.getUpdate_user_no());
        parameters.set(i++, "update_time", Types.TIMESTAMP, task.getUpdate_time());
        parameters.set(i++, "comment", Types.LONGVARCHAR, task.getComment());
        parameters.set(i++, "scalarType", Types.VARCHAR, task.getScalarType());
        parameters.set(i++, "pagination", Types.BIT, task.getPagination());
        parameters.set(i++, "orderby", Types.VARCHAR, task.getOrderby());
        parameters.set(i++, "approved", Types.INTEGER, task.getApproved());
        parameters.set(i++, "approveMsg", Types.LONGVARCHAR, task.getApproveMsg());
        parameters.set(i++, "hints", Types.VARCHAR, task.getHints());
        parameters.set(i++, "length", Types.TINYINT, task.getLength());
        parameters.set(i++, "id", Types.INTEGER, task.getId());
        parameters.set(i++, "version", Types.INTEGER, task.getVersion());

        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

    public int updateTask(int id, int approved, String approveMsg) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("UPDATE task_auto SET approved=?, approveMsg=? WHERE id=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "approved", Types.INTEGER, approved);
        parameters.set(i++, "approveMsg", Types.VARCHAR, approveMsg);
        parameters.set(i++, "id", Types.INTEGER, id);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

    public int deleteTask(GenTaskBySqlBuilder task) throws SQLException {
        if (null == task)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.delete(hints, task);
    }

    public int deleteByProjectId(int projectId) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("DELETE FROM task_auto WHERE project_id=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "project_id", Types.INTEGER, projectId);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

}
