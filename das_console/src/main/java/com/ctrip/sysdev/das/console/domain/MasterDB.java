package com.ctrip.sysdev.das.console.domain;

public class MasterDB {
	private String name;
	private DbSetting setting;
	private SalveDB[] slave;

	public SalveDB[] getSlave() {
		return slave;
	}

	public void setSlave(SalveDB[] slave) {
		this.slave = slave;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DbSetting getSetting() {
		return setting;
	}

	public void setSetting(DbSetting setting) {
		this.setting = setting;
	}
}
