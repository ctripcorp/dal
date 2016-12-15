package com.ctrip.platform.dal.common.db;

import com.ctrip.platform.dal.common.cfg.DasConfigureService;
import com.ctrip.platform.dal.common.to.DasConfigure;
import com.ctrip.platform.dal.common.to.LogicDbGroup;
import com.ctrip.platform.dal.common.to.MasterLogicDB;

public class ConfigureServiceReader implements DasConfigureReader {
	private DasConfigureService cs;
	public ConfigureServiceReader(DasConfigureService cs) {
		this.cs = cs;
	}
	
	@Override
	public MasterLogicDB getMasterLogicDB(String logicdbName) {
		DasConfigure conf = cs.getSnapshot();
		for(MasterLogicDB db: conf.getDb()) {
			if(db.getName().equals(logicdbName))
				return db;
		}
		return null;
	}

	@Override
	public String[] getLogicDbsByGroup(String logicDbGroupName) {
		DasConfigure conf = cs.getSnapshot();
		
		for(LogicDbGroup group: conf.getDbGroup())
			if(group.getName().equals(logicDbGroupName))
				return group.getLogicDBs();
		return null;
	}
}