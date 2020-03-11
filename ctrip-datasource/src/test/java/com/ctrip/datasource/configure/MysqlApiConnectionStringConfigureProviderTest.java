package com.ctrip.datasource.configure;

import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.datasource.util.EnvUtil;
import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;
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
    public void testConnectionString() throws Exception {
        String mgrUrl = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.9.72.67)(port=55944)," +
                "address=(type=master)(protocol=tcp)(host=10.25.82.137)(port=55944)," +
                "address=(type=master)(protocol=tcp)(host=10.60.53.211)(port=55944)/qconfig" +
                "?useUnicode=true&characterEncoding=UTF-8" +
                "&loadBalanceStrategy=serverAffinity&serverAffinityOrder=10.25.82.137:55944,10.60.53.211:55944,10.9.72.67:55944";

        ConnectionStringConfigureProvider provider = new MysqlApiConnectionStringConfigureProvider(DB_NAME);
        EnvUtil.setEnv("pro");
        DalConnectionStringConfigure configure = provider.getConnectionString();
        Assert.assertEquals(configure.getConnectionUrl(), mgrUrl);
        EnvUtil.setEnv(null);
    }

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
