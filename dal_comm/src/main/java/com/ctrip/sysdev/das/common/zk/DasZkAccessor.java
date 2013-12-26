package com.ctrip.sysdev.das.common.zk;

import org.apache.zookeeper.ZooKeeper;

public abstract class DasZkAccessor implements DasZkPathConstants{
	protected ZooKeeper zk;

	public ZooKeeper getZk() {
		return zk;
	}

	public void setZk(ZooKeeper zk) {
		this.zk = zk;
	}
	
	public void create(String path, String value) {
		
	}
	
	public void remove(String path) {
		
	}
	
	public void setValue(String path, String value) {
		
	}
	
	public String[] getChildren() {
		return null;
	}
	
	public abstract void initialize();
}
