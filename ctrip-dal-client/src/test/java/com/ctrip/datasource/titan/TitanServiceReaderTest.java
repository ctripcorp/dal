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
	public void testGetFromTitanServiceSuccess() {
		String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";
		TitanProvider provider = new TitanProvider();
		Set<String> dbNames = new HashSet<>();
		dbNames.add("AbacusDB_INSERT_1");
		dbNames.add("CrawlerResultMDB");
		
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
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testGetFromTitanServiceFail() {
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
			Assert.fail();
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testGetFromTitanServiceProd() {
		String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";
		TitanProvider provider = new TitanProvider();
		Set<String> dbNames = new HashSet<>();
		dbNames.add("test1");
		dbNames.add("test2");
		
		Map<String, String> settings = new HashMap<>();
		settings.put(TitanProvider.SERVICE_ADDRESS, fws);
		settings.put(TitanProvider.USE_LOCAL_CONFIG, "false");
		try {
			provider.initialize(settings);
			provider.setup(dbNames);
			// Can not test from local environment
		} catch (Exception e) {
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
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testGetFromLocalConfigWitUsingSettingFail() {
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
			
			Assert.fail();
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
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testGetFromLocalConfigWitUsingConfigVersionFlagFail() {
		TitanProvider provider = new TitanProvider();
		Set<String> dbNames = new HashSet<>();
		dbNames.add("SimpleShard_0");
		dbNames.add("test");
		
		Map<String, String> settings = new HashMap<>();
		try {
			provider.initialize(settings);
			provider.setup(dbNames);
			
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetFromTitanServiceUser() {
		String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";
		TitanProvider provider = new TitanProvider();
		Set<String> dbNames = new HashSet<>();
		dbNames.add("PkgWorkflowDB_W");
		
		Map<String, String> settings = new HashMap<>();
		settings.put(TitanProvider.SERVICE_ADDRESS, fws);
		settings.put(TitanProvider.USE_LOCAL_CONFIG, "false");
		settings.put(TitanProvider.TIMEOUT, "100");
		try {
			provider.initialize(settings);
			provider.setup(dbNames);
			
			DataSourceConfigure result = null;
			
			result = provider.getDataSourceConfigure("PkgWorkflowDB_W");
			Assert.assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testGetFromTitanServiceName() {
		String name = "SecUGCdb_W";
		String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";
		TitanProvider provider = new TitanProvider();
		Set<String> dbNames = new HashSet<>();
		dbNames.add(name);
		
		Map<String, String> settings = new HashMap<>();
		settings.put(TitanProvider.SERVICE_ADDRESS, fws);
		settings.put(TitanProvider.USE_LOCAL_CONFIG, "false");
		settings.put(TitanProvider.TIMEOUT, "100");
		try {
			provider.initialize(settings);
			provider.setup(dbNames);
			
			DataSourceConfigure result = null;
			
			result = provider.getDataSourceConfigure(name);
			Assert.assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testGetFromUATTitanService() {
		String uat = "https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query";
		TitanProvider provider = new TitanProvider();
		Set<String> dbNames = new HashSet<>();
		dbNames.add("GSCommunityDB_SELECT_1");
		dbNames.add("YouSearchDB");
		dbNames.add("GSDestDB_SELECT_1");
		
		Map<String, String> settings = new HashMap<>();
		settings.put(TitanProvider.SERVICE_ADDRESS, uat);
		settings.put(TitanProvider.USE_LOCAL_CONFIG, "false");
		settings.put(TitanProvider.TIMEOUT, "100");
		try {
			provider.initialize(settings);
			provider.setup(dbNames);
			
			DataSourceConfigure result = null;

			for(String name: dbNames) {
				result = provider.getDataSourceConfigure(name);
				Assert.assertNotNull(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	// This test simulate _SH case in PROD. You have to hijack TitanProvide to make PROD_SUFFIX = _W
//	@Test
//	public void testGetFromTitanService_SH() {
//		String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";
//		TitanProvider provider = new TitanProvider();
//		Set<String> dbNames = new HashSet<>();
//		dbNames.add("PkgWorkflowDB");
//		
//		Map<String, String> settings = new HashMap<>();
//		settings.put(TitanProvider.SERVICE_ADDRESS, fws);
//		settings.put(TitanProvider.USE_LOCAL_CONFIG, "false");
//		try {
//			provider.initialize(settings);
//			provider.setup(dbNames);
//			
//			DataSourceConfigure result = null;
//			
//			result = provider.getDataSourceConfigure("PkgWorkflowDB");
//			Assert.assertNotNull(result);
//		} catch (Exception e) {
//			e.printStackTrace();
//			Assert.fail();
//		}
//	}
}
