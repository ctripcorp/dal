package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.DalHints;

public interface DalShardStrategy {
	String locateDbName(DalConfigure configure, String logicDbName, DalHints hint);
}
