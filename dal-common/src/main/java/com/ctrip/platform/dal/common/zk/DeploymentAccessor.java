package com.ctrip.platform.dal.common.zk;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.ZooKeeper;

import com.ctrip.platform.dal.common.to.Deployment;
import com.ctrip.platform.dal.common.to.NodeDeployment;

public class DeploymentAccessor extends DasZkAccessor {

	public DeploymentAccessor(ZooKeeper zk) {
		super(zk);
	}
	
	public List<String> listName() throws Exception {
		return getChildren(DEPLOYMENT);
	}
	
	public List<NodeDeployment> list() throws Exception {
		List<String> names = listName();
		List<NodeDeployment> nodeDeployments = new ArrayList<NodeDeployment>();
		for(String name: names) {
			NodeDeployment nodeDeployment = new NodeDeployment();
			nodeDeployment.setId(name);
			nodeDeployment.setPort(listById(name));
			nodeDeployments.add(nodeDeployment);
		}
		return nodeDeployments;
	}
	
	public List<String> listNameById(String id) throws Exception {
		return getChildren(pathOf(DEPLOYMENT, id));
	}
	
	public List<Deployment> listById(String id) throws Exception {
		List<String> names = listNameById(id);
		List<Deployment> deployments = new ArrayList<Deployment>();
		for(String name: names) {
			deployments.add(getDeployment(id, name));
		}
		return deployments;
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
