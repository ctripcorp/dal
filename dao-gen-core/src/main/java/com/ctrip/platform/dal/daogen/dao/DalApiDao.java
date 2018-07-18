package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.daogen.entity.DalApi;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class DalApiDao extends BaseDao {
    private DalTableDao<DalApi> client;
    private DalRowMapper<DalApi> dalApiRowMapper = null;

    public DalApiDao() throws SQLException {
        client = new DalTableDao<>(new DalDefaultJpaParser<>(DalApi.class));
        dalApiRowMapper = new DalDefaultJpaMapper<>(DalApi.class);
    }

    public DalApi getDalApiById(Integer id) throws SQLException {
        DalHints hints = DalHints.createIfAbsent(null);
        return client.queryByPk(id, hints);
    }

    public List<DalApi> getDalApiByLanguageAndDbtype(String language, String db_type) throws SQLException {
        FreeSelectSqlBuilder<List<DalApi>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT id, language, db_type, crud_type, method_declaration, method_description,sp_type FROM api_list WHERE language = ? AND db_type=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "language", Types.VARCHAR, language);
        parameters.set(i++, "db_type", Types.VARCHAR, db_type);
        builder.mapWith(dalApiRowMapper);
        DalHints hints = DalHints.createIfAbsent(null).allowPartial();
        return queryDao.query(builder, parameters, hints);
    }

}
