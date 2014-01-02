package com.ctrip.sysdev.das.common.zk.to;

public class DasNode {
	private String name;
	private DasNodeSetting setting;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DasNodeSetting getSetting() {
		return setting;
	}

	public void setSetting(DasNodeSetting setting) {
		this.setting = setting;
	}
}
