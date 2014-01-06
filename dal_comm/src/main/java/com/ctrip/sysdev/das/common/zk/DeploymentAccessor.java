package com.ctrip.sysdev.das.common.zk;

import java.util.List;

import org.apache.zookeeper.ZooKeeper;

import com.ctrip.sysdev.das.common.zk.to.DedicateDeployment;
import com.ctrip.sysdev.das.common.zk.to.Deployment;
import com.ctrip.sysdev.das.common.zk.to.SharedDeployment;

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
		String value = rawValue.split(Deployment.SEPARATOR)[1];
		return  isShared(rawValue) ? new SharedDeployment(value) : new DedicateDeployment(value); 
	}
	
	public void bindShared(String id, int port, String[] groupNames) {
		
	}
	
	public void bindDedicate(String id, int port, String logicDbName) {
		
	}
	
	public void clearBinding(String id, int port) throws Exception {
		setValue(pathOf(DEPLOYMENT, id), String.valueOf(port), "");
	}
	
	public boolean isShared(String rawValue) {
		return rawValue.startsWith(Deployment.SHARED);
	}

	@Override
	public void initialize() {
		createPath(DEPLOYMENT);
	}
}
