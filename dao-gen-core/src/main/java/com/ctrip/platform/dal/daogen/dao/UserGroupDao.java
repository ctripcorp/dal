package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.daogen.entity.UserGroup;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class UserGroupDao extends BaseDao {
    private DalRowMapper<UserGroup> userGroupRowMapper = null;
    private DalTableDao<UserGroup> client;

    public UserGroupDao() throws SQLException {
        userGroupRowMapper = new DalDefaultJpaMapper<>(UserGroup.class);
        client = new DalTableDao<>(new DalDefaultJpaParser<>(UserGroup.class));
    }

    public List<UserGroup> getUserGroupByUserId(Integer userId) throws SQLException {
        FreeSelectSqlBuilder<List<UserGroup>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate("SELECT id, user_id, group_id, role, adduser FROM user_group WHERE user_id = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "user_id", Types.INTEGER, userId);
        builder.mapWith(userGroupRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

    public List<UserGroup> getUserGroupByGroupIdAndUserId(Integer groupId, Integer userId) throws SQLException {
        FreeSelectSqlBuilder<List<UserGroup>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id, user_id, group_id, role, adduser FROM user_group WHERE group_id = ? AND user_id=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "group_id", Types.INTEGER, groupId);
        parameters.set(i++, "user_id", Types.INTEGER, userId);
        builder.mapWith(userGroupRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

    public int insertUserGroup(Integer user_id, Integer group_id, Integer role, Integer adduser) throws SQLException {
        UserGroup userGroup = new UserGroup();
        userGroup.setUser_id(user_id);
        userGroup.setGroup_id(group_id);
        userGroup.setRole(role);
        userGroup.setAdduser(adduser);
        DalHints hints = DalHints.createIfAbsent(null);
        return client.insert(hints, userGroup);
    }

    public int deleteUserFromGroup(Integer user_id, Integer group_id) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("DELETE FROM user_group WHERE user_id=? AND group_id=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "user_id", Types.INTEGER, user_id);
        parameters.set(i++, "group_id", Types.INTEGER, group_id);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

    public int updateUserPersimion(Integer userId, Integer groupId, Integer role, Integer adduser) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("UPDATE user_group SET role=?, adduser=?  WHERE user_id=? AND group_id=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "role", Types.INTEGER, role);
        parameters.set(i++, "adduser", Types.INTEGER, adduser);
        parameters.set(i++, "user_id", Types.INTEGER, userId);
        parameters.set(i++, "group_id", Types.INTEGER, groupId);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

}
