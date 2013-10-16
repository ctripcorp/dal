package com.ctrip.sysdev.das.console.domain;

public class Configure {
	private Port port;
	private DB db;
	private Node node;
	
	public Port getPort() {
		return port;
	}
	public void setPort(Port port) {
		this.port = port;
	}
	public DB getDb() {
		return db;
	}
	public void setDb(DB db) {
		this.db = db;
	}
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	
}
