package com.ctrip.sysdev.das.common.zk;

import org.apache.zookeeper.ZooKeeper;

public class PortAccessor extends DasZkAccessor {
	public PortAccessor(ZooKeeper zk) {
		super(zk);
	}
	
	public String[] getPorts() {
		return null;
	}
	
	public void add(int port) {
		
	}
	
	public void remove(int port) {
		
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}
}
