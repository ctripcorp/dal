package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DaoOfDatabaseSet extends BaseDao {
    private DalTableDao<DatabaseSet> client;
    private DalTableDao<DatabaseSetEntry> client2;
    private DalRowMapper<DatabaseSet> databaseSetRowMapper = null;
    private DalRowMapper<DatabaseSetEntry> databaseSetEntryRowMapper = null;

    public DaoOfDatabaseSet() throws SQLException {
        client = new DalTableDao<>(new DalDefaultJpaParser<>(DatabaseSet.class));
        client2 = new DalTableDao<>(new DalDefaultJpaParser<>(DatabaseSetEntry.class));
        databaseSetRowMapper = new DalDefaultJpaMapper<>(DatabaseSet.class);
        databaseSetEntryRowMapper = new DalDefaultJpaMapper<>(DatabaseSetEntry.class);
    }

    public DatabaseSet getAllDatabaseSetById(Integer id) throws SQLException {
        DalHints hints = DalHints.createIfAbsent(null);
        DatabaseSet databaseSet = client.queryByPk(id, hints);
        processDatabaseSet(databaseSet);
        return databaseSet;
    }

    public List<DatabaseSet> getAllDatabaseSetByName(String name) throws SQLException {
        FreeSelectSqlBuilder<List<DatabaseSet>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id, name, provider, shardingStrategy, groupId, update_user_no, update_time FROM databaseset WHERE name = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "name", Types.VARCHAR, name);
        builder.mapWith(databaseSetRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        List<DatabaseSet> list = queryDao.query(builder, parameters, hints);
        processList(list);
        return list;
    }

    public List<DatabaseSet> getAllDatabaseSetByGroupId(Integer groupId) throws SQLException {
        FreeSelectSqlBuilder<List<DatabaseSet>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id, name, provider, shardingStrategy, groupId, update_user_no, update_time FROM databaseset WHERE groupId = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "groupId", Types.INTEGER, groupId);
        builder.mapWith(databaseSetRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        List<DatabaseSet> list = queryDao.query(builder, parameters, hints);
        processList(list);
        return list;
    }

    private void processList(List<DatabaseSet> list) throws SQLException {
        if (list == null || list.size() == 0)
            return;

        for (DatabaseSet entity : list) {
            processDatabaseSet(entity);
        }
    }

    private void processDatabaseSet(DatabaseSet entity) throws SQLException {
        if (entity.getUpdate_time() == null)
            return;
        Date date = new Date(entity.getUpdate_time().getTime());
        entity.setStr_update_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
    }

    public List<DatabaseSetEntry> getAllDatabaseSetEntryByDbsetid(Integer databaseSet_Id) throws SQLException {
        FreeSelectSqlBuilder<List<DatabaseSetEntry>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id, name, databaseType, sharding, connectionString, databaseSet_Id, update_user_no, update_time FROM databasesetentry WHERE databaseSet_Id = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "databaseSet_Id", Types.INTEGER, databaseSet_Id);
        builder.mapWith(databaseSetEntryRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        List<DatabaseSetEntry> list = queryDao.query(builder, parameters, hints);
        processEntryList(list);
        return list;
    }

    public DatabaseSetEntry getMasterDatabaseSetEntryByDatabaseSetName(String dbName) throws SQLException {
        FreeSelectSqlBuilder<DatabaseSetEntry> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT en.id, en.name, en.databaseType, en.sharding, en.connectionString, en.databaseSet_Id, en.update_user_no, en.update_time "
                        + "FROM databasesetentry as en join databaseset as se on en.databaseSet_Id = se.id "
                        + "WHERE se.name = ? and en.databaseType = 'Master' limit 1");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "name", Types.VARCHAR, dbName);
        builder.mapWith(databaseSetEntryRowMapper).requireFirst().nullable();
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        DatabaseSetEntry entry = queryDao.query(builder, parameters, hints);
        processDatabaseSetEntry(entry);
        return entry;
    }

    private void processEntryList(List<DatabaseSetEntry> list) throws SQLException {
        if (list == null || list.size() == 0)
            return;

        for (DatabaseSetEntry entity : list) {
            processDatabaseSetEntry(entity);
        }
    }

    private void processDatabaseSetEntry(DatabaseSetEntry entity) throws SQLException {
        if (entity == null)
            return;
        if (entity.getUpdate_time() == null)
            return;
        Date date = new Date(entity.getUpdate_time().getTime());
        entity.setStr_update_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
    }

    public int insertDatabaseSet(DatabaseSet dbset) throws SQLException {
        if (null == dbset)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.insert(hints, dbset);
    }

    public int updateDatabaseSet(DatabaseSet dbset) throws SQLException {
        if (null == dbset)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.update(hints, dbset);
    }

    public int insertDatabaseSetEntry(DatabaseSetEntry dbsetEntry) throws SQLException {
        if (null == dbsetEntry)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client2.insert(hints, dbsetEntry);
    }

    public int updateDatabaseSetEntry(DatabaseSetEntry dbsetEntry) throws SQLException {
        if (null == dbsetEntry)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client2.update(hints, dbsetEntry);
    }

    public int deleteDatabaseSetById(Integer dbsetId) throws SQLException {
        DatabaseSet dbset = new DatabaseSet();
        dbset.setId(dbsetId);
        DalHints hints = DalHints.createIfAbsent(null);
        return client.delete(hints, dbset);
    }

    /**
     * 依据外键databaseSet_Id删除entry
     *
     * @param dbsetId
     * @return
     */
    public int deleteDatabaseSetEntryByDbsetId(Integer dbsetId) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("DELETE FROM databasesetentry WHERE databaseSet_Id = ?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.setSensitive(i++, "databaseSet_Id", Types.INTEGER, dbsetId);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

    /**
     * 根据主键id删除entry
     *
     * @param id
     * @return
     */
    public int deleteDatabaseSetEntryById(Integer id) throws SQLException {
        DatabaseSetEntry entry = new DatabaseSetEntry();
        entry.setId(id);
        DalHints hints = DalHints.createIfAbsent(null);
        return client2.delete(hints, entry);
    }

}


