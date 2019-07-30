package com.ctrip.framework.plugin.test.application.test.dal;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalObjectRowMapper;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Created by shenjie on 2019/7/30.
 */
@Slf4j
@Component
public class TestDao {
    private DalQueryDao queryDao;
    private DalRowMapper<String> rowMapper;

    public TestDao() throws SQLException {
        this.queryDao = new DalQueryDao("daltestdb_w");
        this.rowMapper = new DalObjectRowMapper<>(String.class);
    }

    public String queryDatabase() throws SQLException {
        return queryDatabase(null);
    }

    private String queryDatabase(DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>();
        builder.setTemplate("SELECT DATABASE()");
        StatementParameters parameters = new StatementParameters();
        builder.mapWith(rowMapper).requireSingle().nullable();
        return queryDao.query(builder, parameters, hints);
    }
}
