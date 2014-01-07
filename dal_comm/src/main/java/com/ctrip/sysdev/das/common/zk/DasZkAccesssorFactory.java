package com.ctrip.sysdev.das.common.zk;

import java.io.IOException;

import org.apache.zookeeper.ZooKeeper;

public class DasZkAccesssorFactory {
	private ZooKeeper zk;
	
	public DasZkAccesssorFactory(String hostPorts) throws IOException {
		this.zk = new ZooKeeper(hostPorts, 30 * 1000, null);
	}
	
	public DasZkAccesssorFactory(ZooKeeper zk) {
		this.zk = zk;
	}

	public BaseStructureInitializer getBaseStructureInitializer() {
		return new BaseStructureInitializer(this);
	}

	public DasNodeAccessor getDasNodeAccessor() {
		return new DasNodeAccessor(zk);
	}
	
	public DasWorkerAccessor getDasWorkerAccessor() {
		return new DasWorkerAccessor(zk);
	}
	
	public DasControllerAccessor getDasControllerAccessor() {
		return new DasControllerAccessor(zk);
	}
	
	public LogicDbGroupAccessor getLogicDbGroupAccessor() {
		return new LogicDbGroupAccessor(zk);
	}
	
	public DeploymentAccessor getDeploymentAccessor() {
		return new DeploymentAccessor(zk);
	}
	
	public LogicDbAccessor getLogicDbAccessor() {
		return new LogicDbAccessor(zk);
	}
	
	public PortAccessor getPortAccessor() {
		return new PortAccessor(zk);
	}
}
