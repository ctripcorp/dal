package com.ctrip.sysdev.das.common.to;

public class LogicDbGroup {
	private String name;
	private String[] logicDBs;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String[] getLogicDBs() {
		return logicDBs;
	}
	
	public void setLogicDBs(String[] logicDBs) {
		this.logicDBs = logicDBs;
	}
}
