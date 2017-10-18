package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConnectionStringTest {
    private static DRTestDao dao = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        /**
         * Initialize DalClientFactory. The Dal.config can be specified from class-path or local file path. One of
         * follow three need to be enabled.
         **/
        DalClientFactory.initClientFactory(); // load from class-path Dal.config
        DalClientFactory.warmUpConnections();
        // client = DalClientFactory.getClient(DATA_BASE);
        dao = new DRTestDao();
    }

    @Test
    public void testConnectionStringVersion() throws Exception {
        dao.selectDatabase(null);

        String name = "mysqldaltest01db_W";
        DataSourceConfigure config = DataSourceConfigureLocator.getInstance().getDataSourceConfigure(name);
        String connectionProperties = config.getProperty(DataSourceConfigureConstants.CONNECTIONPROPERTIES);
        String version = config.getVersion();

        System.out.println("*****Ready to sleep 30 seconds...*****");
        Thread.sleep(30 * 1000);
        System.out.println("*****Waked up.*****");

        DataSourceConfigure config1 = DataSourceConfigureLocator.getInstance().getDataSourceConfigure(name);
        String connectionProperties1 = config1.getProperty(DataSourceConfigureConstants.CONNECTIONPROPERTIES);
        String version1 = config1.getVersion();

        if (version != null && version1 != null) {
            Assert.assertEquals(version, version1);
        }

        Assert.assertNotEquals(connectionProperties, connectionProperties1);
    }

}
