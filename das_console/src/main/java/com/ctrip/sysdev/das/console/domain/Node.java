package com.ctrip.sysdev.das.console.domain;

public class Node {
	private String name;
	private NodeSetting setting;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NodeSetting getSetting() {
		return setting;
	}

	public void setSetting(NodeSetting setting) {
		this.setting = setting;
	}
}
