package com.ctrip.platform.dal.dao.datasource.tomcat;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.Test;

import java.util.Properties;

public class TomcatPoolPropertiesTest {

//    @Test
    public void doTest() throws Exception {
        Properties p1 = new Properties();
        p1.setProperty("userName", "root");
        p1.setProperty("password", "root");
        p1.setProperty("connectionUrl", "jdbc:mysql://10.5.119.91:3308/dalservice?useUnicode=true&characterEncoding=UTF-8;");
        p1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        DataSourceConfigure config = new DataSourceConfigure("c1", p1);
        PoolProperties props = PoolPropertiesHelper.getInstance().convert(config);
//        props.setInitialSize(0);
        props.setMinIdle(0);
//        props.setTestWhileIdle(true);
        props.setMinEvictableIdleTimeMillis(5000);
        props.setTimeBetweenEvictionRunsMillis(100);
        DalTomcatDataSource ds = new DalTomcatDataSource(props);
        ds.createPool();
        long start = System.currentTimeMillis();
        System.out.println("----- START -----");
        System.out.println(String.format("TestWhileIdle: %s", ds.isTestWhileIdle()));
        System.out.println(String.format("MinIdle: %s", ds.getMinIdle()));
        System.out.println(String.format("MinEvictableIdleTimeMillis: %s", ds.getMinEvictableIdleTimeMillis()));
        System.out.println(String.format("TimeBetweenEvictionRunsMillis: %s", ds.getTimeBetweenEvictionRunsMillis()));
        System.out.println(String.format("InitialSize: %s", ds.getInitialSize()));
        while (true) {
            long now = System.currentTimeMillis();
            System.out.println(String.format("--- Elapsed: %d ms ---", now - start));
            System.out.println(String.format("Size: %s", ds.getSize()));
            System.out.println(String.format("Idle: %s", ds.getIdle()));
            System.out.println(String.format("Active: %s", ds.getActive()));
            if (ds.getSize() == 0) break;
            Thread.sleep(1000);
        }
        System.out.println("----- END -----");
    }

}
