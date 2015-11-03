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
	public void testGet() {
		String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";
		TitanProvider provider = new TitanProvider();
		Set<String> dbNames = new HashSet<>();
		dbNames.add("AbacusDB_INSERT_1");
		dbNames.add("CrawlerResultMDB");
		dbNames.add("test");
		
		Map<String, String> settings = new HashMap<>();
		settings.put(TitanProvider.SERVICE_ADDRESS, fws);
		settings.put(TitanProvider.FORCE_LOCAL_CONFIG, "false");
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
}
