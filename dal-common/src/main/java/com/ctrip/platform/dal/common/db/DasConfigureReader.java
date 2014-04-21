package com.ctrip.platform.dal.common.db;

import com.ctrip.platform.dal.common.to.MasterLogicDB;

public interface DasConfigureReader {
	String[] getLogicDbsByGroup(String logicDbGroupName) throws Exception;
	MasterLogicDB getMasterLogicDB(String logicdbName) throws Exception;
}
