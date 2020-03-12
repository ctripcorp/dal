package com.ctrip.platform.dal.dao;

import com.ctrip.datasource.configure.MysqlApiConnectionStringConfigureProvider;
import com.ctrip.datasource.configure.qconfig.PoolPropertiesProviderImpl;
import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.framework.vi.IgniteManager;
import com.ctrip.platform.dal.dao.configure.FailedQConfigIPDomainStatusProvider;
import com.ctrip.platform.dal.dao.configure.FailedQConfigPoolPropertiesProvider;
import com.ctrip.platform.dal.dao.datasource.ApiDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import com.ctrip.platform.dal.dao.vi.DalIgnite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by taochen on 2019/8/28.
 */
public class DaoInitializationTest {
    private static final String DB_NAME = "dao_test";

    @BeforeClass
    public static void beforeClass() throws Exception {
        DataSourceConfigureManager.getInstance().initialize(new HashMap<>());
    }

    @Before
    public void beforeTest() {
        DataSourceConfigureManager.getInstance().clear();
        DalClientFactory.shutdownFactory();
    }

    @Test
    public void testCreateDaoAfterFailedGetPoolProperties() throws Exception {
        DataSourceConfigureManager.getInstance().setPoolPropertiesProvider(new FailedQConfigPoolPropertiesProvider());
        Map<String, String> settings = new HashMap<>();
        settings.put("dataSourceConfigureProvider", "com.ctrip.datasource.titan.TitanProvider");
        settings.put("ignoreExternalException", "true");
        DataSourceConfigureManager.getInstance().initialize(settings);
        try {
            DalTableDao<LoginUser> client = new DalTableDao<LoginUser>(LoginUser.class);
        }  catch (Exception e) {
            Assert.fail();
        }

        try {
            DalQueryDao queryClient = new DalQueryDao(DB_NAME);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCreateDaoAfterFailedGetPoolProperties2() throws Exception {
        DataSourceConfigureManager.getInstance().setPoolPropertiesProvider(new FailedQConfigPoolPropertiesProvider());
        Map<String, String> settings = new HashMap<>();
        settings.put("dataSourceConfigureProvider", "com.ctrip.datasource.titan.TitanProvider");
        settings.put("ignoreExternalException", "false");
        DataSourceConfigureManager.getInstance().initialize(settings);
        try {
            DalTableDao<LoginUser> client = new DalTableDao<LoginUser>(LoginUser.class);
            Assert.fail();
        }  catch (Exception e) {

        }

        try {
            DalQueryDao queryClient = new DalQueryDao(DB_NAME);
            Assert.fail();
        } catch (Exception e) {

        }
    }

    @Test
    public void testCreateDaoAfterFailedGetIpDomain() throws Exception {
        DataSourceConfigureManager.getInstance().setIPDomainStatusProvider(new FailedQConfigIPDomainStatusProvider());
        Map<String, String> settings = new HashMap<>();
        settings.put("dataSourceConfigureProvider", "com.ctrip.datasource.titan.TitanProvider");
        settings.put("ignoreExternalException", "true");
        DataSourceConfigureManager.getInstance().initialize(settings);
        try {
            DalTableDao<LoginUser> client = new DalTableDao<LoginUser>(LoginUser.class);
        }  catch (Exception e) {
            Assert.fail();
        }

        try {
            DalQueryDao queryClient = new DalQueryDao(DB_NAME);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCreateDaoAfterFailedGetIpDomain2() throws Exception {
        DataSourceConfigureManager.getInstance().setIPDomainStatusProvider(new FailedQConfigIPDomainStatusProvider());
        Map<String, String> settings = new HashMap<>();
        settings.put("dataSourceConfigureProvider", "com.ctrip.datasource.titan.TitanProvider");
        settings.put("ignoreExternalException", "false");
        DataSourceConfigureManager.getInstance().initialize(settings);
        try {
            DalTableDao<LoginUser> client = new DalTableDao<LoginUser>(LoginUser.class);
            Assert.fail();
        }  catch (Exception e) {

        }

        try {
            DalQueryDao queryClient = new DalQueryDao(DB_NAME);
            Assert.fail();
        } catch (Exception e) {

        }
    }

    @Test
    public void testDalIgniteAfterFailedGetPoolProperties() throws Exception {
        DataSourceConfigureManager.getInstance().setPoolPropertiesProvider(new FailedQConfigPoolPropertiesProvider());
        Map<String, String> settings = new HashMap<>();
        settings.put("dataSourceConfigureProvider", "com.ctrip.datasource.titan.TitanProvider");
        settings.put("ignoreExternalException", "true");
        DataSourceConfigureManager.getInstance().initialize(settings);

        DalIgnite ignite = new DalIgnite();
        boolean status = ignite.warmUP(new IgniteManager.SimpleLogger());
        assertFalse(status);
    }

    // need remove all databaseSet in Dal.config
    @Test
    public void testDalIgnitePoolPropertiesIsNullWhenDataSetIsNull() throws Exception {
        DataSourceConfigureManager.getInstance().setPoolPropertiesProvider(new PoolPropertiesProviderImpl());
        String path = DaoInitializationTest.class.getClassLoader().getResource("Dal.config.ignite").getPath();
        DalClientFactory.shutdownFactory();
        DalClientFactory.initClientFactory(path);
        DalIgnite ignite = new DalIgnite();
        boolean status = ignite.warmUP(new IgniteManager.SimpleLogger());
        assertTrue(status);
        DalClientFactory.shutdownFactory();
    }

    //support mgr in dal.config
    @Test
    public void testMGRInDALConfig() throws Exception {
        String mgrUrl1 = "jdbc:mysql://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306):3306:3306/";
        String mgrUrl2 = "jdbc:mysql://address=(type=master)(protocol=tcp)(host=10.2.7.187)(port=3306):3306:3306/";
        String mgrUrl3 = "jdbc:mysql://address=(type=master)(protocol=tcp)(host=10.2.7.184)(port=3306):3306:3306/";
        String standaloneUrl = "jdbc:mysql://qconfig.mysql.db.fat.qa.nt.ctripcorp.com:55111/qconfig?useUnicode=true&characterEncoding=UTF-8";
        String normalUrl = "jdbc:mysql://fxdaltest.mysql.db.fat.qa.nt.ctripcorp.com:55111/fxdaltestdb?useUnicode=true&characterEncoding=UTF-8";

        String dbName1 = "qconfig";
        String dbName2 = "kevin";
        String dbName3 = "fxdaltestdb_w";
        String path = DaoInitializationTest.class.getClassLoader().getResource("Dal.config.mgr").getPath();
        DalClientFactory.shutdownFactory();
        DalClientFactory.initClientFactory(path);
        DataSourceLocator locator = new DataSourceLocator(new TitanProvider());
        DataSource ds1 = locator.getDataSource(new ApiDataSourceIdentity(new MysqlApiConnectionStringConfigureProvider(dbName1)));
        Assert.assertNotNull(ds1);

        DataSource ds2 = locator.getDataSource(new ApiDataSourceIdentity(new MockConnectionStringConfigureProvider()));
        Assert.assertNotNull(ds2);

        DataSource ds3 = locator.getDataSource(dbName3);
        Assert.assertNotNull(ds3);

        DatabaseMetaData metaData1 = ds1.getConnection().getMetaData();
        String url1 = metaData1.getURL();
        Assert.assertTrue(standaloneUrl.equalsIgnoreCase(url1));

        DatabaseMetaData metaData2 = ds2.getConnection().getMetaData();
        String url2 = metaData2.getURL();
        Assert.assertTrue(mgrUrl1.equalsIgnoreCase(url2) || mgrUrl2.equalsIgnoreCase(url2) || mgrUrl3.equalsIgnoreCase(url2));

        DatabaseMetaData metaData3 = ds3.getConnection().getMetaData();
        String url3 = metaData3.getURL();
        Assert.assertTrue(normalUrl.equalsIgnoreCase(url3));

        DalClientFactory.shutdownFactory();
    }
}
