package com.ctrip.datasource.configure;

import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.datasource.util.CtripEnvUtils;
import com.ctrip.datasource.util.entity.ClusterNodeInfo;
import com.ctrip.platform.dal.common.enums.DBModel;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.ApiDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.sql.DatabaseMetaData;
import java.util.*;

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
    public void testLocalAccessConnectionString() throws Exception {
        String mgrUrl = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.9.72.67)(port=55944)," +
                "address=(type=master)(protocol=tcp)(host=10.25.82.137)(port=55944)," +
                "address=(type=master)(protocol=tcp)(host=10.60.53.211)(port=55944)/qconfig" +
                "?useUnicode=true&characterEncoding=UTF-8" +
                "&loadBalanceStrategy=serverAffinity&serverAffinityOrder="+
                "address=(type=master)(protocol=tcp)(host=10.9.72.67)(port=55944):3306," +
                "address=(type=master)(protocol=tcp)(host=10.60.53.211)(port=55944):3306," +
                "address=(type=master)(protocol=tcp)(host=10.25.82.137)(port=55944):3306";
        MockProvider provider = new MockProvider(DB_NAME_1, true,
                new String[] { "sharb", "shaoy", "shafq", "shajq" });
        envUtils.setEnv("pro");
        envUtils.setIdc("shafq");
        DalConnectionStringConfigure configure = provider.getConnectionString();
        Assert.assertEquals(configure.getConnectionUrl(), mgrUrl);
        envUtils.setEnv(null);
        envUtils.setIdc(null);
    }

    @Test
    public void testNonLocalAccessConnectionString() throws Exception {
        String mgrUrl = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.9.72.67)(port=55944)," +
                "address=(type=master)(protocol=tcp)(host=10.25.82.137)(port=55944)," +
                "address=(type=master)(protocol=tcp)(host=10.60.53.211)(port=55944)/qconfig" +
                "?useUnicode=true&characterEncoding=UTF-8" +
                "&loadBalanceStrategy=serverAffinity&serverAffinityOrder="+
                "address=(type=master)(protocol=tcp)(host=10.60.53.211)(port=55944):3306," +
                "address=(type=master)(protocol=tcp)(host=10.25.82.137)(port=55944):3306," +
                "address=(type=master)(protocol=tcp)(host=10.9.72.67)(port=55944):3306";
        MockProvider provider = new MockProvider(DB_NAME_1, false,
                new String[] { "sharb", "shaoy", "shafq", "shajq" });
        envUtils.setEnv("pro");
        envUtils.setIdc("shafq");
        DalConnectionStringConfigure configure = provider.getConnectionString();
        Assert.assertEquals(configure.getConnectionUrl(), mgrUrl);
        envUtils.setEnv(null);
        envUtils.setIdc(null);
    }

    @Test
    public void testCustomGetDataSourceConfigure() {
        ConnectionStringConfigureProvider provider = new MockConnectionStringConfigureProvider();
        DataSourceConfigureLocator locator = DataSourceConfigureManager.getInstance().getDataSourceConfigureLocator();
        DataSourceConfigure dataSourceConfigure = locator.getDataSourceConfigure(new ApiDataSourceIdentity(provider));
        Assert.assertNotNull(dataSourceConfigure);
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
        String url = configure.getConnectionUrl();
        Assert.assertTrue(url.startsWith("jdbc:mysql://"));
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

    @Test
    public void testGetServerAffinityOrder() {
        envUtils.setIdc("b");
        List<ClusterNodeInfo> nodes = new ArrayList<>();
        nodes.add(mockClusterNodeInfo("c", "1.1.1.1", 1111));
        nodes.add(mockClusterNodeInfo("b", "2.2.2.2", 2222));
        nodes.add(mockClusterNodeInfo("a", "3.3.3.3", 3333));
        nodes.add(mockClusterNodeInfo("a", "4.4.4.4", 3333));
        MockProvider provider1 = new MockProvider("test", false,
                new String[] { "a", "b", "c" });
        String params1 = "address=(type=master)(protocol=tcp)(host=3.3.3.3)(port=3333):3306," +
                "address=(type=master)(protocol=tcp)(host=4.4.4.4)(port=3333):3306," +
                "address=(type=master)(protocol=tcp)(host=2.2.2.2)(port=2222):3306," +
                "address=(type=master)(protocol=tcp)(host=1.1.1.1)(port=1111):3306";
        Assert.assertEquals(params1, provider1.getServerAffinityOrder(nodes));
        MockProvider provider2 = new MockProvider("test", true,
                new String[] { "a", "b", "c" });
        String params2 = "address=(type=master)(protocol=tcp)(host=2.2.2.2)(port=2222):3306," +
                "address=(type=master)(protocol=tcp)(host=3.3.3.3)(port=3333):3306," +
                "address=(type=master)(protocol=tcp)(host=4.4.4.4)(port=3333):3306," +
                "address=(type=master)(protocol=tcp)(host=1.1.1.1)(port=1111):3306";
        Assert.assertEquals(params2, provider2.getServerAffinityOrder(nodes));
        envUtils.setIdc(null);
    }

    @Test
    public void testGetMGRConfig() {
        Properties appProperties = new Properties();
        appProperties.setProperty(DataSourceConfigureConstants.DB_MODEL, "mgr");
        Properties dsProperties = new Properties();
        dsProperties.setProperty(DataSourceConfigureConstants.DB_TOKEN, "xyz");
        dsProperties.setProperty(DataSourceConfigureConstants.LOCAL_ACCESS, "true");
        dsProperties.setProperty(DataSourceConfigureConstants.IDC_PRIORITY, "c,b,a");
        MockProvider2 provider = new MockProvider2("test", appProperties, dsProperties);
        provider.initMysqlApiConfigure();
        Assert.assertEquals(DBModel.MGR, provider.dbModel);
        Assert.assertEquals( "xyz", provider.dbToken);
        Assert.assertTrue(provider.localAccess);
        Assert.assertEquals(3, provider.idcPriority.length);
        Assert.assertEquals("c", provider.idcPriority[0]);
        Assert.assertEquals("b", provider.idcPriority[1]);
        Assert.assertEquals("a", provider.idcPriority[2]);
    }

    private ClusterNodeInfo mockClusterNodeInfo(String idc, String ip, int port) {
        ClusterNodeInfo node = new ClusterNodeInfo();
        node.setMachine_located_short(idc);
        node.setIp_business(ip);
        node.setDns_port(port);
        node.setStatus("online");
        return node;
    }

    static class MockProvider extends MysqlApiConnectionStringConfigureProvider {
        final boolean localAccess;
        final String[] idcPriority;

        MockProvider(String dbName, boolean localAccess, String[] idcPriority) {
            super(dbName);
            this.localAccess = localAccess;
            this.idcPriority = idcPriority;
            setProperties();
        }

        @Override
        protected void initMysqlApiConfigure() {
            super.initMysqlApiConfigure();
            setProperties();
        }

        private void setProperties() {
            super.localAccess = localAccess;
            if (idcPriority != null)
                super.idcPriority = idcPriority;
        }
    }

    static class MockProvider2 extends MysqlApiConnectionStringConfigureProvider {
        final String dbName;
        final Properties appProperties;
        final Properties dsProperties;

        MockProvider2(String dbName, Properties appProperties, Properties dsProperties) {
            super(dbName);
            this.dbName = dbName;
            this.appProperties = appProperties;
            this.dsProperties = dsProperties;
        }

        @Override
        protected DalPoolPropertiesConfigure getMysqlApiConfigureProperties(PropertiesWrapper propertiesWrapper) {
            Map<String, Properties> dsPropertiesMap = new HashMap<>();
            dsPropertiesMap.put(dbName, dsProperties);
            PropertiesWrapper wrapper = new PropertiesWrapper(null, appProperties, dsPropertiesMap);
            return super.getMysqlApiConfigureProperties(wrapper);
        }
    }

}
