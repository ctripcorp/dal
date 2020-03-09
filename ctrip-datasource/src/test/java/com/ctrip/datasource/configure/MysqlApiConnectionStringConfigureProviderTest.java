package com.ctrip.datasource.configure;

import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.datasource.ApiDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;

public class MysqlApiConnectionStringConfigureProviderTest {

    private static final String DB_NAME = "qconfig";
    private static final String DB_NAME_MGR = "kevin";

    @Test
    public void testCustomGetDataSourceConfigure() {
        ConnectionStringConfigureProvider provider = new MockConnectionStringConfigureProvider();
        DataSourceConfigureLocator locator = DataSourceConfigureManager.getInstance().getDataSourceConfigureLocator();
        DataSourceConfigure dataSourceConfigure = locator.getDataSourceConfigure(new ApiDataSourceIdentity(provider));
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
        DataSource dataSource = factory.createVariableTypeDataSource(new MockConnectionStringConfigureProvider());
        Assert.assertNotNull(dataSource);
    }
}
