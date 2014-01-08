package com.ctrip.sysdev.das.common.to;

import java.util.List;

public class MasterLogicDB extends LogicDB {
	private List<LogicDB> slave;

	public List<LogicDB> getSlave() {
		return slave;
	}

	public void setSlave(List<LogicDB> slave) {
		this.slave = slave;
	}
}
