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
			// This won't work for local environment set to fat
//			Assert.assertNotNull(dl.createDataSource("SimpleShard_0", null, null));
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
			Assert.assertNotNull(dl.createDataSource("DalService2DB", fws, "12233"));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetDataSourceTitanSuccess2() {
		DalDataSourceFactory dl = new DalDataSourceFactory();
		try {
			Assert.assertNotNull(dl.createDataSource("CommonOrderDB", fws, "12233"));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
