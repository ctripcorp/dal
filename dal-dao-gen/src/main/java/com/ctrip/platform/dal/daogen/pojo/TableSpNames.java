package com.ctrip.platform.dal.daogen.pojo;

import java.util.List;

public class TableSpNames {
	
	private List<String> tables;
	
	private List<StoredProcedure> sps;

	public List<String> getTables() {
		return tables;
	}

	public void setTables(List<String> tables) {
		this.tables = tables;
	}

	public List<StoredProcedure> getSps() {
		return sps;
	}

	public void setSps(List<StoredProcedure> sps) {
		this.sps = sps;
	}

}
