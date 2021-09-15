package com.ctrip.platform.dal.dao.util;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
import com.mysql.jdbc.MySQLConnection;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

/**
 * @author c7ch23en
 */
public class WaitTimeoutTest {

    @Test
    public void test() throws Exception {
        DataSourceConfigure config = new DataSourceConfigure("test");
        config.setDriverClass("com.mysql.jdbc.Driver");
        config.setConnectionUrl("jdbc:mysql://10.32.20.116:3306/llj_test?useUnicode=true&characterEncoding=UTF-8");
        config.setUserName("root");
        config.setPassword("!QAZ@WSX1qaz2wsx");
        config.setProperty(DataSourceConfigureConstants.VALIDATORCLASSNAME, "com.ctrip.platform.dal.dao.util.MockValidator");
        config.setProperty(DataSourceConfigureConstants.VALIDATIONINTERVAL, "2000");
        RefreshableDataSource dataSource = new RefreshableDataSource(config.getName(), config);

        while (true) {
            try (Connection connection = dataSource.getConnection()) {
                System.err.println("connId: " + connection.unwrap(MySQLConnection.class).getId());
                try (Statement statement = connection.createStatement()) {
                    try (ResultSet rs = statement.executeQuery("show session variables like 'wait_timeout'")) {
                        if (rs != null && rs.next())
                            System.err.println("wait_timeout: " + rs.getInt(2));
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                TimeUnit.MILLISECONDS.sleep(500);
            }
        }
    }

}
