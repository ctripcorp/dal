package com.ctrip.sysdev.das.common.db;

import com.ctrip.sysdev.das.common.to.MasterLogicDB;
import com.ctrip.sysdev.das.common.zk.DasZkAccesssorFactory;
import com.ctrip.sysdev.das.common.zk.LogicDbAccessor;
import com.ctrip.sysdev.das.common.zk.LogicDbGroupAccessor;

public class ZkConfigureReader implements DasConfigureReader {
	private LogicDbGroupAccessor logicDbGroupAccessor;
	private LogicDbAccessor logicDbAccessor;
	
	public ZkConfigureReader(DasZkAccesssorFactory accessorFactory) {
		this.logicDbGroupAccessor = accessorFactory.getLogicDbGroupAccessor();
		this.logicDbAccessor = accessorFactory.getLogicDbAccessor();
	}

	@Override
	public String[] getLogicDbsByGroup(String logicDbGroupName) throws Exception {
		return logicDbGroupAccessor.getGroup(logicDbGroupName);
	}
	
	@Override
	public MasterLogicDB getMasterLogicDB(String logicDbName) throws Exception {
		return logicDbAccessor.getMasterByName(logicDbName);
	}
}
