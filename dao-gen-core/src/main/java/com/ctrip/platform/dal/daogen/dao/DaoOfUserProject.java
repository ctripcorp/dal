package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.daogen.entity.UserProject;

import java.sql.*;
import java.util.List;

public class DaoOfUserProject extends BaseDao {
    private DalRowMapper<UserProject> userProjectRowMapper = null;

    public DaoOfUserProject() throws SQLException {
        userProjectRowMapper = new DalDefaultJpaMapper<>(UserProject.class);
    }

    public List<UserProject> getUserProjectsByUser(String userNo) throws SQLException {
        FreeSelectSqlBuilder<List<UserProject>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate("SELECT id, project_id, user_no FROM user_project WHERE user_no = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "user_no", Types.VARCHAR, userNo);
        builder.mapWith(userProjectRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

    public UserProject getMinUserProjectByProjectId(int project_id) throws SQLException {
        FreeSelectSqlBuilder<UserProject> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id,project_id, user_no FROM user_project WHERE id=(SELECT min(id) FROM user_project WHERE project_id = ?)");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "project_id", Types.INTEGER, project_id);
        builder.mapWith(userProjectRowMapper).requireFirst().nullable();
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

    public int deleteUserProject(int project_id) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("DELETE FROM user_project WHERE project_id = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "project_id", Types.INTEGER, project_id);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

}
