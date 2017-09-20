package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.datasource.configure.DataSourceConfigureProcessor;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureHolder;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import org.junit.Test;

public class DynamicDataSourcePoolSettingsTest {
    @Test
    public void testDynamicDataSourcePoolSettings() throws InterruptedException {
        String name = "dalservice2db_w";

        // get user datasource.xml settings
        DataSourceConfigure config = DataSourceConfigureHolder.getInstance().getDataSourceConfigure(name);
        int maxActiveBefore = config.getIntProperty(DataSourceConfigureConstants.MAXACTIVE, -1);
        // int minIdleBefore = config.getIntProperty(DataSourceConfigureConstants.MINIDLE, -1);
        System.out.println(String.format("***** maxActive before:%s *****", maxActiveBefore));
        // System.out.println(String.format("***** minIdle before:%s *****", minIdleBefore));
        System.out.println("*****Ready to sleep 30 seconds...*****");
        Thread.sleep(30 * 1000);
        System.out.println("*****Waked up.*****");

        // **********change maxActive to 200**********

        DataSourceConfigure config1 = DataSourceConfigureHolder.getInstance().getDataSourceConfigure(name);
        config1 = DataSourceConfigureProcessor.getInstance().getDataSourceConfigure(config1);
        int maxActiveAfter = config1.getIntProperty(DataSourceConfigureConstants.MAXACTIVE, -1);
        // int minIdleAfter = config1.getIntProperty(DataSourceConfigureConstants.MINIDLE, -1);
        System.out.println(String.format("***** maxActive after:%s *****", maxActiveAfter));
        // System.out.println(String.format("***** minIdle after:%s *****", minIdleAfter));
    }
}
