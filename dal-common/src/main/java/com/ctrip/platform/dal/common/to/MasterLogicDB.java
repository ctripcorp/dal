package com.ctrip.platform.dal.common.to;

import java.util.List;

public class MasterLogicDB {
	private String name;
	private LogicDbSetting setting;
	private List<LogicDB> slave;

	public List<LogicDB> getSlave() {
		return slave;
	}

	public void setSlave(List<LogicDB> slave) {
		this.slave = slave;
	}

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
