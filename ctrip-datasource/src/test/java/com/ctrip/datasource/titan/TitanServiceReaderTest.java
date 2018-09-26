package com.ctrip.datasource.titan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocatorManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

public class TitanServiceReaderTest {
    @Before
    public void setUp() throws Exception {
        DalClientFactory.shutdownFactory();
        TitanProvider provider = new TitanProvider();
        provider.clear();
    }

    @After
    public void tearDown() throws Exception {
        TitanProvider provider = new TitanProvider();
        provider.clear();
    }

    @Test
    public void testGetFromTitanServiceSuccess() {
        String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query/";
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("AbacusDB_INSERT_1");
        dbNames.add("CrawlerResultMDB");

        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.SERVICE_ADDRESS, fws);
        settings.put(DataSourceConfigureConstants.USE_LOCAL_CONFIG, "false");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            DataSourceConfigure result = null;

            result = provider.getDataSourceConfigure("AbacusDB_INSERT_1");
            Assert.assertNotNull(result);

            result = provider.getDataSourceConfigure("CrawlerResultMDB");
            Assert.assertNotNull(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testGetFromTitanServiceFail() {
        String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("AbacusDB_INSERT_1");
        dbNames.add("CrawlerResultMDB");
        dbNames.add("test");

        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.SERVICE_ADDRESS, fws);
        settings.put(DataSourceConfigureConstants.USE_LOCAL_CONFIG, "false");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetFromTitanServiceProd() {
        String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("test1");
        dbNames.add("test2");

        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.SERVICE_ADDRESS, fws);
        settings.put(DataSourceConfigureConstants.USE_LOCAL_CONFIG, "false");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);
            // Can not test from local environment
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetFromLocalConfigWitUsingSetting() {
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("SimpleShard_0");
        dbNames.add("SimpleShard_1");
        dbNames.add("dao_test_sqlsvr");
        dbNames.add("dao_test_mysql");

        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.USE_LOCAL_CONFIG, "true");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            DataSourceConfigure result = null;

            result = provider.getDataSourceConfigure("SimpleShard_0");
            Assert.assertNotNull(result);

            result = provider.getDataSourceConfigure("SimpleShard_1");
            Assert.assertNotNull(result);

            result = provider.getDataSourceConfigure("dao_test_sqlsvr");
            Assert.assertNotNull(result);

