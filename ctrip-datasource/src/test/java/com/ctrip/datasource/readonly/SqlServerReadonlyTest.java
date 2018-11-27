package com.ctrip.datasource.readonly;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.client.DbMeta;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SqlServerReadonlyTest {
    private static final String READ_ONLY_DATABASE = "readonly_test";

    @Test
    public void testSqlServerReadonlyError() {
        Connection connection = null;
        Set<String> names = new HashSet<>();
        names.add(READ_ONLY_DATABASE);
        Map<String, String> settings = new HashMap<>();
        settings.put("useLocalConfig", "true");

        try {
            TitanProvider provider = new TitanProvider();
            provider.initialize(settings);
            provider.setSourceTypeByEnv();
            provider.setup(names);

            DataSourceLocator loc = new DataSourceLocator(provider);
            DataSource ds = loc.getDataSource(READ_ONLY_DATABASE);
            connection = ds.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into test_table(test_column) values ('1')");
            Assert.fail();
        } catch (Throwable e) {
            boolean isReadOnly = isReadOnlyException(connection, e);
            Assert.assertTrue(isReadOnly);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable e) {
                }
            }
        }
    }

    private boolean isReadOnlyException(Connection connection, Throwable e) {
        boolean result = false;
        try {
            DbMeta meta = DbMeta.createIfAbsent("", DatabaseCategory.SqlServer, connection);
            DalConnection dalConnection = new DalConnection(connection, true, "", meta);
            Method method = DalConnection.class.getDeclaredMethod("isSpecificException", Throwable.class);
            method.setAccessible(true);
            result = (boolean) method.invoke(dalConnection, e);
        } catch (Throwable ex) {
            System.out.println(ex.getCause().getMessage());
            Assert.fail();
        }

        return result;
    }

}
