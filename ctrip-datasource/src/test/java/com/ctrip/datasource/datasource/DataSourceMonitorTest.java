package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
import com.ctrip.platform.dal.dao.helper.FixedValueRowMapper;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by taochen on 2020/1/19.
 */
public class DataSourceMonitorTest {
    private static final String dbName = "mysqldaltest01db_W";
    private static final String KEY_NAME = "mysqldaltest01db_W";

    @Test
    public void testDataSourceSwitchErrorReport() throws Exception {
        DalQueryDao client = new DalQueryDao(dbName);


        client.query("select 1", new StatementParameters(), new DalHints(), new FixedValueRowMapper<>());
        RefreshableDataSource dataSource = getDataSource();
        Assert.assertEquals(0, dataSource.getFirstErrorTime());
        Assert.assertEquals(0, dataSource.getLastReportErrorTime());

        try {
            client.query("select *from noTable", new StatementParameters(), new DalHints(), new FixedValueRowMapper<>());
        } catch (SQLException e) {

        }
        Assert.assertNotEquals(0, dataSource.getFirstErrorTime());
        Assert.assertEquals(0, dataSource.getLastReportErrorTime());

        Thread.sleep(60 * 1000);
        try {
            client.query("select *from noTable", new StatementParameters(), new DalHints(), new FixedValueRowMapper<>());
        } catch (SQLException e) {

        }

        Assert.assertNotEquals(0, dataSource.getFirstErrorTime());
        Assert.assertNotEquals(0, dataSource.getLastReportErrorTime());
        long oldLastReportErrorTime1 = dataSource.getLastReportErrorTime();
        Thread.sleep(30 * 1000);

        try {
            client.query("select *from noTable", new StatementParameters(), new DalHints(), new FixedValueRowMapper<>());
        } catch (SQLException e) {

        }
        Assert.assertNotEquals(0, dataSource.getFirstErrorTime());
        Assert.assertNotEquals(oldLastReportErrorTime1, dataSource.getLastReportErrorTime());

        long oldLastReportErrorTime2 = dataSource.getLastReportErrorTime();
        try {
            client.query("select *from noTable", new StatementParameters(), new DalHints(), new FixedValueRowMapper<>());
        } catch (SQLException e) {

        }
        Assert.assertNotEquals(0, dataSource.getFirstErrorTime());
        Assert.assertEquals(oldLastReportErrorTime2, dataSource.getLastReportErrorTime());

        client.query("select 1", new StatementParameters(), new DalHints(), new FixedValueRowMapper<>());
        Assert.assertEquals(0, dataSource.getFirstErrorTime());
        Assert.assertEquals(0, dataSource.getLastReportErrorTime());
    }

    private RefreshableDataSource getDataSource() {
        DataSourceLocator dataSourceLocator = new DataSourceLocator();
        return (RefreshableDataSource) dataSourceLocator.getDataSource(KEY_NAME);
    }
}
