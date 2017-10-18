package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PoolSettingsTest {
    @Test
    public void testDynamicDataSourcePoolSettings() throws InterruptedException {
        String name = "mysqldaltest01db_W";

        DataSourceConfigure config = DataSourceConfigureLocator.getInstance().getDataSourceConfigure(name);
        String connectionProperties = config.getProperty(DataSourceConfigureConstants.CONNECTIONPROPERTIES);
        System.out.println(String.format("***** connectionProperties before:%s *****", connectionProperties));

        System.out.println("*****Ready to sleep 30 seconds...*****");
        Thread.sleep(30 * 1000);
        System.out.println("*****Waked up.*****");

        DataSourceConfigure config1 = DataSourceConfigureLocator.getInstance().getDataSourceConfigure(name);
        String connectionProperties1 = config1.getProperty(DataSourceConfigureConstants.CONNECTIONPROPERTIES);
        System.out.println(String.format("***** connectionProperties after:%s *****", connectionProperties1));

    }

    @Test
    public void testBatchInsert() throws Exception {
        DalTableDao<TestTable> client = new DalTableDao<>(new DalDefaultJpaParser<>(TestTable.class));

        for (int i = 0; i < 10000; i++) {
            List<TestTable> list = new ArrayList<>();
            TestTable table = new TestTable();
            table.setColumn1(i);
            table.setColumn2(i);
            list.add(table);

            TestTable table1 = new TestTable();
            table1.setColumn1(i);
            table1.setColumn2(i);
            list.add(table1);

            DalHints hints = DalHints.createIfAbsent(null);
            int[] result = client.batchInsert(hints, list);

            System.out.println(result[0]);
            Thread.sleep(3 * 1000);
        }
    }
}
