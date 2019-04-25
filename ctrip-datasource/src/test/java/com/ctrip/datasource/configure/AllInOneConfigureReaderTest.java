package com.ctrip.datasource.configure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.dao.configure.DalConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import org.junit.Assert;
import org.junit.Test;

public class AllInOneConfigureReaderTest {
    private static final String LINUX_DB_CONFIG_FILE = "/opt/ctrip/AppData/";
    private static final String WIN_DB_CONFIG_FILE = "/D:/WebSites/CtripAppData/";
    @Test
    public void testGetDataSourceConfiguresSuccess() {
        AllInOneConfigureReader reader = new AllInOneConfigureReader();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("simpleshard_0");
        dbNames.add("simpleshard_1");
        dbNames.add("dao_test_sqlsvr");
        dbNames.add("dao_test_mysql");
        dbNames.add("paybasedb_insert_2");
        try {
            reader.getConnectionStrings(dbNames, true, "$classpath");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testGetDataSourceConfiguresValidateFail() {
        AllInOneConfigureReader reader = new AllInOneConfigureReader();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("SimpleShard_0");
        dbNames.add("SimpleShard_1");
        dbNames.add("dao_test_sqlsvr");
        dbNames.add("dao_test_mysql");
        dbNames.add("test");// not exist in database.config
        try {
            reader.getConnectionStrings(dbNames, true, "$classpath");
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetAllInOneConfig() throws Exception {
        TitanProvider provider = new TitanProvider();
        Map<String, String> settings = new HashMap<>();
        settings.put(DataSourceConfigureConstants.USE_LOCAL_CONFIG, "true");
        settings.put(DataSourceConfigureConstants.DATABASE_CONFIG_LOCATION, "$classpath");
        provider.initialize(settings);
        Set<String> names = new HashSet<>();
        names.add("SimpleShard_0");
        provider.setup(names);
    }

    @Test
    public void testGetConfigFromFrameworkConfigInDEVMode() throws Exception {
        AllInOneConfigureReader reader = new AllInOneConfigureReader();
        Set<String> dbNames = new HashSet<>();
        dbNames.add("test12345678");
        try {
            Map<String, DalConnectionString> ret=reader.getConnectionStrings(dbNames, true, null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Cannot load config for the following DB:"));
            //database.config not exist in OS path
//            String osName=null;
//            try {
//                osName = System.getProperty("os.name");
//            } catch (SecurityException ex) {
//
//            }
//            if(osName!=null) {
//                if (osName.startsWith("Windows"))
//                    Assert.assertTrue(e.getMessage().contains(WIN_DB_CONFIG_FILE));
//                else
//                    Assert.assertTrue(e.getMessage().contains(LINUX_DB_CONFIG_FILE));
//            }
        }
    }

    @Test
    public void testGetConfigFromLocalConfigureProviderSuccess() throws Exception {
        AllInOneConfigureReader reader = new AllInOneConfigureReader(new MockNormalLocalConfigureProvider());
        Set<String> dbNames = new HashSet<>();
        dbNames.add("simpleshard_0");
        try {
            reader.getConnectionStrings(dbNames, true, null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testGetConfigFromLocalConfigureProviderFileNotFound() throws Exception {
        AllInOneConfigureReader reader = new AllInOneConfigureReader(new MockFileNotFoundLocalConfigureProvider());
        Set<String> dbNames = new HashSet<>();
        dbNames.add("test12345678");
        try {
            Map<String, DalConnectionString> config=reader.getConnectionStrings(dbNames, true, null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Cannot load config for the following DB:"));
            //database.config not exist in OS path
//            String osName=null;
//            try {
//                osName = System.getProperty("os.name");
//            } catch (SecurityException ex) {
//
//            }
//            if(osName!=null) {
//                if (osName.startsWith("Windows"))
//                    Assert.assertTrue(e.getMessage().contains(WIN_DB_CONFIG_FILE));
//                else
//                    Assert.assertTrue(e.getMessage().contains(LINUX_DB_CONFIG_FILE));
//            }
        }
    }

    @Test
    public void testGetConfigFromLocalConfigureProviderOtherException() throws Exception {
        AllInOneConfigureReader reader = new AllInOneConfigureReader(new MockOtherExceptionLocalConfigureProvider());
        Set<String> dbNames = new HashSet<>();
        dbNames.add("simpleshard_0");
        try {
            reader.getConnectionStrings(dbNames, true, null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("other exception"));
        }
    }
}
