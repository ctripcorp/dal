package com.ctrip.datasource.sqlserverjdbc;

import java.sql.Types;
import java.util.List;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class SqlServerJDBCDriverTest {
    private DalTableDao<SqlServerJDBCPojo> client;

    @Test
    public void testSqlServerJDBC() throws Exception {
        client = new DalTableDao<>(new DalDefaultJpaParser<>(SqlServerJDBCPojo.class));
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "nvarcharField", Types.NVARCHAR, "nvarchartest");
        parameters.set(i++, "varcharField", Types.VARCHAR, "varchartest");
        List<SqlServerJDBCPojo> list =
                client.query(" nvarcharField = ? and varcharField = ?", parameters, new DalHints());

    }

}
