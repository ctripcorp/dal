package com.ctrip.sysdev.das.common.zk;

import java.util.List;

import org.apache.zookeeper.ZooKeeper;

import com.ctrip.sysdev.das.common.to.Deployment;

public class DeploymentAccessor extends DasZkAccessor {

	public DeploymentAccessor(ZooKeeper zk) {
		super(zk);
	}
	
	public List<String> list() throws Exception {
		return getChildren(DEPLOYMENT);
	}
	
	public List<String> listById(String id) throws Exception {
		return getChildren(pathOf(DEPLOYMENT, id));
	}
	
	public Deployment getDeployment(String id, String port) throws Exception {
		String rawValue = getStringValue(pathOf(pathOf(DEPLOYMENT, id), port));
		return Deployment.create(Integer.parseInt(port), rawValue); 
	}
	
	public void bindShared(String id, int port, String groupNames) throws Exception {
		setValue(pathOf(pathOf(DEPLOYMENT, id), port), SHARED + DEPLOYMENT_SEPARATOR + groupNames);
	}
	
	public void bindDedicate(String id, int port, String logicDbName) throws Exception {
		setValue(pathOf(pathOf(DEPLOYMENT, id), port), DEDICATE + DEPLOYMENT_SEPARATOR + logicDbName);
	}
	
	public void clearBinding(String id, int port) throws Exception {
		setValue(pathOf(DEPLOYMENT, id), String.valueOf(port), EMPTY_VALUE);
	}

	@Override
	public void initialize() {
		createPath(DEPLOYMENT);
	}
}
