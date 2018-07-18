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
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.dao.KeyHolder;

import java.sql.*;
import java.util.List;

public class DaoOfLoginUser extends BaseDao {
    private DalTableDao<LoginUser> client;
    private DalRowMapper<LoginUser> loginUserRowMapper = null;

    public DaoOfLoginUser() throws SQLException {
        client = new DalTableDao<>(new DalDefaultJpaParser<>(LoginUser.class));
        loginUserRowMapper = new DalDefaultJpaMapper<>(LoginUser.class);
    }

    public List<LoginUser> getAllUsers() throws SQLException {
        SelectSqlBuilder builder = new SelectSqlBuilder().selectAll();
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return client.query(builder, hints);
    }

    public LoginUser getUserById(int userId) throws SQLException {
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return client.queryByPk(userId, hints);
    }

    public LoginUser getUserByNo(String userNo) throws SQLException {
        FreeSelectSqlBuilder<LoginUser> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate("SELECT id, user_no, user_name, user_email, password FROM login_users WHERE user_no = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.setSensitive(i++, "user_no", Types.VARCHAR, userNo);
        builder.mapWith(loginUserRowMapper).requireFirst().nullable();
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

    public List<LoginUser> getUserByGroupId(int groupId) throws SQLException {
        FreeSelectSqlBuilder<List<LoginUser>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT tb2.id, tb2.user_no, tb2.user_name, tb2.user_email, tb2.password, tb1.role, tb1.adduser FROM user_group tb1 LEFT JOIN login_users tb2 ON tb1.user_id = tb2.id WHERE tb1.group_id = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.setSensitive(i++, "group_id", Types.INTEGER, groupId);
        builder.mapWith(loginUserRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

    public int insertUser(LoginUser user) throws SQLException {
        if (null == user)
            return 0;

        KeyHolder keyHolder = new KeyHolder();
        DalHints hints = DalHints.createIfAbsent(null);
        client.insert(hints, keyHolder, user);
        return keyHolder.getKey().intValue();
    }

    public int updateUser(LoginUser user) throws SQLException {
        if (null == user)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.update(hints, user);
    }

    public int updateUserPassword(LoginUser user) throws SQLException {
        if (user == null)
            return 0;

        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("UPDATE login_users SET password = ? WHERE id = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "password", Types.VARCHAR, user.getPassword());
        parameters.set(i++, "id", Types.INTEGER, user.getId());
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

    public int deleteUser(int userId) throws SQLException {
        LoginUser user = new LoginUser();
        user.setId(userId);
        DalHints hints = DalHints.createIfAbsent(null);
        return client.delete(hints, user);
    }

}
