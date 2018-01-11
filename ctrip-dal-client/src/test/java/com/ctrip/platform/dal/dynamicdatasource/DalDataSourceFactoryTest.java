package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;

public class DalDataSourceFactoryTest {
    private static final String name = "mysqldaltest01db_W";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testDalDataSourceFactoryCreateDataSource() throws Exception {
        try {
            DalDataSourceFactory factory = new DalDataSourceFactory();
            factory.createDataSource(name);
        } catch (Throwable e) {
            Assert.assertTrue(false);
        }

        DataSourceLocator locator = new DataSourceLocator();
        DataSource dataSource = locator.getDataSource(name);
        Assert.assertTrue(dataSource != null);
    }

}
