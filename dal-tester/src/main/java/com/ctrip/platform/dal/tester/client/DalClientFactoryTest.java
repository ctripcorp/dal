package com.ctrip.platform.dal.tester.client;

import java.util.List;
import java.util.Map;

import com.ctrip.freeway.config.LogConfig;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;

public class DalClientFactoryTest {
	private void testNoStrategy() {
		try {
			DalClientFactory.initClientFactoryBy("e:/DalNoShard.config");
			DalClient client = DalClientFactory.getClient("AbacusDB_INSERT_1");
			DalHints hints = new DalHints();
			//SimpleShardHintStrategy test
			hints.set(DalHintEnum.shard, "1");
			hints.set(DalHintEnum.masterOnly);

			List<Map<String, Object>> result = client.query("SELECT TOP 1000 * FROM AbacusPara Where ParaID > 0 ", new StatementParameters(), hints, new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper()));
			for(Map<String, Object> e: result) {
				System.out.println("ParaTypeID");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testLoad() {
		try {
			DalClientFactory.initClientFactoryBy("e:/DalMult.config");
			DalClient client = DalClientFactory.getClient("AbacusDB_INSERT_1");
			DalHints hints = new DalHints();
			//SimpleShardHintStrategy test
			hints.set(DalHintEnum.shard, "1");
			hints.set(DalHintEnum.masterOnly);

			List<Map<String, Object>> result = client.query("SELECT TOP 1000 * FROM AbacusPara Where ParaID > 0 ", new StatementParameters(), hints, new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper()));
			System.out.println(result.size());
			
			hints.set(DalHintEnum.shard, "");
			result = client.query("SELECT TOP 1000 * FROM AbacusPara Where ParaID > 0 ", new StatementParameters(), hints, new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper()));
			System.out.println(result.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testLoadFromClassPath() {
		try {
			DalClientFactory.initClientFactory();
			DalClient client = DalClientFactory.getClient("AbacusDB_INSERT_1");
			
			DalHints hints = new DalHints();
			//SimpleShardHintStrategy test
			hints.set(DalHintEnum.shard, "1");
			hints.set(DalHintEnum.masterOnly);

			List<Map<String, Object>> result = client.query("SELECT TOP 1000 * FROM AbacusPara Where ParaID > 0 ", new StatementParameters(), hints, new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper()));
			System.out.println(result.size());
			
			hints.set(DalHintEnum.shard, "");
			result = client.query("SELECT TOP 1000 * FROM AbacusPara Where ParaID > 0 ", new StatementParameters(), hints, new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper()));
			System.out.println(result.size());

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
        LogConfig.setAppID("9302011");
        LogConfig.setLoggingServerIP("192.168.82.58");
        LogConfig.setLoggingServerPort("63100");
        DalClientFactoryTest test = new DalClientFactoryTest();
        // The follow test should be be performed in one run. because the factory internally will cache Dal.config.
        // So subsequence call will not do the actual init
        test.testLoadFromClassPath();
//        test.testLoad();
//        test.testNoStrategy();
		System.exit(0);
	}
}