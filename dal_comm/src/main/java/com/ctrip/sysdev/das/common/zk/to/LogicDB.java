package com.ctrip.sysdev.das.common.zk.to;

public class LogicDB {
	private String name;
	private LogicDbSetting setting;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LogicDbSetting getSetting() {
		return setting;
	}

	public void setSetting(LogicDbSetting setting) {
		this.setting = setting;
	}
}