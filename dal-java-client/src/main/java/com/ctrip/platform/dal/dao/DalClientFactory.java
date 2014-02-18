package com.ctrip.platform.dal.dao;

import java.util.concurrent.atomic.AtomicReference;

import com.ctrip.platform.dal.common.db.DasConfigureReader;
import com.ctrip.platform.dal.common.db.DruidDataSourceWrapper;
import com.ctrip.platform.dal.dao.client.DalDirectClient;

public class DalClientFactory {
	private static AtomicReference<DruidDataSourceWrapper> connPool = new AtomicReference<DruidDataSourceWrapper>();

	public static void initDirectClientFactory(DasConfigureReader reader, String...logicDbNames) throws Exception {
		// TODO FIXIT should allow initialize logic Db for several times
		if(connPool.get() != null)
			return;
		synchronized(DalClientFactory.class) {
			if(connPool.get() != null)
				return;
			connPool.set(new DruidDataSourceWrapper(reader, logicDbNames));
		}
	}
	
	public static void initDasClientFactory(DasConfigureReader reader, String...logicDbNames) throws Exception {
		// TODO to support
	}
	
	public static DalClient getClient(String logicDbName) {
		return new DalDirectClient(connPool.get(), logicDbName);
	}
	
}
