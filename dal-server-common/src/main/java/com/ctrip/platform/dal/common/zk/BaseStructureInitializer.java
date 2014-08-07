package com.ctrip.platform.dal.common.zk;

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
		factory.getDasNodeAccessor().initialize();
		factory.getDasWorkerAccessor().initialize();
		factory.getDasControllerAccessor().initialize();
	}
}
