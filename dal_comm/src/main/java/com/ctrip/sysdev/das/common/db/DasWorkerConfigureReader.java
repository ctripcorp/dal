package com.ctrip.sysdev.das.common.db;

import com.ctrip.sysdev.das.common.cfg.DasConfigureService;
import com.ctrip.sysdev.das.common.to.MasterLogicDB;
import com.ctrip.sysdev.das.common.zk.DasZkAccesssorFactory;

public class DasWorkerConfigureReader implements DasConfigureReader {
	private DasConfigureReader[] readers;
	private DasConfigureReader curReader;
	
	public DasWorkerConfigureReader(DasConfigureService cs, DasZkAccesssorFactory accessorFactory) {
		readers = new DasConfigureReader[]{
				new ConfigureServiceReader(cs),
				new ZkConfigureReader(accessorFactory),
		};
		curReader = readers[0];
	}
	
	// TODO need to check reader back online. timeout, etc
	@Override
	public MasterLogicDB getMasterLogicDB(String logicdbName) throws Exception {
		MasterLogicDB db = null;
		Exception exception = null;
		
		try {
			return curReader.getMasterLogicDB(logicdbName);
		} catch (Exception e) {
			exception = e;
		}

		for(DasConfigureReader reader: readers) {
			try {
				db = reader.getMasterLogicDB(logicdbName);
				curReader = reader;
			} catch (Exception e) {
				exception = e;
			}
		}
		
		if(db == null)
			throw exception;
		return db;
	}

	@Override
	public String[] getLogicDbsByGroup(String logicDbGroupName) {
		// TODO Auto-generated method stub
		return null;
	}
}
