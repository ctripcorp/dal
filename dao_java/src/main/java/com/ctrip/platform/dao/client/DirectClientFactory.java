package com.ctrip.platform.dao.client;

import com.ctrip.sysdev.das.common.db.DasConfigureReader;
import com.ctrip.sysdev.das.common.db.DruidDataSourceWrapper;

public class DirectClientFactory {
	private String logicDbName;
	private static DruidDataSourceWrapper connPool;

	public DirectClientFactory(DasConfigureReader reader, String logicDbName) throws Exception {
		this.logicDbName = logicDbName;
		connPool = new DruidDataSourceWrapper(reader, new String[]{logicDbName});
	}
	
	public DirectClient getClient() {
		return new DirectClient(connPool, logicDbName);
	}
}
