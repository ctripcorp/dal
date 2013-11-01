package com.ctrip.sysdev.das.console.domain;

public class MasterSlave {
	private DB master;
	private DB[] slaves;
	public DB getMaster() {
		return master;
	}
	public void setMaster(DB master) {
		this.master = master;
	}
	public DB[] getSlaves() {
		return slaves;
	}
	public void setSlaves(DB[] slaves) {
		this.slaves = slaves;
	}
}
