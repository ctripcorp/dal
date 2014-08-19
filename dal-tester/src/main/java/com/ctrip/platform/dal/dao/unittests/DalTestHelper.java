package com.ctrip.platform.dal.dao.unittests;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalTestHelper {
	public static int getCount(DalTableDao dao) throws SQLException {
		return dao.query("1=1", new StatementParameters(), new DalHints()).size();
	}

	public static int getCount(DalTableDao dao, String where) throws SQLException {
		return dao.query(where, new StatementParameters(), new DalHints()).size();
	}
	
	public static Object query(DalTableDao dao, String where, Object... params) throws SQLException {
		return dao.query("1=1", new StatementParameters(), new DalHints()).size();
	}
}
