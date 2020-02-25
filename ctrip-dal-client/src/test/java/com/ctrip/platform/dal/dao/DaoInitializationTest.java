package com.ctrip.platform.dal.dao;

import com.ctrip.datasource.configure.qconfig.PoolPropertiesProviderImpl;
import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.framework.vi.IgniteManager;
import com.ctrip.platform.dal.dao.configure.FailedQConfigIPDomainStatusProvider;
import com.ctrip.platform.dal.dao.configure.FailedQConfigPoolPropertiesProvider;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import com.ctrip.platform.dal.dao.vi.DalIgnite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
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
        String dbName1 = "qconfig";
        String dbName2 = "kevin";
        DataSourceConfigureManager.getInstance().setVariableConnectionStringProvider(new MockCtripVariableDataSourceConfigureProvider());
        String path = DaoInitializationTest.class.getClassLoader().getResource("Dal.config.mgr").getPath();
        DalClientFactory.shutdownFactory();
        DalClientFactory.initClientFactory(path);
        DataSourceLocator locator = new DataSourceLocator();
        DataSource ds1 = locator.getDataSource(dbName1);
        Assert.assertNotNull(ds1);
        DataSource ds2 = locator.getDataSource(dbName2);
        Assert.assertNotNull(ds2);
    }
}
