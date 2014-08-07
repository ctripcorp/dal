package com.ctrip.platform.dal.tester.shard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;
import com.ctrip.platform.dal.dao.strategy.DalShardStrategy;
import com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;
import com.ctrip.platform.dal.dao.strategy.SimpleShardHintStrategy;
import com.ctrip.platform.dal.sql.logging.DalEventEnum;

public class ShardConfigTest {
	private void testRead() {
		try {
			DalConfigure cfg = DalConfigureFactory.load("e:/Dal.config");
			System.out.println(cfg.getName());
			System.out.println(cfg.getDatabaseSet("shardingtestMaster").getAllShards());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void testSimpleStrategey() {
		try {
			DalShardStrategy stra = new SimpleShardHintStrategy();
			Map<String, String> settings = new HashMap<String, String>();
			stra.initialize(settings);
			DalConfigure cfg;
			cfg = DalConfigureFactory.load("e:/Dal.config");
			DalHints hints = new DalHints();
			hints.set(DalHintEnum.shard, "1");
			System.out.println(stra.locateShard(cfg, "shardingtestMaster", hints));
			
			hints = new DalHints();
			Set<String> set = new HashSet<String>();
			set.add("1");
			set.add("2");
			set.add("3");
//			hints.set(DalHintEnum.shards, set);
			System.out.println(stra.locateShard(cfg, "shardingtestMaster", hints));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void testModStrategey() {
		try {
			DalShardStrategy stra = new ShardColModShardStrategy();
			Map<String, String> settings = new HashMap<String, String>();
			settings.put(ShardColModShardStrategy.COLUMNS, "user_id,order_id");
			settings.put(ShardColModShardStrategy.MOD, "3");
			
			stra.initialize(settings);
			DalConfigure cfg;
			cfg = DalConfigureFactory.load("e:/Dal.config");
			
			Map<String, Integer> colValues = new HashMap<String, Integer>();
			colValues.put("aaa", 1);
			DalHints hints = new DalHints();
			hints.set(DalHintEnum.shardColValues, colValues);
			
			System.out.println(stra.locateShard(cfg, "shardingtestMaster", hints));
			
			hints = new DalHints();
			colValues.clear();
			colValues.put("user_id", 0);
			hints.set(DalHintEnum.shardColValues, colValues);
			hints.set(DalHintEnum.masterOnly);
			hints.set(DalHintEnum.operation, DalEventEnum.BATCH_UPDATE);
			System.out.println(stra.locateShard(cfg, "shardingtestMaster", hints));
			System.out.println(stra.isMaster(cfg, "shardingtestMaster", hints));
			
			hints = new DalHints();
			colValues.clear();
			colValues.put("user_id", 2);
			hints.set(DalHintEnum.shardColValues, colValues);
			hints.set(DalHintEnum.operation, DalEventEnum.QUERY);
			System.out.println(stra.locateShard(cfg, "shardingtestMaster", hints));
			System.out.println(stra.isMaster(cfg, "shardingtestMaster", hints));
			
			hints = new DalHints();
			colValues.clear();
			colValues.put("user_id", 3);
			hints.set(DalHintEnum.shardColValues, colValues);
			hints.set(DalHintEnum.operation, DalEventEnum.EXECUTE);
			System.out.println(stra.locateShard(cfg, "shardingtestMaster", hints));
			System.out.println(stra.isMaster(cfg, "shardingtestMaster", hints));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ShardConfigTest test = new ShardConfigTest();
		test.testRead();
		test.testSimpleStrategey();
		test.testModStrategey();
	}
}
