package test.com.ctrip.platform.dal.dao.shard;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;
import com.ctrip.platform.dal.dao.strategy.SimpleShardHintStrategy;

public class SimpleShardHintStrategyTest {
    public SimpleShardHintStrategy createTest() throws Exception {
        SimpleShardHintStrategy test = new SimpleShardHintStrategy();
        Map<String, String> settings = new HashMap<String, String>();
        settings.put(SimpleShardHintStrategy.SHARD_BY_DB, "true");
        settings.put(SimpleShardHintStrategy.SHARD_BY_TABLE, "true");
        settings.put(SimpleShardHintStrategy.SHARDED_TABLES, "table1 , Table1,   TaBle2 , taBLE ");
        settings.put(ShardColModShardStrategy.SEPARATOR, "_");
        test.initialize(settings);
        assertTrue(test.isShardingByDb());
        assertTrue(test.isShardingByTable());
        return test;
    }
    
    @Test
    public void testLocateDbShard() throws Exception {
        SimpleShardHintStrategy test = createTest();
        assertEquals("1", test.locateDbShard(null, null, new DalHints().inShard(1)));
        assertEquals("1", test.locateDbShard(null, null, new DalHints().inShard("1")));
    }
    
    @Test
    public void testLocateTableShardBackwardCompatible() throws Exception {
        SimpleShardHintStrategy test = createTest();
        assertEquals("1", test.locateTableShard(null, "db", new DalHints().inTableShard(1)));
        assertEquals("1", test.locateTableShard(null, "db", new DalHints().inTableShard("1")));
    }
    
    @Test
    public void testLocateTableShard() throws Exception {
        SimpleShardHintStrategy test = createTest();
        assertEquals("1", test.locateTableShard(null, "db", "abc", new DalHints().inTableShard(1)));
        assertEquals("1", test.locateTableShard(null, "db", "abc", new DalHints().inTableShard("1")));
    }
    
    @Test
    public void testIsShardingEnable() throws Exception {
        SimpleShardHintStrategy test = createTest();
        assertTrue(test.isShardingEnable("table"));
        assertTrue(test.isShardingEnable("tablE"));
        assertTrue(test.isShardingEnable("Table"));
        
        assertTrue(test.isShardingEnable("table1"));
        assertTrue(test.isShardingEnable("tablE1"));
        assertTrue(test.isShardingEnable("Table1"));
        
        assertTrue(test.isShardingEnable("table2"));
        assertTrue(test.isShardingEnable("tablE2"));
        assertTrue(test.isShardingEnable("Table2"));

        assertFalse(test.isShardingEnable("table "));
        assertFalse(test.isShardingEnable(" table"));
    }
}
