package com.ctrip.sysdev.das.common.zk;

public class DasWorkerAccessor extends DasZkAccessor {

	public boolean addNode(String ip) {
		return true;
	}
	
	public boolean removeDasNode(String ip) {
		return true;
	}
	
	public String[] listDasNodes() {
		return null;
	}
	
	public String[] getLogicDbs() {
		return null;
	}
	
	public String[] getLogicDb(String logicDbName) {
		return null;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

}
