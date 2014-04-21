package com.ctrip.platform.dal.common.to;

import java.util.List;

public class DasConfigure {
	private List<String> port;
	private List<MasterLogicDB> db;
	private List<LogicDbGroup> dbGroup;
	private List<DasNode> node;
	private List<NodeDeployment> deployment;
	
	public List<String> getPort() {
		return port;
	}
	public void setPort(List<String> port) {
		this.port = port;
	}
	public List<MasterLogicDB> getDb() {
		return db;
	}
	public void setDb(List<MasterLogicDB> db) {
		this.db = db;
	}
	public List<LogicDbGroup> getDbGroup() {
		return dbGroup;
	}
	public void setDbGroup(List<LogicDbGroup> dbGroup) {
		this.dbGroup = dbGroup;
	}
	public List<DasNode> getNode() {
		return node;
	}
	public void setNode(List<DasNode> node) {
		this.node = node;
	}
	public List<NodeDeployment> getDeployment() {
		return deployment;
	}
	public void setDeployment(List<NodeDeployment> deployment) {
		this.deployment = deployment;
	}
}
