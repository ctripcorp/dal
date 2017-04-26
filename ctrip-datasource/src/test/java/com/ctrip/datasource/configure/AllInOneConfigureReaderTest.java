package com.ctrip.datasource.configure;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class AllInOneConfigureReaderTest {
	@Test
	public void testGetDataSourceConfiguresSuccess()
	{
		AllInOneConfigureReader reader = new AllInOneConfigureReader();
		Set<String> dbNames = new HashSet<>();
		dbNames.add("SimpleShard_0");
		dbNames.add("SimpleShard_1");
		dbNames.add("dao_test_sqlsvr");
		dbNames.add("dao_test_mysql");
		dbNames.add("PayBaseDB_INSERT_2");
		reader.getDataSourceConfigures(dbNames, true).get("PayBaseDB_INSERT_2").getPassword();
	}

	@Test
	public void testGetDataSourceConfiguresValidateFail()
	{
		AllInOneConfigureReader reader = new AllInOneConfigureReader();
		Set<String> dbNames = new HashSet<>();
		dbNames.add("SimpleShard_0");
		dbNames.add("SimpleShard_1");
		dbNames.add("dao_test_sqlsvr");
		dbNames.add("dao_test_mysql");
		dbNames.add("test");
		try {
			reader.getDataSourceConfigures(dbNames, true);
			Assert.fail();
		} catch (Exception e) {
		}
	}
}
