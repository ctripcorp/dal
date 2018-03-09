package test.com.ctrip.platform.dal.dao.shard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;
import com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;

public class ShardColModShardStrategyTest {
	private final String logicDbName = "DB_TABLE_SHARD";
	private final String tableName = "table";
	
	@BeforeClass
	public static void setUpBeforeClass() {
//		Map<String, String> settings = new HashMap<String, String>();
//		settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
//		settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
//		settings.put(ShardColModShardStrategy.MOD, "2");
//		settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
//		strategy.initialize(settings);
	}
	
	@Test
	public void testIsShardingByDbFalse() {
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
//		settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
		settings.put(ShardColModShardStrategy.MOD, "2");
		strategy.initialize(settings);
		assertFalse(strategy.isShardingByDb());
	}
	
	@Test
	public void testIsShardingByDbTrue() {
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
		settings.put(ShardColModShardStrategy.MOD, "2");
		strategy.initialize(settings);
		assertTrue(strategy.isShardingByDb());
	}
	
	@Test
	public void testIsShardingBytableFalse() {
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
		strategy.initialize(settings);
		assertFalse(strategy.isShardingByTable());
	}
	
	@Test
	public void testIsShardingByTableTrue() {
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
		settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
		strategy.initialize(settings);
		assertTrue(strategy.isShardingByTable());
	}
	
    @Test
    public void testIsShardingEnableForTable() {
        ShardColModShardStrategy strategy = new ShardColModShardStrategy();
        Map<String, String> settings = new HashMap<String, String>();
        settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
        settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
        settings.put(ShardColModShardStrategy.SHARDED_TABLES, "table1,   table");
        strategy.initialize(settings);
        assertTrue(strategy.isShardingByTable());
        assertTrue(strategy.isShardingEnable("table"));
        assertTrue(strategy.isShardingEnable("table1"));
        assertTrue(strategy.isShardingEnable("tablE"));
        assertTrue(strategy.isShardingEnable("tabLe1"));
        assertTrue(strategy.isShardingEnable("TABLE"));
        assertTrue(strategy.isShardingEnable("tabLE1"));
    }
    
