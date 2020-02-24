package com.ctrip.datasource.configure;

import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;

public class CtripVariableDataSourceConfigureProviderTest {

    @Test
    public void testCreateVariableDataSource() throws Exception {
        DalDataSourceFactory factory = new DalDataSourceFactory();
        DataSource dataSource = factory.createVariableTypeDataSource("qconfig");
        Assert.assertNotNull(dataSource);
    }
}
