package com.ctrip.platform.dal.application.service;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;

public class DataSourceTest {

    @Test
    public void testGetClusterDataSource() throws Exception {
        DalDataSourceFactory factory = new DalDataSourceFactory();
        DataSource dataSource = factory.createDataSource("DalService2DB_W");
        Assert.assertNotNull(dataSource);
    }

}
