package com.ctrip.sysdev.das.common.to;

public class DedicateDeployment implements Deployment {
	private String logicDb;

	public DedicateDeployment() {}
	
	public DedicateDeployment(String logicDb) {
		this.logicDb = logicDb;
	}
	
	public String getLogicDb() {
		return logicDb;
	}

	public void setLogicDb(String logicDb) {
		this.logicDb = logicDb;
	}
	
	public String toString() {
		return new StringBuilder(DEDICATE).append(SEPARATOR).append(logicDb).toString();
	}
}
