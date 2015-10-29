package com.ctrip.datasource.configure;

import junit.framework.Assert;

import org.junit.Test;

public class CtripDalDataSourceTest {
	String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";
	@Test
	public void testGetDataSourceLocalFail() {
		DalDataSourceFactory dl = new DalDataSourceFactory();
		try {
			dl.createDataSource("test", null, null);
			Assert.fail();
		} catch (Exception e) {
		}
	}	

	@Test
	public void testGetDataSourceLocalSuccess() {
		DalDataSourceFactory dl = new DalDataSourceFactory();
		try {
			Assert.assertNotNull(dl.createDataSource("SimpleShard_0", null, null));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetDataSourceTitanFail() {
		DalDataSourceFactory dl = new DalDataSourceFactory();
		try {
			dl.createDataSource("test", fws, "12233");
			Assert.fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void testGetDataSourceTitanSuccess() {
		DalDataSourceFactory dl = new DalDataSourceFactory();
		try {
			Assert.assertNotNull(dl.createDataSource("AbacusDB_INSERT_1", fws, "12233"));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
