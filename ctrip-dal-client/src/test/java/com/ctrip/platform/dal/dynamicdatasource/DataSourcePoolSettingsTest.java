package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataSourcePoolSettingsTest {
    private static final String databaseName = "mysqldaltest01db_w";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testDataSourcePoolSettings() throws Exception {
        DataSourceConfigure configure1 = getDefaultDataSourceConfigure();
        DataSourceConfigureLocator.getInstance().addDataSourceConfigure(databaseName, configure1);

        // mock QConfig modifying
        modifyDataSourceConfigure(configure1);
        DataSourceConfigure configure2 = DataSourceConfigureLocator.getInstance().getDataSourceConfigure(databaseName);

        boolean testWhileIdle = configure2.getBooleanProperty(DataSourceConfigureConstants.TESTWHILEIDLE,
                DataSourceConfigureConstants.DEFAULT_TESTWHILEIDLE);
        Assert.assertNotEquals(testWhileIdle, DataSourceConfigureConstants.DEFAULT_TESTWHILEIDLE);

        boolean testOnBorrow = configure2.getBooleanProperty(DataSourceConfigureConstants.TESTONBORROW,
                DataSourceConfigureConstants.DEFAULT_TESTONBORROW);
        Assert.assertNotEquals(testOnBorrow, DataSourceConfigureConstants.DEFAULT_TESTONBORROW);

        boolean testOnReturn = configure2.getBooleanProperty(DataSourceConfigureConstants.TESTONRETURN,
                DataSourceConfigureConstants.DEFAULT_TESTONRETURN);
        Assert.assertNotEquals(testOnReturn, DataSourceConfigureConstants.DEFAULT_TESTONRETURN);

        String validationQuery = configure2.getProperty(DataSourceConfigureConstants.VALIDATIONQUERY);
        Assert.assertEquals(validationQuery, "");

        int validationQueryTimeout = configure2.getIntProperty(DataSourceConfigureConstants.VALIDATIONQUERYTIMEOUT,
                DataSourceConfigureConstants.DEFAULT_VALIDATIONQUERYTIMEOUT);
        Assert.assertNotEquals(validationQueryTimeout, DataSourceConfigureConstants.DEFAULT_VALIDATIONQUERYTIMEOUT);

        long validationInterval = configure2.getLongProperty(DataSourceConfigureConstants.VALIDATIONINTERVAL,
                DataSourceConfigureConstants.DEFAULT_VALIDATIONINTERVAL);
        Assert.assertNotEquals(validationInterval, DataSourceConfigureConstants.DEFAULT_VALIDATIONINTERVAL);

        String validatorClassName = configure2.getProperty(DataSourceConfigureConstants.VALIDATORCLASSNAME);
        Assert.assertEquals(validatorClassName, "");

        int timeBetweenEvictionRunsMillis =
                configure2.getIntProperty(DataSourceConfigureConstants.TIMEBETWEENEVICTIONRUNSMILLIS,
                        DataSourceConfigureConstants.DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS);
        Assert.assertNotEquals(timeBetweenEvictionRunsMillis,
                DataSourceConfigureConstants.DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS);

        int maxAge = configure2.getIntProperty(DataSourceConfigureConstants.MAX_AGE,
                DataSourceConfigureConstants.DEFAULT_MAXAGE);
        Assert.assertNotEquals(maxAge, DataSourceConfigureConstants.DEFAULT_MAXAGE);

        int maxActive = configure2.getIntProperty(DataSourceConfigureConstants.MAXACTIVE,
                DataSourceConfigureConstants.DEFAULT_MAXACTIVE);
        Assert.assertNotEquals(maxActive, DataSourceConfigureConstants.DEFAULT_MAXACTIVE);

        int minIdle = configure2.getIntProperty(DataSourceConfigureConstants.MINIDLE,
                DataSourceConfigureConstants.DEFAULT_MINIDLE);
        Assert.assertNotEquals(minIdle, DataSourceConfigureConstants.DEFAULT_MINIDLE);

        int maxWait = configure2.getIntProperty(DataSourceConfigureConstants.MAXWAIT,
                DataSourceConfigureConstants.DEFAULT_MAXWAIT);
        Assert.assertNotEquals(maxWait, DataSourceConfigureConstants.DEFAULT_MAXWAIT);

        int initialSize = configure2.getIntProperty(DataSourceConfigureConstants.INITIALSIZE,
                DataSourceConfigureConstants.DEFAULT_INITIALSIZE);
        Assert.assertNotEquals(initialSize, DataSourceConfigureConstants.DEFAULT_INITIALSIZE);

        int removeAbandonedTimeout = configure2.getIntProperty(DataSourceConfigureConstants.REMOVEABANDONEDTIMEOUT,
                DataSourceConfigureConstants.DEFAULT_REMOVEABANDONEDTIMEOUT);
        Assert.assertNotEquals(removeAbandonedTimeout, DataSourceConfigureConstants.DEFAULT_REMOVEABANDONEDTIMEOUT);

        boolean removeAbandoned = configure2.getBooleanProperty(DataSourceConfigureConstants.REMOVEABANDONED,
                DataSourceConfigureConstants.DEFAULT_REMOVEABANDONED);
        Assert.assertNotEquals(removeAbandoned, DataSourceConfigureConstants.DEFAULT_REMOVEABANDONED);

        boolean logAbandoned = configure2.getBooleanProperty(DataSourceConfigureConstants.LOGABANDONED,
                DataSourceConfigureConstants.DEFAULT_LOGABANDONED);
        Assert.assertNotEquals(logAbandoned, DataSourceConfigureConstants.DEFAULT_LOGABANDONED);

        int minEvictableIdleTimeMillis =
                configure2.getIntProperty(DataSourceConfigureConstants.MINEVICTABLEIDLETIMEMILLIS,
                        DataSourceConfigureConstants.DEFAULT_MINEVICTABLEIDLETIMEMILLIS);
        Assert.assertNotEquals(minEvictableIdleTimeMillis,
                DataSourceConfigureConstants.DEFAULT_MINEVICTABLEIDLETIMEMILLIS);

        String connectionProperties = configure2.getProperty(DataSourceConfigureConstants.CONNECTIONPROPERTIES);
        Assert.assertEquals(connectionProperties, "");

        String initSql = configure2.getProperty(DataSourceConfigureConstants.INIT_SQL);
        Assert.assertEquals(initSql, "");
    }

    private DataSourceConfigure getDefaultDataSourceConfigure() {
        DataSourceConfigure configure = new DataSourceConfigure();
        configure.setProperty(DataSourceConfigureConstants.TESTWHILEIDLE, "false");
        configure.setProperty(DataSourceConfigureConstants.TESTONBORROW, "true");
        configure.setProperty(DataSourceConfigureConstants.TESTONRETURN, "false");
        configure.setProperty(DataSourceConfigureConstants.VALIDATIONQUERY, "SELECT 1");
        configure.setProperty(DataSourceConfigureConstants.VALIDATIONQUERYTIMEOUT, "5");
        configure.setProperty(DataSourceConfigureConstants.VALIDATIONINTERVAL, "30000");
        configure.setProperty(DataSourceConfigureConstants.VALIDATORCLASSNAME,
                "com.ctrip.platform.dal.dao.datasource.DataSourceValidator");
        configure.setProperty(DataSourceConfigureConstants.TIMEBETWEENEVICTIONRUNSMILLIS, "5000");
        configure.setProperty(DataSourceConfigureConstants.MAX_AGE, "28000000");
        configure.setProperty(DataSourceConfigureConstants.MAXACTIVE, "100");
        configure.setProperty(DataSourceConfigureConstants.MINIDLE, "0");
        configure.setProperty(DataSourceConfigureConstants.MAXWAIT, "10000");
        configure.setProperty(DataSourceConfigureConstants.INITIALSIZE, "1");
        configure.setProperty(DataSourceConfigureConstants.REMOVEABANDONEDTIMEOUT, "65");
        configure.setProperty(DataSourceConfigureConstants.REMOVEABANDONED, "true");
        configure.setProperty(DataSourceConfigureConstants.LOGABANDONED, "false");
        configure.setProperty(DataSourceConfigureConstants.MINEVICTABLEIDLETIMEMILLIS, "30000");
        configure.setProperty(DataSourceConfigureConstants.CONNECTIONPROPERTIES, "");
        configure.setProperty(DataSourceConfigureConstants.INIT_SQL, "");
        configure.setProperty(DataSourceConfigureConstants.INIT_SQL2, "");
        return configure;
    }

    private void modifyDataSourceConfigure(DataSourceConfigure configure) {
        if (configure == null) {
            return;
        }

        configure.setProperty(DataSourceConfigureConstants.TESTWHILEIDLE, "true");
        configure.setProperty(DataSourceConfigureConstants.TESTONBORROW, "false");
        configure.setProperty(DataSourceConfigureConstants.TESTONRETURN, "true");
        configure.setProperty(DataSourceConfigureConstants.VALIDATIONQUERY, "");
        configure.setProperty(DataSourceConfigureConstants.VALIDATIONQUERYTIMEOUT, "10");
        configure.setProperty(DataSourceConfigureConstants.VALIDATIONINTERVAL, "60000");
        configure.setProperty(DataSourceConfigureConstants.VALIDATORCLASSNAME, "");
        configure.setProperty(DataSourceConfigureConstants.TIMEBETWEENEVICTIONRUNSMILLIS, "10000");
        configure.setProperty(DataSourceConfigureConstants.MAX_AGE, "56000000");
        configure.setProperty(DataSourceConfigureConstants.MAXACTIVE, "200");
        configure.setProperty(DataSourceConfigureConstants.MINIDLE, "1");
        configure.setProperty(DataSourceConfigureConstants.MAXWAIT, "20000");
        configure.setProperty(DataSourceConfigureConstants.INITIALSIZE, "2");
        configure.setProperty(DataSourceConfigureConstants.REMOVEABANDONEDTIMEOUT, "130");
        configure.setProperty(DataSourceConfigureConstants.REMOVEABANDONED, "false");
        configure.setProperty(DataSourceConfigureConstants.LOGABANDONED, "true");
        configure.setProperty(DataSourceConfigureConstants.MINEVICTABLEIDLETIMEMILLIS, "60000");
        configure.setProperty(DataSourceConfigureConstants.CONNECTIONPROPERTIES, "");
        configure.setProperty(DataSourceConfigureConstants.INIT_SQL, "");
        configure.setProperty(DataSourceConfigureConstants.INIT_SQL2, "");
    }

}
