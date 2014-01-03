package com.ctrip.sysdev.das.common.zk;

public class DasWorkerAccessor extends DasZkAccessor {

	public String[] listDasWorkers() {
		return null;
	}
	
	public String[] listDasWorkersByLogicDB(String logicDbName) {
		return null;
	}
	
	public String[] listDasWorkersByLogicDBGroup(String logicDbGroupName) {
		return null;
	}
	
	public boolean registerDasWorker(String ip, int port) {
		// Add node to all workers' directory
		// Add node to db_node directory
		// Add node to db_group_node directory
		return true;
	}
	
	public boolean removeDasNode(String ip) {
		return true;
	}
	
	public String[] getLogicDbs() {
		return null;
	}
	
	public String[] getLogicDb(String logicDbName) {
		return null;
	}

	@Override
	public void initialize() {
		createPath(WORKER);
	}
}
