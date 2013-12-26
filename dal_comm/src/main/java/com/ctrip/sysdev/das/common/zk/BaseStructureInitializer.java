package com.ctrip.sysdev.das.common.zk;

public class BaseStructureInitializer {
	private DasZkAccesssorFactory factory;
	public BaseStructureInitializer(DasZkAccesssorFactory factory) {
		this.factory = factory;
	}
	
	public void initialize() {
		factory.getLogicDbAccessor().initialize();
		factory.getLogicDbGroupAccessor().initialize();
		factory.getPortAccessor().initialize();
		factory.getDeploymentAccessor().initialize();
		factory.getDasWorkerAccessor().initialize();
		factory.getDasNodeAccessor().initialize();
	}
}
