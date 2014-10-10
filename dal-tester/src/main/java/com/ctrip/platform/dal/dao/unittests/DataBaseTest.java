package com.ctrip.platform.dal.dao.unittests;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.configure.DataBase;

public class DataBaseTest {
	@Test
	public void testGetDatabaseCategory() {
		String mySqlConnStr = "dao_test";
		String sqlServerConnStr = "SimpleShard_0";
		try {
			Assert.assertTrue(DataBase.getDatabaseCategory(mySqlConnStr) == DatabaseCategory.MySql);
			Assert.assertTrue(DataBase.getDatabaseCategory(sqlServerConnStr) == DatabaseCategory.SqlServer);
		} catch (SQLException e) {
			Assert.fail();
			e.printStackTrace();
		}
	}
}
