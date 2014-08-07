package com.ctrip.platform.dal.common.zk;

import java.util.List;

import org.apache.zookeeper.ZooKeeper;

public class PortAccessor extends DasZkAccessor {
	public PortAccessor(ZooKeeper zk) {
		super(zk);
	}
	
	public List<String> list() throws Exception {
		return getChildren(PORT);
	}
	
	public void add(int port) throws Exception {
		create(PORT, String.valueOf(port), "");
	}
	
	public void remove(int port) throws Exception {
		delete(PORT, String.valueOf(port));
	}

	@Override
	public void initialize() {
		createPath(PORT);
	}
}
