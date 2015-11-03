package com.ctrip.platform.dal.tester.datasource;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;

public class DataSourceLocatorTest {

	@Test
	public void testGetMySqlDataSource() throws Exception {
		DataSourceConfigureProvider p = new TitanProvider();

		Set<String> s = new HashSet<>();
//		s.add("daogen");
		s.add("ha_test");
		s.add("ha_test_1");
		s.add("ha_test_2");
		s.add("dao_test");
		s.add("dao_test_sqlsvr");
		s.add("dao_test_mysql");
		s.add("dal_test_new");
		s.add("MySqlShard_0");
		s.add("MySqlShard_1");
		s.add("SqlSvrShard_0");
		s.add("SqlSvrShard_1");
		s.add("MultiThreadingTest");
		s.add("AbacusDB_SEC");
		p.setup(s);

		for(String name: s) {
			DataSource ds = new DataSourceLocator(p).getDataSource(name);
			Assert.assertNotNull(ds);
			Connection conn = ds.getConnection();
			Assert.assertNotNull(conn);
			conn.close();
		}
	}
}
