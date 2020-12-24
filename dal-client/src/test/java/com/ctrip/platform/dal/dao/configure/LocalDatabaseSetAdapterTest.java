package com.ctrip.platform.dal.dao.configure;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class LocalDatabaseSetAdapterTest {

    @Test
    public void testAdapt() throws Exception {
        LocalDatabaseSetAdapter adapter = new LocalDatabaseSetAdapter(mockConnectionStrings());
        DatabaseSet set1 = mockDatabaseSet("set1", "set1-db1", "set1-db2");
        DatabaseSet set1Adapted = adapter.adapt(set1);
        Assert.assertNotEquals(set1, set1Adapted);
        Assert.assertTrue(set1Adapted instanceof LocalDefaultDatabaseSet);
        DatabaseSet set2 = mockDatabaseSet("set2", "set2-db1", "set2-db2");
        DatabaseSet set2Adapted = adapter.adapt(set2);
        Assert.assertNotEquals(set2, set2Adapted);
        Assert.assertTrue(set2Adapted instanceof LocalDefaultDatabaseSet);
        DatabaseSet set3 = mockDatabaseSet("set3", "set3-db1");
        DatabaseSet set3Adapted = adapter.adapt(set3);
        Assert.assertEquals(set3, set3Adapted);
        Assert.assertFalse(set3Adapted instanceof LocalDefaultDatabaseSet);
    }

    private Map<String, DalConnectionString> mockConnectionStrings() {
        Map<String, DalConnectionString> connectionStrings = new HashMap<>();
        connectionStrings.put("set1-db1",
                mockConnectionString("set1-db1", true, true));
        connectionStrings.put("set1-db2",
                mockConnectionString("set1-db2", true, true));
        connectionStrings.put("set2-db1",
                mockConnectionString("set2-db1", true, true));
        connectionStrings.put("set2-db2",
                mockConnectionString("set2-db2", true, false));
        connectionStrings.put("set3-db1",
                mockConnectionString("set3-db1", false, false));
        return connectionStrings;
    }

    private DalConnectionString mockConnectionString(String dbName, boolean local, boolean tableShardingDisabled) {
        if (local)
            return new LocalConnectionString(
                    dbName,
                    "Server=ip;port=3306;UID=u;password=p;database=mock",
                    "Server=domain;port=3306;UID=u;password=p;database=mock",
                    tableShardingDisabled);
        else
            return new ConnectionString(dbName,
                    "Server=ip;port=3306;UID=u;password=p;database=mock",
                    "Server=domain;port=3306;UID=u;password=p;database=mock");
    }

    private DatabaseSet mockDatabaseSet(String logicName, String... dbNames) throws Exception {
        return new DefaultDatabaseSet(logicName, "mySqlProvider", mockDatabases(dbNames));
    }

    private Map<String, DataBase> mockDatabases(String... dbNames) {
        Map<String, DataBase> databases = new HashMap<>();
        if (dbNames.length == 1) {
            String dbName = dbNames[0];
            databases.put(dbName, new DefaultDataBase(dbName, true, "", dbName));
        } else {
            for (int i = 0; i < dbNames.length; i++) {
                String dbName = dbNames[i];
                databases.put(dbName, new DefaultDataBase(dbName, true, i + "", dbName));
            }
        }
        return databases;
    }

}
