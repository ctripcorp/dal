package com.ctrip.sysdev.das.common.zk;

import com.ctrip.sysdev.das.common.zk.to.DasNode;
import com.ctrip.sysdev.das.common.zk.to.DasNodeSetting;

public class DasNodeAccessor extends DasZkAccessor {

	public String[] listDasNodes() {
		return null;
	}
	
	public DasNode getDasNode(String ip) {
		return null;
	}
	
	public boolean createDasNode(String ip, DasNodeSetting setting) {
		return true;
	}
	
	public boolean removeDasNode(String ip) {
		// When there is running Das Worker, this operation is denied
		return true;
	}
	
	public boolean modifyDasNode(String ip, DasNodeSetting setting) {
		return true;
	}
	
	@Override
	public void initialize() {
		createPath(NODE);
	}
}
