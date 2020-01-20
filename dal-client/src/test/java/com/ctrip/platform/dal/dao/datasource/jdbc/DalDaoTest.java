package com.ctrip.platform.dal.dao.datasource.jdbc;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
import com.ctrip.platform.dal.dao.helper.FixedValueRowMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by taochen on 2020/1/19.
 */
public class DalDaoTest {
    private static final String dbName = "dao_test";
    private static final String KEY_NAME = "dao_test";

    @Before
    public void setUp() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testDalDaoExecuteSql() throws Exception {
        //TestTableDao testTableDao = new TestTableDao();
        DalQueryDao client = new DalQueryDao(dbName);


        client.query("select 1", new StatementParameters(), new DalHints(), new FixedValueRowMapper<>());
        RefreshableDataSource dataSource = getDataSource();
        Assert.assertEquals(0, dataSource.getFirstAppearContinuousErrorTime());
        Assert.assertEquals(0, dataSource.getLastReportContinuousErrorTime());

        try {
            client.query("select *from noTable", new StatementParameters(), new DalHints(), new FixedValueRowMapper<>());
        } catch (SQLException e) {

        }
        Assert.assertNotEquals(0, dataSource.getFirstAppearContinuousErrorTime());
        Assert.assertEquals(0, dataSource.getLastReportContinuousErrorTime());

        Thread.sleep(60*1000);
        try {
            client.query("select *from noTable", new StatementParameters(), new DalHints(), new FixedValueRowMapper<>());
        } catch (SQLException e) {

        }
        Assert.assertNotEquals(0, dataSource.getFirstAppearContinuousErrorTime());
        Assert.assertNotEquals(0, dataSource.getLastReportContinuousErrorTime());

        client.query("select 1", new StatementParameters(), new DalHints(), new FixedValueRowMapper<>());
        Assert.assertEquals(0, dataSource.getFirstAppearContinuousErrorTime());
        Assert.assertEquals(0, dataSource.getLastReportContinuousErrorTime());
    }

    private RefreshableDataSource getDataSource() {
        DataSourceLocator dataSourceLocator = new DataSourceLocator();
        return (RefreshableDataSource) dataSourceLocator.getDataSource(KEY_NAME);
    }
}
