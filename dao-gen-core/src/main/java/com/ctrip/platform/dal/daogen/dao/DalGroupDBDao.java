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
import com.ctrip.platform.dal.daogen.entity.DalGroupDB;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DalGroupDBDao extends BaseDao {
    private DalTableDao<DalGroupDB> client;
    private DalRowMapper<DalGroupDB> dalGroupDBRowMapper = null;

    public DalGroupDBDao() throws SQLException {
        client = new DalTableDao<>(new DalDefaultJpaParser<>(DalGroupDB.class));
        dalGroupDBRowMapper = new DalDefaultJpaMapper<>(DalGroupDB.class);
    }

    public List<DalGroupDB> getAllGroupDbs() throws SQLException {
        FreeSelectSqlBuilder<List<DalGroupDB>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT a.id,a.dbname,a.dal_group_id,a.db_address,a.db_port,a.db_user,a.db_password,a.db_catalog,a.db_providerName,b.group_name as comment ");
        sb.append("FROM alldbs a LEFT JOIN dal_group b ON b.id = a.dal_group_id ");
        builder.setTemplate(sb.toString());
        StatementParameters parameters = new StatementParameters();
        builder.mapWith(dalGroupDBRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

    public List<String> getAllDbAllinOneNames() throws SQLException {
        FreeSelectSqlBuilder<List<String>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate("SELECT dbname FROM alldbs");
        StatementParameters parameters = new StatementParameters();
        builder.mapWith(dalGroupDBRowMapper).simpleType();
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

    public DalGroupDB getGroupDBByDbId(int id) throws SQLException {
        DalHints hints = DalHints.createIfAbsent(null);
        return client.queryByPk(id, hints);
    }

    public List<DalGroupDB> getGroupDBsByGroup(int groupId) throws SQLException {
        FreeSelectSqlBuilder<List<DalGroupDB>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id, dbname, comment,dal_group_id, db_address, db_port, db_user, db_password, db_catalog, db_providerName FROM alldbs WHERE dal_group_id=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "dal_group_id", Types.INTEGER, groupId);
        builder.mapWith(dalGroupDBRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

    public DalGroupDB getGroupDBByDbName(String dbname) throws SQLException {
        FreeSelectSqlBuilder<DalGroupDB> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id, dbname, comment,dal_group_id, db_address, db_port, db_user, db_password, db_catalog, db_providerName FROM alldbs WHERE dbname=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "dbname", Types.VARCHAR, dbname);
        builder.mapWith(dalGroupDBRowMapper).requireFirst().nullable();
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

    public List<DalGroupDB> getGroupDbsByDbNames(Set<String> dbNames) throws SQLException {
        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("id", "db_catalog", "db_port", "db_providerName", "dbname", "db_password", "db_user",
                "db_address", "comment", "dal_group_id");
        builder.in("dbname", new ArrayList<>(dbNames), Types.VARCHAR, false);
        DalHints hints = DalHints.createIfAbsent(null);
        return client.query(builder, hints);
    }

    public int insertDalGroupDB(DalGroupDB groupDb) throws SQLException {
        if (null == groupDb)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.insert(hints, groupDb);
    }

    public int updateGroupDB(int id, String dbname, String db_address, String db_port, String db_user,
            String db_password, String db_catalog, String db_providerName) throws Exception {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate(
                "UPDATE alldbs SET dbname=?, db_address=?, db_port=?, db_user=?, db_password=?, db_catalog=?, db_providerName=? WHERE id=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "dbname", Types.VARCHAR, dbname);
        parameters.set(i++, "db_address", Types.VARCHAR, db_address);
        parameters.set(i++, "db_port", Types.VARCHAR, db_port);
        parameters.set(i++, "db_user", Types.VARCHAR, db_user);
        parameters.set(i++, "db_password", Types.VARCHAR, db_password);
        parameters.set(i++, "db_catalog", Types.VARCHAR, db_catalog);
        parameters.set(i++, "db_providerName", Types.VARCHAR, db_providerName);
        parameters.set(i++, "id", Types.INTEGER, id);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

    public int updateGroupDB(int id, String comment) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("UPDATE alldbs SET comment=? WHERE id=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "comment", Types.VARCHAR, comment);
        parameters.set(i++, "id", Types.INTEGER, id);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

    public int updateGroupDB(int id, Integer groupId) throws SQLException {
        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("UPDATE alldbs SET dal_group_id=? WHERE id=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "dal_group_id", Types.INTEGER, groupId);
        parameters.set(i++, "id", Types.INTEGER, id);
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.update(builder, parameters, hints);
    }

    public int deleteDalGroupDB(int id) throws SQLException {
        DalGroupDB groupDb = new DalGroupDB();
        groupDb.setId(id);
        DalHints hints = DalHints.createIfAbsent(null);
        return client.delete(hints, groupDb);
    }

}