            result = provider.getDataSourceConfigure("dao_test_mysql");
            Assert.assertNotNull(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testGetFromLocalConfigWitUsingSettingFail() {
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("SimpleShard_0");
        dbNames.add("SimpleShard_1");
        dbNames.add("dao_test_sqlsvr");
        dbNames.add("dao_test_mysql");
        dbNames.add("test");

        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.USE_LOCAL_CONFIG, "true");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            Assert.fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFromLocalConfigWitUsingConfigVersionFlag() {
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("SimpleShard_0");
        dbNames.add("SimpleShard_1");
        dbNames.add("dao_test_sqlsvr");
        dbNames.add("dao_test_mysql");

        Map<String, String> settings = new HashMap<>();
        settings.put("useLocalConfig", "true");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            DataSourceConfigure result = null;

            result = provider.getDataSourceConfigure("SimpleShard_0");
            Assert.assertNotNull(result);

            result = provider.getDataSourceConfigure("SimpleShard_1");
            Assert.assertNotNull(result);

            result = provider.getDataSourceConfigure("dao_test_sqlsvr");
            Assert.assertNotNull(result);

            result = provider.getDataSourceConfigure("dao_test_mysql");
            Assert.assertNotNull(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testGetFromLocalConfigWitUsingConfigVersionFlagFail() {
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("SimpleShard_0");
        dbNames.add("test");

        Map<String, String> settings = new HashMap<>();
        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            Assert.fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFromTitanServiceUser() {
        String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("CommonOrderDB");

        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.SERVICE_ADDRESS, fws);
        settings.put(DataSourceConfigureConstants.USE_LOCAL_CONFIG, "false");
        settings.put(DataSourceConfigureConstants.TIMEOUT, "100");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            DataSourceConfigure result = null;

            result = provider.getDataSourceConfigure("CommonOrderDB");
            Assert.assertNotNull(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testGetFromTitanServiceName() {
        String name = "SecUGCdb_W";
        String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add(name);

        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.SERVICE_ADDRESS, fws);
        settings.put(DataSourceConfigureConstants.USE_LOCAL_CONFIG, "false");
        settings.put(DataSourceConfigureConstants.TIMEOUT, "100");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            DataSourceConfigure result = null;

            result = provider.getDataSourceConfigure(name);
            Assert.assertNotNull(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testGetFromUATTitanService() {
        String uat = "https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query";
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("GSCommunityDB_SELECT_1");
        dbNames.add("YouSearchDB");
        dbNames.add("GSDestDB_SELECT_1");

        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.SERVICE_ADDRESS, uat);
        settings.put(DataSourceConfigureConstants.USE_LOCAL_CONFIG, "false");
        settings.put(DataSourceConfigureConstants.TIMEOUT, "1000");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            DataSourceConfigure result = null;

            for (String name : dbNames) {
                result = provider.getDataSourceConfigure(name);
                Assert.assertNotNull(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testGetFromEnvironmentJvmTitanService() {
        // You need to rename c:/opt1 to c:/opt if you want to test server.properties
        // System.setProperty("env", "UAT");
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("GSCommunityDB_SELECT_1");
        dbNames.add("YouSearchDB");
        dbNames.add("GSDestDB_SELECT_1");

        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.USE_LOCAL_CONFIG, "false");
        settings.put(DataSourceConfigureConstants.TIMEOUT, "1000");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            DataSourceConfigure result = null;

            for (String name : dbNames) {
                result = provider.getDataSourceConfigure(name);
                Assert.assertNotNull(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(Foundation.server().getEnvType());
            Assert.fail();
        }
    }

    @Test
    public void testSubEnvironmentService() {
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("ActProductDB_R");
        dbNames.add("mysqldbatestshard02db_w");

        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.USE_LOCAL_CONFIG, "false");
        settings.put(DataSourceConfigureConstants.TIMEOUT, "1000");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            DataSourceConfigure result = null;

            for (String name : dbNames) {
                result = provider.getDataSourceConfigure(name);
                Assert.assertNotNull(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(Foundation.server().getEnvType());
            Assert.fail();
        }
    }

    @Test
    public void testCheckDatasource() {
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("SimpleShard_0");// has config
        dbNames.add("SimpleShard_0_SH");// has no config, but name may be match

        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.USE_LOCAL_CONFIG, "true");
        settings.put(DataSourceConfigureConstants.TIMEOUT, "1000");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);
            // Assert.assertTrue(DataSourceConfigureLocator.getInstance().contains("SimpleShard_0"));
            // Assert.assertTrue(DataSourceConfigureLocator.getInstance().contains("SimpleShard_0_SH"));
            Assert.assertTrue(
                    DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure("SimpleShard_0") != null);
            Assert.assertTrue(
                    DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure("SimpleShard_0_SH") != null);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(Foundation.server().getEnvType());
            Assert.fail();
        }
    }

    @Test
    public void testCheckDatasourceDefault() {
        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("ha_test");// has no config

        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.USE_LOCAL_CONFIG, "true");
        settings.put(DataSourceConfigureConstants.TIMEOUT, "1000");
        try {
            provider.initialize(settings);
            provider.setup(dbNames);
            // Assert.assertFalse(DataSourceConfigureLocator.getInstance().contains("Not_Exist"));
            // Assert.assertTrue(DataSourceConfigureLocator.getInstance().contains("ha_test"));
            Assert.assertFalse(
                    DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure("Not_Exist") != null);
            Assert.assertTrue(
                    DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure("ha_test") != null);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(Foundation.server().getEnvType());
            Assert.fail();
        }
    }

    @Test
    public void testGetDatasourceWithMixedNames() {
        String fws = "https://ws.titan.ctripcorp.com/titanservice/query";

        TitanProvider provider = new TitanProvider();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("mysqldbatestshard01db_W_SH");
        dbNames.add("mysqldbatestshard01db_R");

        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.TIMEOUT, "1000");
        settings.put(DataSourceConfigureConstants.SERVICE_ADDRESS, fws);
        settings.put("isDebug", "true");

        try {
            provider.initialize(settings);
            provider.setup(dbNames);

            DataSourceConfigure result = null;

            for (String name : dbNames) {
                result = provider.getDataSourceConfigure(name);
                Assert.assertNotNull(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(Foundation.server().getEnvType());
            Assert.fail();
        }
    }

}