	@Test
	public void testLocateDbShardByShard() throws Exception {
		DalConfigure configure = DalConfigureFactory.load();
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
		settings.put(ShardColModShardStrategy.MOD, "2");
		settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
		settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
		strategy.initialize(settings);
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().inShard("0")));
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().inShard("1")));
	}
	
	@Test
	public void testLocateDbShardByShardValue() throws Exception {
		DalConfigure configure = DalConfigureFactory.load();
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
		settings.put(ShardColModShardStrategy.MOD, "2");
		settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
		settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
		strategy.initialize(settings);
		
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardValue(0)));
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardValue(1)));
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardValue(2)));
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardValue(3)));
		
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardValue(100000000000L)));
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardValue(100000000001L)));
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardValue(100000000002L)));
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardValue(100000000003L)));
	}

	@Test
	public void testLocateDbShardByParameters() throws Exception {
		DalConfigure configure = DalConfigureFactory.load();
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
		settings.put(ShardColModShardStrategy.MOD, "2");
		settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
		settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
		strategy.initialize(settings);
		
		StatementParameters parameters = null;

		parameters = new StatementParameters();
		parameters.set(1, "id", Types.INTEGER, 0);
		parameters.set(1, "abc", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setParameters(parameters)));

		
		parameters = new StatementParameters();
		parameters.set(1, "id", Types.INTEGER, 1);
		parameters.set(1, "abc", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "id", Types.INTEGER, 2);
		parameters.set(2, "abc", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "id", Types.INTEGER, 3);
		parameters.set(2, "abc", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setParameters(parameters)));
		
		parameters = new StatementParameters();
		parameters.set(1, "id", Types.BIGINT, 100000000003L);
		parameters.set(2, "abc", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setParameters(parameters)));
		
		// Test case insensitive
        parameters = new StatementParameters();
        parameters.set(1, "iD", Types.INTEGER, 0);
        parameters.set(1, "abc", Types.INTEGER, 1);
        parameters.set(1, "def", Types.INTEGER, 1);
        
        assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setParameters(parameters)));

        
        parameters = new StatementParameters();
        parameters.set(1, "Id", Types.INTEGER, 1);
        parameters.set(1, "abc", Types.INTEGER, 1);
        parameters.set(1, "def", Types.INTEGER, 1);
        
        assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setParameters(parameters)));

        parameters = new StatementParameters();
        parameters.set(1, "ID", Types.INTEGER, 2);
        parameters.set(2, "abc", Types.INTEGER, 1);
        parameters.set(3, "def", Types.INTEGER, 1);
        
        assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setParameters(parameters)));

        parameters = new StatementParameters();
        parameters.set(1, "iD", Types.INTEGER, 3);
        parameters.set(2, "abc", Types.INTEGER, 1);
        parameters.set(3, "def", Types.INTEGER, 1);
        
        assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setParameters(parameters)));
        
        parameters = new StatementParameters();
        parameters.set(1, "Id", Types.BIGINT, 100000000003L);
        parameters.set(2, "abc", Types.INTEGER, 1);
        parameters.set(3, "def", Types.INTEGER, 1);
        
        assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setParameters(parameters)));		
	}

	@Test
	public void testLocateDbShardByShardCol() throws Exception {
		DalConfigure configure = DalConfigureFactory.load();
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
		settings.put(ShardColModShardStrategy.MOD, "2");
		settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
		settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
		strategy.initialize(settings);
		
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("id", 0)));
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("id", 1)));
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("id", 2)));
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("id", 3)));
		
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("id", 100000000000L)));
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("id", 100000000001L)));
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("id", 100000000002L)));
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("id", 100000000003L)));

		// Test case insensitive
        assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("Id", 0)));
        assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("iD", 1)));
        assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("ID", 2)));
        assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("iD", 3)));
        
        assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("iD", 100000000000L)));
        assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("Id", 100000000001L)));
        assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("ID", 100000000002L)));
        assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setShardColValue("iD", 100000000003L)));

	}
	
	@Test
	public void testLocateDbShardByFields() throws Exception {
		DalConfigure configure = DalConfigureFactory.load();
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
		settings.put(ShardColModShardStrategy.MOD, "2");
		settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
		settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
		strategy.initialize(settings);
		
		Map<String, Object> fields = new HashMap<String, Object>(); 
		fields.put("id", 0);
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
		
		fields.put("id", 1);
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
		
		fields.put("id", 2);
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
		
		fields.put("id", 3);
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
		// Test long
		fields.put("id", 100000000000L);
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
		
		fields.put("id", 100000000001L);
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
		
		fields.put("id", 100000000002L);
		assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
		
		fields.put("id", 100000000003L);
		assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
		
		// Test case insensitive
		fields.clear();
        fields.put("iD", 0);
        assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
        
        fields.clear();
        fields.put("Id", 1);
        assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
        
        fields.clear();
        fields.put("ID", 2);
        assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
        // Test long
        fields.clear();
        fields.put("iD", 100000000000L);
        assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
        
        fields.clear();
        fields.put("Id", 100000000001L);
        assertEquals("1", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
        
        fields.clear();
        fields.put("ID", 100000000002L);
        assertEquals("0", strategy.locateDbShard(configure, logicDbName, new DalHints().setFields(fields)));
	}
	
    @Test
    public void testLocateTableShardByTableShardForBackwardCompatible() throws Exception {
        DalConfigure configure = DalConfigureFactory.load();
        ShardColModShardStrategy strategy = new ShardColModShardStrategy();
        Map<String, String> settings = new HashMap<String, String>();
        settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
        settings.put(ShardColModShardStrategy.MOD, "2");
        settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
        settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
        settings.put(ShardColModShardStrategy.SEPARATOR, "_");
        strategy.initialize(settings);
        
        assertEquals("0", strategy.locateTableShard(configure, logicDbName, new DalHints().inTableShard("0")));
        assertEquals("1", strategy.locateTableShard(configure, logicDbName, new DalHints().inTableShard("1")));
        assertEquals("2", strategy.locateTableShard(configure, logicDbName, new DalHints().inTableShard("2")));
        assertEquals("3", strategy.locateTableShard(configure, logicDbName, new DalHints().inTableShard("3")));
    }
    	
	@Test
	public void testLocateTableShardByTableShard() throws Exception {
		DalConfigure configure = DalConfigureFactory.load();
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
		settings.put(ShardColModShardStrategy.MOD, "2");
		settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
		settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
		settings.put(ShardColModShardStrategy.SEPARATOR, "_");
		strategy.initialize(settings);
		
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().inTableShard("0")));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().inTableShard("1")));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().inTableShard("2")));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().inTableShard("3")));
	}
	
	@Test
	public void testLocateTableShardByTableShardValue() throws Exception {
		DalConfigure configure = DalConfigureFactory.load();
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
		settings.put(ShardColModShardStrategy.MOD, "2");
		settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
		settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
		settings.put(ShardColModShardStrategy.SEPARATOR, "_");
		strategy.initialize(settings);

		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue("0")));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue("1")));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue("2")));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue("3")));

		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue("4")));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue("5")));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue("6")));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue("7")));

		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue(0)));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue(1)));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue(2)));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue(3)));

		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue(4)));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue(5)));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue(6)));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue(7)));
		
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue(100000000004L)));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue(100000000005L)));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue(100000000006L)));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setTableShardValue(100000000007L)));
	}

	@Test
	public void testLocateTableShardByParameters() throws Exception {
		DalConfigure configure = DalConfigureFactory.load();
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
		settings.put(ShardColModShardStrategy.MOD, "2");
		settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
		settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
		settings.put(ShardColModShardStrategy.SEPARATOR, "_");
		strategy.initialize(settings);
		
		StatementParameters parameters = null;

		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 0);
		parameters.set(1, "abc", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));

		parameters.set(1, "index", Types.INTEGER, 100000000004L);
		parameters.set(1, "abc", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));
		
		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 1);
		parameters.set(1, "abc", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 100000000001L);
		parameters.set(1, "abc", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));
		
		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 2);
		parameters.set(2, "abc", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 3);
		parameters.set(2, "abc", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));

		// Test case insensitive
        parameters = new StatementParameters();
        parameters.set(1, "indeX", Types.INTEGER, 0);
        parameters.set(1, "abc", Types.INTEGER, 1);
        parameters.set(1, "def", Types.INTEGER, 1);
        
        assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));

        parameters.set(1, "indEx", Types.INTEGER, 100000000004L);
        parameters.set(1, "abc", Types.INTEGER, 1);
        parameters.set(1, "def", Types.INTEGER, 1);
        
        assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));
        
        parameters = new StatementParameters();
        parameters.set(1, "inDex", Types.INTEGER, 1);
        parameters.set(1, "abc", Types.INTEGER, 1);
        parameters.set(1, "def", Types.INTEGER, 1);
        
        assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));

        parameters = new StatementParameters();
        parameters.set(1, "iNdex", Types.INTEGER, 100000000001L);
        parameters.set(1, "abc", Types.INTEGER, 1);
        parameters.set(1, "def", Types.INTEGER, 1);
        
        assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));
        
        parameters = new StatementParameters();
        parameters.set(1, "Index", Types.INTEGER, 2);
        parameters.set(2, "abc", Types.INTEGER, 1);
        parameters.set(3, "def", Types.INTEGER, 1);
        
        assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));

        parameters = new StatementParameters();
        parameters.set(1, "INDEX", Types.INTEGER, 3);
        parameters.set(2, "abc", Types.INTEGER, 1);
        parameters.set(3, "def", Types.INTEGER, 1);
        
        assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));

		// Test another key
        parameters = new StatementParameters();
        parameters.set(1, "index1", Types.INTEGER, 0);
        parameters.set(1, "abc", Types.INTEGER, 1);
        parameters.set(1, "def", Types.INTEGER, 1);
        
        assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));
        
		parameters = new StatementParameters();
		parameters.set(1, "index1", Types.INTEGER, 1);
		parameters.set(1, "abc", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "index1", Types.INTEGER, 2);
		parameters.set(2, "abc", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "index1", Types.INTEGER, 3);
		parameters.set(2, "abc", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));
		
		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 0);
		parameters.set(1, "index1", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));

		
		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 1);
		parameters.set(1, "index1", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 2);
		parameters.set(2, "index1", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 3);
		parameters.set(2, "index1", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setParameters(parameters)));
	}

	@Test
	public void testLocateTableShardByShardCol() throws Exception {
		DalConfigure configure = DalConfigureFactory.load();
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
		settings.put(ShardColModShardStrategy.MOD, "2");
		settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
		settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
		settings.put(ShardColModShardStrategy.SEPARATOR, "_");
		strategy.initialize(settings);
		
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("index", 0)));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("index1", 1)));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("index", 2)));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("index1", 3)));

		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("index", 4)));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("index1", 5)));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("index", 6)));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("index1", 7)));
		
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("index", 100000000004L)));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("index1", 100000000005L)));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("index", 100000000006L)));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("index1", 100000000007L)));
		
		// Test case insensitive
        assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("Index", 0)));
        assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("iNdex1", 1)));
        assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("inDex", 2)));
        assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("inDEx1", 3)));

        assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("indeX", 4)));
        assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("INDEX1", 5)));
        assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("INdex", 6)));
        assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("inDEX1", 7)));
        
        assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("InDeX", 100000000004L)));
        assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("iNdEx1", 100000000005L)));
        assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("InDEX", 100000000006L)));
        assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setShardColValue("iNDEX1", 100000000007L)));		
	}
	
	@Test
	public void testLocateTableShardByFields() throws Exception {
		DalConfigure configure = DalConfigureFactory.load();
		ShardColModShardStrategy strategy = new ShardColModShardStrategy();
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(ShardColModShardStrategy.COLUMNS, "id,id1");
		settings.put(ShardColModShardStrategy.MOD, "2");
		settings.put(ShardColModShardStrategy.TABLE_COLUMNS, "index,index1");
		settings.put(ShardColModShardStrategy.TABLE_MOD, "4");
		settings.put(ShardColModShardStrategy.SEPARATOR, "_");
		strategy.initialize(settings);
		
		Map<String, Object> fields = null; 

		fields = new HashMap<String, Object>();
		fields.put("index", 0);
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));
		
		fields = new HashMap<String, Object>();
		fields.put("index1", 1);
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));
		
		fields = new HashMap<String, Object>();
		fields.put("index", 2);
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));
		
		fields = new HashMap<String, Object>();
		fields.put("index1", 3);
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));

		fields = new HashMap<String, Object>();
		fields.put("index", 4);
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));

		fields = new HashMap<String, Object>();
		fields.put("index1", 5);
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));

		fields = new HashMap<String, Object>();
		fields.put("index", 6);
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));
		
		fields = new HashMap<String, Object>();
		fields.put("index1", 7);
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));
		
		fields = new HashMap<String, Object>();
		fields.put("index1", 100000000007L);
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));
		
		// Test case insensitive
        fields = new HashMap<String, Object>();
        fields.put("Index", 0);
        assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));
        
        fields = new HashMap<String, Object>();
        fields.put("iNdex1", 1);
        assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));
        
        fields = new HashMap<String, Object>();
        fields.put("inDex", 2);
        assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));
        
        fields = new HashMap<String, Object>();
        fields.put("indEx1", 3);
        assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));

        fields = new HashMap<String, Object>();
        fields.put("indeX", 4);
        assertEquals("0", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));

        fields = new HashMap<String, Object>();
        fields.put("INdex1", 5);
        assertEquals("1", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));

        fields = new HashMap<String, Object>();
        fields.put("iNDex", 6);
        assertEquals("2", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));
        
        fields = new HashMap<String, Object>();
        fields.put("inDEx1", 7);
        assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));
        
        fields = new HashMap<String, Object>();
        fields.put("indEX1", 100000000007L);
        assertEquals("3", strategy.locateTableShard(configure, logicDbName, tableName, new DalHints().setFields(fields)));
	}
}
