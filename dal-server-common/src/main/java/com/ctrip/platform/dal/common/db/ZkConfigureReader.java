package com.ctrip.platform.dal.common.db;

import com.ctrip.platform.dal.common.to.MasterLogicDB;
import com.ctrip.platform.dal.common.zk.DasZkAccesssorFactory;
import com.ctrip.platform.dal.common.zk.LogicDbAccessor;
import com.ctrip.platform.dal.common.zk.LogicDbGroupAccessor;

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
