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
			Set<String> shards = stra.locateShards(cfg, "shardingtestMaster", hints);
			System.out.println(shards);
			
			hints = new DalHints();
			Set<String> set = new HashSet<String>();
			set.add("1");
			set.add("2");
			set.add("3");
			hints.set(DalHintEnum.shards, set);
			shards = stra.locateShards(cfg, "shardingtestMaster", hints);
			System.out.println(shards);
			
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
			
			Set<String> shards = stra.locateShards(cfg, "shardingtestMaster", hints);
			System.out.println(shards);
			
			hints = new DalHints();
			colValues.clear();
			colValues.put("user_id", 0);
			hints.set(DalHintEnum.shardColValues, colValues);
			shards = stra.locateShards(cfg, "shardingtestMaster", hints);
			System.out.println(shards);
			
			hints = new DalHints();
			colValues.clear();
			colValues.put("user_id", 2);
			hints.set(DalHintEnum.shardColValues, colValues);
			shards = stra.locateShards(cfg, "shardingtestMaster", hints);
			System.out.println(shards);
			
			hints = new DalHints();
			colValues.clear();
			colValues.put("user_id", 3);
			hints.set(DalHintEnum.shardColValues, colValues);
			shards = stra.locateShards(cfg, "shardingtestMaster", hints);
			System.out.println(shards);

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
