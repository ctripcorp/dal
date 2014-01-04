package com.ctrip.sysdev.das.common.zk;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;

public class DasControllerAccessor extends DasZkAccessor {

	public List<String> list() {
		try {
			return zk.getChildren(CONTROLLER, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public boolean register(String ip) {
		try {
			zk.create(pathOf(CONTROLLER, ip), new byte[0], Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL);

			String workerPath = pathOf(WORKER, ip);
			if (zk.exists(workerPath, null) == null) {
				logger.info("No worker path for ip " + ip
						+ " found. Create path at " + workerPath);
				zk.create(workerPath, new byte[0], Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
			}

			return true;
		} catch (Exception e) {
			logger.error("Error during register controller path", e);
			return false;
		}
	}
	
	public boolean isValidate(String ip) {
		try {
			return zk.exists(pathOf(NODE, ip), null) != null;
		} catch (Exception e) {
			logger.error("Error during validate controller path", e);
			return false;
		}
	}
	
	public boolean isRegistered(String ip) {
		try {
			return zk.exists(pathOf(CONTROLLER, ip), null) != null;
		} catch (Exception e) {
			logger.error("Error during validate controller path", e);
			return false;
		}
	}

	
	public boolean removeDasController(String ip) {
		try {
			delete(pathOf(CONTROLLER, ip));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void initialize() {
		createPath(CONTROLLER);
	}
}
