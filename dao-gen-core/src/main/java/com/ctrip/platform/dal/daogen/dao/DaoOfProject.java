package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.dao.KeyHolder;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class DaoOfProject extends BaseDao {
    private DalTableDao<Project> client;
    private DalRowMapper<Project> projectRowMapper = null;

    public DaoOfProject() throws SQLException {
        client = new DalTableDao<>(new DalDefaultJpaParser<>(Project.class));
        projectRowMapper = new DalDefaultJpaMapper<>(Project.class);
    }

    public Project getProjectByID(int id) throws SQLException {
        DalHints hints = DalHints.createIfAbsent(null);
        Project project = client.queryByPk(id, hints);
        processProject(project);
        return project;
    }

    public List<Project> getProjectByGroupId(int groupId) throws SQLException {
        FreeSelectSqlBuilder<List<Project>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id, name, namespace,dal_group_id,dal_config_name,update_user_no,update_time FROM project WHERE dal_group_id = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "dal_group_id", Types.INTEGER, groupId);
        builder.mapWith(projectRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        List<Project> list = queryDao.query(builder, parameters, hints);
        processList(list);
        return list;
    }

    public List<Project> getProjectByConfigname(String dal_config_name) throws SQLException {
        FreeSelectSqlBuilder<List<Project>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id, name, namespace,dal_group_id,dal_config_name,update_user_no,update_time FROM project WHERE dal_config_name = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "dal_config_name", Types.VARCHAR, dal_config_name);
        builder.mapWith(projectRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        List<Project> list = queryDao.query(builder, parameters, hints);
        processList(list);
        return list;
    }

    private void processList(List<Project> list) throws SQLException {
        if (list == null || list.size() == 0)
            return;

        for (Project entity : list) {
            processProject(entity);
        }
    }

    private void processProject(Project entity) throws SQLException {
        entity.setText(entity.getName());
        entity.setChildren(false);
        entity.setIcon("glyphicon glyphicon-tasks");

        if (entity.getUpdate_time() == null)
            return;
        Date date = new Date(entity.getUpdate_time().getTime());
        entity.setStr_update_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
    }

    public int insertProject(Project project) throws SQLException {
        if (null == project)
            return 0;
        KeyHolder keyHolder = new KeyHolder();
        DalHints hints = DalHints.createIfAbsent(null);
        client.insert(hints, keyHolder, project);
        return keyHolder.getKey().intValue();
    }

    public int updateProject(Project project) throws SQLException {
        if (null == project)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.update(hints, project);
    }

    public int updateProjectGroupById(int groupId, int id) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("UPDATE project SET dal_group_id = ? WHERE id = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "dal_group_id", Types.INTEGER, groupId);
        parameters.set(i++, "id", Types.INTEGER, id);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

    public int deleteProject(Project project) throws SQLException {
        if (null == project)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.delete(hints, project);
    }
}
