package com.ctrip.sysdev.das.common.zk;

public class DasWorkerAccessor {
	public boolean addNode(String ip) {
		return true;
	}
	
	public boolean removeNode(String ip) {
		return true;
	}
	
	public String[] getNodes() {
		return null;
	}
	
	public String[] getLogicDbs() {
		return null;
	}
	
	public String[] getLogicDb(String logicDbName) {
		return null;
	}

}
