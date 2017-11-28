package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;

import java.sql.SQLException;

public class DynamicDataSourceDao {
    private DalTableDao<DynamicDataSourcePojo> client;
    private static final String DATA_BASE = "dynamicdatasource";
    private static final DatabaseCategory dbCategory = DatabaseCategory.MySql;
    private DalQueryDao queryDao = null;

    public DynamicDataSourceDao() throws SQLException {
        this.client = new DalTableDao<>(new DalDefaultJpaParser<>(DynamicDataSourcePojo.class));
        this.queryDao = new DalQueryDao(DATA_BASE);
    }

    /**
     * 自定义，查询
     **/
    public String selectHostname(DalHints hints) throws Exception {
        hints = DalHints.createIfAbsent(hints);

        FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate("select @@hostname");
        StatementParameters parameters = new StatementParameters();
        builder.simpleType().requireFirst().nullable();
        return queryDao.query(builder, parameters, hints);
    }

    /**
     * 自定义，查询
     **/
    public String selectDatabase(DalHints hints) throws Exception {
        hints = DalHints.createIfAbsent(hints);

        FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate("select database()");
        StatementParameters parameters = new StatementParameters();
        builder.simpleType().requireFirst().nullable();
        return queryDao.query(builder, parameters, hints);
    }

}
