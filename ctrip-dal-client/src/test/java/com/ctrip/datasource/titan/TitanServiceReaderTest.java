package com.ctrip.datasource.titan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

public class TitanServiceReaderTest {
	
	@Test
	public void testGetAppid() {
		Assert.assertEquals("930201", TitanProvider.getPreConfiguredAppId());
	}
	
	@Test
	public void testGetFromTitanService() {
		String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";
		TitanProvider provider = new TitanProvider();
		Set<String> dbNames = new HashSet<>();
		dbNames.add("AbacusDB_INSERT_1");
		dbNames.add("CrawlerResultMDB");
		dbNames.add("test");
		
		Map<String, String> settings = new HashMap<>();
		settings.put(TitanProvider.SERVICE_ADDRESS, fws);
		settings.put(TitanProvider.USE_LOCAL_CONFIG, "false");
		try {
			provider.initialize(settings);
			provider.setup(dbNames);
			
			DataSourceConfigure result = null;
			
			result = provider.getDataSourceConfigure("AbacusDB_INSERT_1");
			Assert.assertNotNull(result);

			result = provider.getDataSourceConfigure("CrawlerResultMDB");
			Assert.assertNotNull(result);
			
			result = provider.getDataSourceConfigure("test");
			Assert.assertNull(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetFromLocalConfigWitUsingSetting() {
		TitanProvider provider = new TitanProvider();
		Set<String> dbNames = new HashSet<>();
		dbNames.add("SimpleShard_0");
		dbNames.add("SimpleShard_1");
		dbNames.add("dao_test_sqlsvr");
		dbNames.add("dao_test_mysql");
		dbNames.add("test");
		
		Map<String, String> settings = new HashMap<>();
		settings.put(TitanProvider.USE_LOCAL_CONFIG, "true");
		try {
			provider.initialize(settings);
			provider.setup(dbNames);
			
			DataSourceConfigure result = null;
			
			result = provider.getDataSourceConfigure("SimpleShard_0");
			Assert.assertNotNull(result);

			result = provider.getDataSourceConfigure("SimpleShard_1");
			Assert.assertNotNull(result);
			
			result = provider.getDataSourceConfigure("dao_test_sqlsvr");
			Assert.assertNotNull(result);

			result = provider.getDataSourceConfigure("dao_test_mysql");
			Assert.assertNotNull(result);

			result = provider.getDataSourceConfigure("test");
			Assert.assertNull(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetFromLocalConfigWitUsingConfigVersionFlag() {
		TitanProvider provider = new TitanProvider();
		Set<String> dbNames = new HashSet<>();
		dbNames.add("SimpleShard_0");
		dbNames.add("SimpleShard_1");
		dbNames.add("dao_test_sqlsvr");
		dbNames.add("dao_test_mysql");
		dbNames.add("test");
		
		Map<String, String> settings = new HashMap<>();
		try {
			provider.initialize(settings);
			provider.setup(dbNames);
			
			DataSourceConfigure result = null;
			
			result = provider.getDataSourceConfigure("SimpleShard_0");
			Assert.assertNotNull(result);

			result = provider.getDataSourceConfigure("SimpleShard_1");
			Assert.assertNotNull(result);
			
			result = provider.getDataSourceConfigure("dao_test_sqlsvr");
			Assert.assertNotNull(result);

			result = provider.getDataSourceConfigure("dao_test_mysql");
			Assert.assertNotNull(result);

			result = provider.getDataSourceConfigure("test");
			Assert.assertNull(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
