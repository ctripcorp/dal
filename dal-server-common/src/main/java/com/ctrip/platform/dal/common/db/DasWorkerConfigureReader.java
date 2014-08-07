package com.ctrip.platform.dal.common.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.common.cfg.DasConfigureService;
import com.ctrip.platform.dal.common.to.MasterLogicDB;
import com.ctrip.platform.dal.common.zk.DasZkAccesssorFactory;

public class DasWorkerConfigureReader implements DasConfigureReader {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private DasConfigureReader cfgReaders;
	private DasConfigureReader zkReader;
	
	public DasWorkerConfigureReader(DasConfigureService cs, DasZkAccesssorFactory accessorFactory) {
		cfgReaders = new ConfigureServiceReader(cs);
		zkReader = new ZkConfigureReader(accessorFactory);
	}
	
	@Override
	public MasterLogicDB getMasterLogicDB(String logicdbName) throws Exception {
		try {
			return zkReader.getMasterLogicDB(logicdbName);
		} catch (Exception e) {
			logger.error("ZK reader is not avaliable", e);
			return cfgReaders.getMasterLogicDB(logicdbName);
		}
	}

	@Override
	public String[] getLogicDbsByGroup(String logicDbGroupName) throws Exception {
		try {
			return zkReader.getLogicDbsByGroup(logicDbGroupName);
		} catch (Exception e) {
			logger.error("ZK reader is not avaliable", e);
			return cfgReaders.getLogicDbsByGroup(logicDbGroupName);
		}
	}
}
