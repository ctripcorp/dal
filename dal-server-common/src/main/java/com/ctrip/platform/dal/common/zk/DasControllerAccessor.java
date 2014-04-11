package com.ctrip.platform.dal.common.zk;

import java.util.List;

import org.apache.zookeeper.ZooKeeper;

public class DasControllerAccessor extends DasZkAccessor {

	public DasControllerAccessor(ZooKeeper zk) {
		super(zk);
	}
	
	public List<String> list() throws Exception {
		return getChildren(CONTROLLER);
	}
	
	public void registerController(String ip) throws Exception {
		register(CONTROLLER, ip);

		String workerPath = pathOf(WORKER, ip);
		if (!exists(workerPath)) {
			logger.info("No worker path for ip " + ip
					+ " found. Create path at " + workerPath);
			
			create(workerPath);
		}
	}
	
	public boolean isValidate(String ip) throws Exception {
		return exists(NODE, ip);
	}
	
	public boolean isRegistered(String ip) throws Exception {
		return exists(CONTROLLER, ip);
	}

	
	public void removeDasController(String ip) throws Exception {
		delete(CONTROLLER, ip);
	}

	@Override
	public void initialize() {
		createPath(CONTROLLER);
	}
}
