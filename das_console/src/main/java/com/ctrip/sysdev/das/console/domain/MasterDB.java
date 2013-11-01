package com.ctrip.sysdev.das.console.domain;

import java.util.List;

public class MasterDB extends DB{
	private List<DB> slaves;

	public List<DB> getSlaves() {
		return slaves;
	}

	public void setSlaves(List<DB> slaves) {
		this.slaves = slaves;
	}
}
