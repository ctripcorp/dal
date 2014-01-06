package com.ctrip.sysdev.das.common.zk.to;

public class SharedDeployment implements Deployment {
	private String[] logicDbGroups;

	public SharedDeployment(String logicDbGroupsStr) {
		logicDbGroups = logicDbGroupsStr.split(",");
	}
	
	public SharedDeployment(String[] logicDbGroups) {
		this.logicDbGroups = logicDbGroups;
	}

	
	public String[] getLogicDbGroups() {
		return logicDbGroups;
	}

	public void setLogicDbGroups(String[] logicDbGroups) {
		this.logicDbGroups = logicDbGroups;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(SHARED).append(SEPARATOR);
		for(String logicDbGroup: logicDbGroups) {
			sb.append(logicDbGroup);
		}
		
		return sb.deleteCharAt(sb.length() - 1).toString();
	}
}
