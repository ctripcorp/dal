package com.ctrip.platform.dao;

import com.ctrip.platform.dal.common.db.DasConfigureReader;
import com.ctrip.platform.dal.common.db.DruidDataSourceWrapper;
import com.ctrip.platform.dao.client.DirectClient;

public class DalClientFactory {
	private String logicDbName;
	private static DruidDataSourceWrapper connPool;

	public DalClientFactory(DasConfigureReader reader, String logicDbName) throws Exception {
		this.logicDbName = logicDbName;
		connPool = new DruidDataSourceWrapper(reader, new String[]{logicDbName});
	}
	
	public DirectClient getClient() {
		return new DirectClient(connPool, logicDbName);
	}
	
	public static void initDirectClient(String logicDbName) {
		
	}
	
	/*
	public static void initDasClient(String logicDbName) {
		
	}
	
	public static void initDirectClient(DasConfigureReader reader, String logicDbName) {
		
	}
	
	public static void initDasClient(DasConfigureReader reader, String logicDbName) {
		
	}
	
	public static void init(DasConfigureReader reader, String logicDbName) {
		
	}*/
	
}
