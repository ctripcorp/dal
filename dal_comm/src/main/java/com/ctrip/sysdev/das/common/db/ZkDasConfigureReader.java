package com.ctrip.sysdev.das.common.db;

import com.ctrip.sysdev.das.common.to.MasterLogicDB;
import com.ctrip.sysdev.das.common.zk.LogicDbAccessor;

public class ZkDasConfigureReader implements DasConfigureReader {
	private LogicDbAccessor logicDbAccessor;
	
	public ZkDasConfigureReader(LogicDbAccessor logicDbAccessor) {
		this.logicDbAccessor = logicDbAccessor;
	}
	
	@Override
	public MasterLogicDB getMasterLogicDB(String logicDbName) throws Exception {
		return logicDbAccessor.getMasterByName(logicDbName);
	}
}
