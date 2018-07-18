package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.daogen.entity.DalGroup;

import java.sql.SQLException;
import java.util.List;

public class DalGroupDao {
    private DalTableDao<DalGroup> client;

    public DalGroupDao() throws SQLException {
        client = new DalTableDao<>(new DalDefaultJpaParser<>(DalGroup.class));
    }

    public List<DalGroup> getAllGroups() throws SQLException {
        SelectSqlBuilder builder = new SelectSqlBuilder().selectAll();
        DalHints hints = DalHints.createIfAbsent(null);
        return client.query(builder, hints);
    }

    public DalGroup getDalGroupById(Integer id) throws SQLException {
        DalHints hints = DalHints.createIfAbsent(null);
        return client.queryByPk(id, hints);
    }

    public int insertDalGroup(DalGroup group) throws SQLException {
        if (null == group)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.insert(hints, group);
    }

    public int updateDalGroup(DalGroup group) throws SQLException {
        if (null == group)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.update(hints, group);
    }

    public int deleteDalGroup(Integer groupId) throws SQLException {
        DalGroup group = new DalGroup();
        group.setId(groupId);
        DalHints hints = DalHints.createIfAbsent(null);
        return client.delete(hints, group);
    }

}
