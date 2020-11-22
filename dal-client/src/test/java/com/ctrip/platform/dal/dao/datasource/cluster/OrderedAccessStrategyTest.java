package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OrderedAccessStrategyTest {

    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private SQLThread selectSQLThread;
    private SQLThread updateSQLThread;

    @Test
    public void testNormal() throws Exception {
        MultiHostDataSource dataSource = new MockMultiHostDataSource(mockDataSourceConfigs(),
                mockClusterProperties(), "zone1");

//
//        selectSQLThread = new SQLThread(40, dataSource) {
//            @Override
//            void execute(Statement statement) throws SQLException {
//                statement.executeQuery("select * from student limit 1;");
//            }
//        };
//
//        updateSQLThread = new SQLThread(40, dataSource) {
//            @Override
//            void execute(Statement statement) throws SQLException {
//                statement.executeQuery("update student set name = 'test' where id = 1");
//            }
//        };
//
//        executor.submit(selectSQLThread);
//        executor.submit(updateSQLThread);


        while (true) {
//            executor.submit(() -> {
//                try (Connection connection = dataSource.getConnection()) {
//                    System.out.println("return " + connection.getMetaData().getURL() + " time is :" + new Date().toString());
//                } catch (SQLException e) {
//                    System.out.println("error time is :" + new Date().toString());
//                    e.printStackTrace();
//                }
//            });

            executor.submit(() -> {
                try (Connection connection = dataSource.getConnection()) {
                    System.out.println("return " + connection.getMetaData().getURL() + " time is :" + new Date().toString());
                    try (Statement statement = connection.createStatement()){
                        statement.execute("select * from student limit 1;");
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.out.println("error time is :" + new Date().toString());
                    e.printStackTrace();
                }

                try (Connection connection = dataSource.getConnection()) {
                    System.out.println("return " + connection.getMetaData().getURL() + " time is :" + new Date().toString());
                    try (Statement statement = connection.createStatement()){
                        statement.execute("update student set name = 'test' where id = 1");
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.out.println("error time is :" + new Date().toString());
                    e.printStackTrace();
                }
            });

            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

    private Map<HostSpec, DataSourceConfigure> mockDataSourceConfigs() {
        Map<HostSpec, DataSourceConfigure> dataSourceConfigs = new HashMap<>();
        HostSpec host1 = HostSpec.of("10.32.20.12", 3306, "zone1");
        dataSourceConfigs.put(host1, mockDataSourceConfig(host1));
        HostSpec host2 = HostSpec.of("10.32.20.12", 3307, "zone2");
        dataSourceConfigs.put(host2, mockDataSourceConfig(host2));
        HostSpec host3 = HostSpec.of("10.32.20.12", 3308, "zone3");
        dataSourceConfigs.put(host3, mockDataSourceConfig(host3));
        return dataSourceConfigs;
    }

    private DataSourceConfigure mockDataSourceConfig(HostSpec host) {
        DataSourceConfigure config = new DataSourceConfigure(host.toString());
        config.setDriverClass("com.mysql.jdbc.Driver");
        config.setConnectionUrl(String.format("jdbc:mysql://%s:%d/mytest", host.host(), host.port()));
        config.setUserName("rpl_user");
        config.setPassword("123456");
        config.setProperty(DataSourceConfigureConstants.VALIDATIONINTERVAL, "30000");
        return config;
    }

    private MultiHostClusterProperties mockClusterProperties() {
        return new MultiHostClusterProperties() {
            @Override
            public String routeStrategyName() {
                return "OrderedAccessStrategy";
            }

            @Override
            public CaseInsensitiveProperties routeStrategyProperties() {
                Properties properties = new Properties();
                properties.put("failoverTimeMS", "10000");
                properties.put("blacklistTimeoutMS", "10000");
                properties.put("fixedValidatePeriodMS", "30000");

                properties.put("ZonesPriority", "zone1,zone2,zone3");
                return new CaseInsensitiveProperties(properties);
            }
        };
    }



    private static abstract class SQLThread extends Thread {
        public volatile boolean exit = false;
        private final long delay;
        private DataSource dataSource;

        public SQLThread(long delay, DataSource dataSource) {
            this.delay = delay;
            this.dataSource = dataSource;
        }

        @Override
        public void run() {
            while (!exit) {
                try (Connection connection = getConnection()){
                    try (Statement statement = connection.createStatement()){
                        statement.setQueryTimeout(1);
                        execute(statement);
                    }
                } catch (Exception e) {
                } finally {
                    try {
                        Thread.sleep(delay);
                    } catch (Exception e) {
                    }
                }
            }
        }

        private Connection getConnection() throws SQLException {
            try {
                Connection connection = dataSource.getConnection();
                System.out.println("return " + connection.getMetaData().getURL() + " time is :" + new Date().toString());
                return connection;
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            } finally {
            }
        }

        abstract void execute(Statement statement) throws SQLException;
    }

}