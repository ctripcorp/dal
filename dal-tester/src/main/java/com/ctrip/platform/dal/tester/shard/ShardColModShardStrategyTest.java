package com.ctrip.platform.dal.tester.shard;

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
		
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, new DalHints().inTableShard("0")));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, new DalHints().inTableShard("1")));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, new DalHints().inTableShard("2")));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, new DalHints().inTableShard("3")));
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

		assertEquals("0", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue("0")));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue("1")));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue("2")));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue("3")));

		assertEquals("0", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue("4")));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue("5")));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue("6")));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue("7")));

		assertEquals("0", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue(0)));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue(1)));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue(2)));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue(3)));

		assertEquals("0", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue(4)));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue(5)));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue(6)));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, new DalHints().setTableShardValue(7)));
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
		
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, new DalHints().setParameters(parameters)));

		
		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 1);
		parameters.set(1, "abc", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 2);
		parameters.set(2, "abc", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 3);
		parameters.set(2, "abc", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "index1", Types.INTEGER, 0);
		parameters.set(1, "abc", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, new DalHints().setParameters(parameters)));

		
		parameters = new StatementParameters();
		parameters.set(1, "index1", Types.INTEGER, 1);
		parameters.set(1, "abc", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "index1", Types.INTEGER, 2);
		parameters.set(2, "abc", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "index1", Types.INTEGER, 3);
		parameters.set(2, "abc", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, new DalHints().setParameters(parameters)));
		
		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 0);
		parameters.set(1, "index1", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, new DalHints().setParameters(parameters)));

		
		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 1);
		parameters.set(1, "index1", Types.INTEGER, 1);
		parameters.set(1, "def", Types.INTEGER, 1);
		
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 2);
		parameters.set(2, "index1", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, new DalHints().setParameters(parameters)));

		parameters = new StatementParameters();
		parameters.set(1, "index", Types.INTEGER, 3);
		parameters.set(2, "index1", Types.INTEGER, 1);
		parameters.set(3, "def", Types.INTEGER, 1);
		
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, new DalHints().setParameters(parameters)));
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
		
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, new DalHints().setShardColValue("index", 0)));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, new DalHints().setShardColValue("index1", 1)));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, new DalHints().setShardColValue("index", 2)));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, new DalHints().setShardColValue("index1", 3)));

		assertEquals("0", strategy.locateTableShard(configure, logicDbName, new DalHints().setShardColValue("index", 4)));
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, new DalHints().setShardColValue("index1", 5)));
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, new DalHints().setShardColValue("index", 6)));
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, new DalHints().setShardColValue("index1", 7)));
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
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, new DalHints().setFields(fields)));
		
		fields = new HashMap<String, Object>();
		fields.put("index1", 1);
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, new DalHints().setFields(fields)));
		
		fields = new HashMap<String, Object>();
		fields.put("index", 2);
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, new DalHints().setFields(fields)));
		
		fields = new HashMap<String, Object>();
		fields.put("index1", 3);
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, new DalHints().setFields(fields)));

		fields = new HashMap<String, Object>();
		fields.put("index", 4);
		assertEquals("0", strategy.locateTableShard(configure, logicDbName, new DalHints().setFields(fields)));

		fields = new HashMap<String, Object>();
		fields.put("index1", 5);
		assertEquals("1", strategy.locateTableShard(configure, logicDbName, new DalHints().setFields(fields)));

		fields = new HashMap<String, Object>();
		fields.put("index", 6);
		assertEquals("2", strategy.locateTableShard(configure, logicDbName, new DalHints().setFields(fields)));
		
		fields = new HashMap<String, Object>();
		fields.put("index1", 7);
		assertEquals("3", strategy.locateTableShard(configure, logicDbName, new DalHints().setFields(fields)));
	}
}
