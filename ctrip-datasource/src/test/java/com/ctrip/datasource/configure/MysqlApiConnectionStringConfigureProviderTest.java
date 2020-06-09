package com.ctrip.datasource.configure;

import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.datasource.util.CtripEnvUtils;
import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.PropertiesWrapper;
import com.ctrip.platform.dal.dao.datasource.ApiDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.sql.DatabaseMetaData;

public class MysqlApiConnectionStringConfigureProviderTest {

    private static final String DB_NAME_1 = "qconfig";
    private static final String DB_NAME_2 = "fxdalclusterbenchmarkdb";
    private static final String DB_NAME_3 = "fxqconfigtestdb";
    private static final String DB_NAME_MGR = "kevin";

    private static CtripEnvUtils envUtils = (CtripEnvUtils) DalElementFactory.DEFAULT.getEnvUtils();

    @Test
    public void testConnectionString() throws Exception {
        String mgrUrl = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.9.72.67)(port=55944)," +
                "address=(type=master)(protocol=tcp)(host=10.25.82.137)(port=55944)," +
                "address=(type=master)(protocol=tcp)(host=10.60.53.211)(port=55944)/qconfig" +
                "?useUnicode=true&characterEncoding=UTF-8" +
                "&loadBalanceStrategy=serverAffinity&serverAffinityOrder="+
                "address=(type=master)(protocol=tcp)(host=10.25.82.137)(port=55944):3306," +
                "address=(type=master)(protocol=tcp)(host=10.60.53.211)(port=55944):3306," +
                "address=(type=master)(protocol=tcp)(host=10.9.72.67)(port=55944):3306";
        ConnectionStringConfigureProvider provider = new MysqlApiConnectionStringConfigureProvider(DB_NAME_1);
        envUtils.setEnv("pro");
        DalConnectionStringConfigure configure = provider.getConnectionString();
        Assert.assertEquals(configure.getConnectionUrl(), mgrUrl);
        envUtils.setEnv(null);
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
        DataSource dataSource = factory.createVariableTypeDataSource(DB_NAME_1);
        Assert.assertNotNull(dataSource);
    }

    @Test
    public void testCreateMGRDataSource() throws Exception {
        DalDataSourceFactory factory = new DalDataSourceFactory();
        envUtils.setEnv("pro");
        DataSource dataSource = factory.createVariableTypeDataSource(DB_NAME_3);
        Assert.assertNotNull(dataSource);
        envUtils.setEnv(null);
    }

    @Test
    public void testCreateMGRDataSourceCustomProvider() throws Exception {
        String mgrUrl = "jdbc:mysql://address=(type=master)(protocol=tcp)(host=10.2.7.184)(port=3306):3306:3306/";
        DalDataSourceFactory factory = new DalDataSourceFactory();
        DataSource dataSource = factory.createVariableTypeDataSource(new MockConnectionStringConfigureProvider());

        Assert.assertNotNull(dataSource);
        for (int i = 0; i < 5; ++i) {
            DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            String url = metaData.getURL();
            Assert.assertTrue(mgrUrl.equalsIgnoreCase(url));
        }
    }

    @Test
    public void testDBModelChange() throws Exception {
        envUtils.setEnv("pro");
        ConnectionStringConfigureProvider provider = new MysqlApiConnectionStringConfigureProvider(DB_NAME_2);
        DalConnectionStringConfigure configure = provider.getConnectionString();
        String normalUrl = configure.getConnectionUrl();
        Assert.assertTrue(normalUrl.startsWith("jdbc:mysql://"));
        DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureManager.getInstance().getDataSourceConfigureLocator();
        PropertiesWrapper propertiesWrapper = dataSourceConfigureLocator.getPoolProperties();
        propertiesWrapper.getDatasourceProperties().get(DB_NAME_2).setProperty("dbModel", "mgr");

        String mgrUrl = provider.getConnectionString().getConnectionUrl();
        Assert.assertTrue(mgrUrl.startsWith("jdbc:mysql:replication://"));
        envUtils.setEnv(null);
    }

    @Test
    public void testDecrypt() throws UnsupportedEncodingException {
        String password = "YTIwZ2t5eHVDcXl6bnZ5dmhHe2F4d2tiI2F5YXAwUWtwaDZ0bXdxVg==";
        String token = "a{GhvyvnzyqCuxykg02a";
        String decodePassword = new String(Base64.decodeBase64(password), "utf-8");
        StringBuilder sb = new StringBuilder(decodePassword);
        sb.reverse();
        System.out.println(sb.substring(0, sb.toString().indexOf(token)));
    }
}
