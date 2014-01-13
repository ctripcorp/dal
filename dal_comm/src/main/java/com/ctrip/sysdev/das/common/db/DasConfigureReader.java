package com.ctrip.sysdev.das.common.db;

import com.ctrip.sysdev.das.common.to.MasterLogicDB;

public interface DasConfigureReader {
	String[] getLogicDbsByGroup(String logicDbGroupName) throws Exception;
	MasterLogicDB getMasterLogicDB(String logicdbName) throws Exception;
}
