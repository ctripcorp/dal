package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GroupRelation;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

public class GroupRelationDao extends BaseDao {
    private DalTableDao<GroupRelation> client;
    private DalRowMapper<GroupRelation> groupRelationRowMapper = null;

    public GroupRelationDao() throws SQLException {
        client = new DalTableDao<>(new DalDefaultJpaParser<>(GroupRelation.class));
        groupRelationRowMapper = new DalDefaultJpaMapper<>(GroupRelation.class);
    }

    public List<GroupRelation> getAllGroupRelationByCurrentGroupId(Integer currentGroupId) throws SQLException {
        FreeSelectSqlBuilder<List<GroupRelation>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id, current_group_id, child_group_id, child_group_role, adduser, update_user_no ,update_time FROM group_relation WHERE current_group_id = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "current_group_id", Types.INTEGER, currentGroupId);
        builder.mapWith(groupRelationRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

    public List<GroupRelation> getAllGroupRelationByChildGroupId(Integer childGroupId) throws SQLException {
        FreeSelectSqlBuilder<List<GroupRelation>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id, current_group_id, child_group_id, child_group_role, adduser, update_user_no ,update_time FROM group_relation WHERE child_group_id = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "child_group_id", Types.INTEGER, childGroupId);
        builder.mapWith(groupRelationRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

    public GroupRelation getGroupRelationByCurrentGroupIdAndChildGroupId(Integer currentGroupId, Integer childGroupId)
            throws SQLException {
        FreeSelectSqlBuilder<GroupRelation> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id, current_group_id, child_group_id, child_group_role, adduser, update_user_no ,update_time FROM group_relation WHERE current_group_id = ? AND child_group_id = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "current_group_id", Types.INTEGER, currentGroupId);
        parameters.set(i++, "child_group_id", Types.INTEGER, childGroupId);
        builder.mapWith(groupRelationRowMapper).requireFirst().nullable();
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

    public int insertChildGroup(GroupRelation relation) throws SQLException {
        if (null == relation)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.insert(hints, relation);
    }

    public int updateGroupRelation(Integer currentGroupId, Integer childGroupId, Integer childGroupRole,
            Integer adduser, String updateUserNo, Timestamp updateTime) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate(
                "UPDATE group_relation SET child_group_role=?, adduser=?, update_user_no=?,update_time=? WHERE current_group_id = ? AND child_group_id = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "child_group_role", Types.INTEGER, childGroupRole);
        parameters.set(i++, "adduser", Types.INTEGER, adduser);
        parameters.set(i++, "update_user_no", Types.VARCHAR, updateUserNo);
        parameters.set(i++, "update_time", Types.TIMESTAMP, updateTime);
        parameters.set(i++, "current_group_id", Types.INTEGER, currentGroupId);
        parameters.set(i++, "child_group_id", Types.INTEGER, childGroupId);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

    public int deleteChildGroupByCurrentGroupIdAndChildGroupId(Integer currentGroupId, Integer childGroupId)
            throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("DELETE FROM group_relation WHERE current_group_id = ? AND child_group_id = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "current_group_id", Types.INTEGER, currentGroupId);
        parameters.set(i++, "child_group_id", Types.INTEGER, childGroupId);

        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }
}
