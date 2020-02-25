package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;

public class CtripVariableDataSourceConfigureProviderTest {

    private static final String DB_NAME = "qconfig";
    private static final String DB_NAME_MGR = "kevin";

    @Test
    public void testGetDataSourceConfigure() {
        CtripVariableDataSourceConfigureProvider provider = new CtripVariableDataSourceConfigureProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add(DB_NAME);
        provider.setup(dbNames);
        DataSourceConfigure dataSourceConfigure = provider.getDataSourceConfigure(DB_NAME);
        Assert.assertNotNull(dataSourceConfigure);
    }

    @Test
    public void testCreateDataSource() throws Exception {
        DalDataSourceFactory factory = new DalDataSourceFactory();
        DataSource dataSource = factory.createVariableTypeDataSource(DB_NAME);
        Assert.assertNotNull(dataSource);
    }

    @Test
    public void testCreateMGRDataSource() throws Exception {
        DalDataSourceFactory factory = new DalDataSourceFactory();
        DataSource dataSource = factory.createVariableTypeDataSource(DB_NAME_MGR, new MockCtripVariableDataSourceConfigureProvider());
        Assert.assertNotNull(dataSource);
    }
}
