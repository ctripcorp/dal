package com.ctrip.datasource.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import com.ctrip.platform.dal.dao.datasource.DataSourceValidator;

public class DataSourceValidatorTest {
    private static final String mySqlName = "mysqldaltest01db_W";
    private static final String sqlServerName = "dalservicedb";

    @BeforeClass
    public static void beforeClass() throws Exception {}

    @Test
    public void testMySqlDataSourceValidator() throws Exception {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://DST56614:3306/dao_test?useUnicode=true&characterEncoding=UTF-8", "root",
                    "!QAZ@WSX1qaz2wsx");
        } catch (Throwable e) {
        }

        try {
            Statement statement1 = connection.createStatement();
            statement1.setQueryTimeout(5); // set timeout,a timer will be created.
            statement1.execute("select SLEEP(1)");
        } catch (Throwable e) {
            System.out.println("Statement1 exception captured.");
        }

        boolean result = false;

        try {
            // emulate connection pool to validate the connection.
            DataSourceValidator validator = new DataSourceValidator();
            result = validator.validate(connection, PooledConnection.VALIDATE_BORROW);
        } catch (Throwable e) {
            System.out.println(String.format("DataSource validation:%s", result));
        }

        // emulate connection pool to close the connection.
        connection.close();

        try {
            Statement statement2 = connection.createStatement();
            statement2.execute("select SLEEP(1)");
        } catch (Throwable e) {
            System.out.println("Statement2 exception captured.");
        }

        Thread.sleep(300 * 1000);
    }

    @Test
    public void testSqlServerDataSourceValidator() throws Exception {
        TitanProvider provider = new TitanProvider();
        Map<String, String> settings = new HashMap<>();
        provider.initialize(settings);

        Set<String> names = new HashSet<>();
        names.add(sqlServerName);
        provider.setup(names);

        DataSourceLocator loc = new DataSourceLocator(provider);
        DataSource dataSource = loc.getDataSource(sqlServerName);
        int i = 0;
        while (true) {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(3);
            statement.execute("select 1");
            connection.close();
            System.out.println(i);
            i++;
            Thread.sleep(100);
        }
    }

}
