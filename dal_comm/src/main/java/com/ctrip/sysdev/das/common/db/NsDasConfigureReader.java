package com.ctrip.sysdev.das.common.db;

import com.ctrip.sysdev.das.common.cfg.DasConfigureService;
import com.ctrip.sysdev.das.common.to.DasConfigure;
import com.ctrip.sysdev.das.common.to.MasterLogicDB;

public class NsDasConfigureReader implements DasConfigureReader {
	private DasConfigureService cs;
	public NsDasConfigureReader(DasConfigureService cs) {
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
}
