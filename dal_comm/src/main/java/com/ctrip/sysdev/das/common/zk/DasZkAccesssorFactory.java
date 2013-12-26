package com.ctrip.sysdev.das.common.zk;

import java.io.IOException;

import org.apache.zookeeper.ZooKeeper;

public class DasZkAccesssorFactory {
	private ZooKeeper zk;
	
	public DasZkAccesssorFactory(String hostPorts) throws IOException {
		ZooKeeper zk = new ZooKeeper(hostPorts, 30 * 1000, null);
	}
	
	public BaseStructureInitializer getBaseStructureInitializer() {
		return null;
	}

	public DasWorkerAccessor getDasWorkerAccessor() {
		return null;
	}
	
	public LogicDbGroupAccessor getLogicDbGroupAccessor() {
		return null;
	}
	
	public DeploymentAccessor getDeploymentAccessor() {
		return null;
	}
	
	public LogicDbAccessor getLogicDbAccessor() {
		return null;
	}
	
	public PortAccessor getPortAccessor() {
		return null;
	}
}
