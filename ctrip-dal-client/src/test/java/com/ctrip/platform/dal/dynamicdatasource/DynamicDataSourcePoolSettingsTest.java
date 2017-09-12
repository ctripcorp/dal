package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.datasource.configure.DataSourceConfigureProcessor;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import org.junit.Test;

public class DynamicDataSourcePoolSettingsTest {
    @Test
    public void testDynamicDataSourcePoolSettings() throws InterruptedException {
        String name = "dalservice2db_w";

        // get user datasource.xml settings
        DataSourceConfigure config = DataSourceConfigureParser.getInstance().getDataSourceConfigure(name);

        config = DataSourceConfigureProcessor.getInstance().getDataSourceConfigure(config);
        DataSourceConfigureParser.getInstance().addDataSourceConfigure(name, config);
        int maxActiveBefore = config.getIntProperty(DataSourceConfigureConstants.MAXACTIVE, 0);
        System.out.println(String.format("***** maxActive before:%s *****", maxActiveBefore));
        System.out.println("*****Ready to sleep 300 seconds...*****");
        Thread.sleep(300 * 1000);
        System.out.println("*****Waked up.*****");

        // **********change maxActive to 200**********

        DataSourceConfigure config1 = DataSourceConfigureParser.getInstance().getDataSourceConfigure(name);
        config1 = DataSourceConfigureProcessor.getInstance().getDataSourceConfigure(config1);
        int maxActiveAfter = config1.getIntProperty(DataSourceConfigureConstants.MAXACTIVE, 0);
        System.out.println(String.format("***** maxActive after:%s *****", maxActiveAfter));

    }
}
