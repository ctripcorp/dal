package com.ctrip.sysdev.das.common.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DasZkAccessor implements DasZkPathConstants {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected ZooKeeper zk;

	public ZooKeeper getZk() {
		return zk;
	}

	public void setZk(ZooKeeper zk) {
		this.zk = zk;
	}

	public void create(String path, String value) {

	}

	public void remove(String path) throws Exception {
		zk.delete(path, -1);
	}

	public void setValue(String path, String value) {

	}

	public String[] getChildren() {
		return null;
	}

	protected String pathOf(String parent, String child) {
		return new StringBuilder(parent).append(SEPARATOR).append(child)
				.toString();
	}

	protected boolean errorByFalse(String msg) {
		logger.error(msg);
		return false;
	}

	protected boolean logOnTrue(boolean value, String msg) {
		if (value)
			logger.info(msg);
		return value;
	}

	protected void createPath(String path) {
		String[] nodes = path.split(SEPARATOR);
		try {
			if(zk.exists(path, null) != null)
				return;
			
			String curPath = "";
			for(String node: nodes){
				curPath = pathOf(curPath, node);
				if(zk.exists(path, null) != null)
					continue;
					
				zk.create(curPath, new byte[0], Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
			}
		} catch (Exception e) {
			logger.error("Error during create path", e);
		}
	}

	public abstract void initialize();
}
