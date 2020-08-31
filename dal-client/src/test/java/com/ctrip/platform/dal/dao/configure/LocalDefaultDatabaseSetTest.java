package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.strategy.LocalShardStrategyAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class LocalDefaultDatabaseSetTest {

    @Test
    public void testLocalDatabaseSet() throws Exception {
        Set<String> dbShards = new HashSet<>();
        dbShards.add("0");
        dbShards.add("1");
        Set<String> tblShards = new HashSet<>();
        tblShards.add("0");
        tblShards.add("1");
        tblShards.add("2");
        tblShards.add("3");

        DefaultDatabaseSet databaseSet = mockDatabaseSet();
        Assert.assertFalse(databaseSet.getStrategy() instanceof LocalShardStrategyAdapter);
        Assert.assertEquals(dbShards, databaseSet.getAllShards());
        Assert.assertEquals(tblShards, databaseSet.getAllTableShards("tbl1"));
        Assert.assertFalse(databaseSet.isTableShardingSupported("tbl2"));
        Assert.assertEquals(tblShards, databaseSet.getAllTableShards("tbl3"));
        Assert.assertEquals(mockProperties(), databaseSet.getProperties());

        LocalDefaultDatabaseSet localDatabaseSet1 =
                new LocalDefaultDatabaseSet(databaseSet, false);
        Assert.assertTrue(localDatabaseSet1.getStrategy() instanceof LocalShardStrategyAdapter);
        Assert.assertEquals(dbShards, localDatabaseSet1.getAllShards());
        Assert.assertEquals(tblShards, localDatabaseSet1.getAllTableShards("tbl1"));
        Assert.assertFalse(localDatabaseSet1.isTableShardingSupported("tbl2"));
        Assert.assertEquals(tblShards, localDatabaseSet1.getAllTableShards("tbl3"));
        Assert.assertEquals(mockProperties(), localDatabaseSet1.getProperties());

        LocalDefaultDatabaseSet localDatabaseSet2 =
                new LocalDefaultDatabaseSet(databaseSet, true);
        Assert.assertTrue(localDatabaseSet2.getStrategy() instanceof LocalShardStrategyAdapter);
        Assert.assertEquals(dbShards, localDatabaseSet2.getAllShards());
        Assert.assertFalse(localDatabaseSet2.isTableShardingSupported("tbl1"));
        Assert.assertFalse(localDatabaseSet2.isTableShardingSupported("tbl2"));
        Assert.assertFalse(localDatabaseSet2.isTableShardingSupported("tbl3"));
        Assert.assertEquals(mockProperties(), localDatabaseSet2.getProperties());
    }

    private DefaultDatabaseSet mockDatabaseSet() throws Exception {
        String strategy = "class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy" +
                ";columns=index;mod=2;tableColumns=tableIndex;tableMod=4" +
                ";separator=_;shardedTables=tbl1,tbl3";
        return new DefaultDatabaseSet("mock-set", "mySqlProvider", strategy,
                mockDatabases("mock-db1", "mock-db2"), null, mockProperties());
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

    private Map<String, String> mockProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("k1", "v1");
        properties.put("k2", "v2");
        return properties;
    }

}
