package com.ctrip.platform.dal.common.to;

import java.util.List;

public class NodeDeployment {
	private String id;
	private List<Deployment> port;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Deployment> getPort() {
		return port;
	}
	public void setPort(List<Deployment> port) {
		this.port = port;
	}
}
