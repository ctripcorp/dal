package com.ctrip.sysdev.das.common.zk;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * All ZK related code should be here. the sub class should only call method in this class.
 * @author jhhe
 *
 */
public abstract class DasZkAccessor implements DasZkPathConstants {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected ZooKeeper zk;
	
	public DasZkAccessor(ZooKeeper zk) {
		this.zk = zk;
	}

	public void create(String parentPath, String node, String value)
			throws Exception {
		create(pathOf(parentPath, node), value);
	}

	public void create(String path, String value) throws Exception {
		zk.create(path, value.getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
	}

	public void create(String path) throws Exception {
		zk.create(path, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	public void register(String parentPath, String node) throws Exception {
		register(pathOf(parentPath, node));
	}

	public void register(String path) throws Exception {
		zk.create(path, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	}
	
	public boolean exists(String parentPath, String node) throws Exception {
		return exists(pathOf(parentPath, node));
	}

	public boolean exists(String path) throws Exception {
		return zk.exists(path, null) != null;
	}

	public void delete(String parentPath, String node) throws Exception {
		delete(pathOf(parentPath, node));
	}
	
	public void delete(String path) throws Exception {
		zk.delete(path, -1);
	}

	public void deleteNodeNested(String path) throws Exception {
		List<String> children = zk.getChildren(path, false);
		if (children != null) {
			for (String c : children) {
				deleteNodeNested(pathOf(path, c));
			}
		}
		zk.delete(path, -1);
	}

	public void setValue(String parentPath, String node, String value)
			throws Exception {
		setValue(pathOf(parentPath, node), value);
	}

	public void setValue(String path, String value) throws Exception {
		zk.setData(path, value.getBytes(), -1);
	}

	public String getStringValue(String parentPath, String node)
			throws Exception {
		return getStringValue(pathOf(parentPath, node));
	}

	public String getStringValue(String path) throws Exception {
		return new String(zk.getData(path, false, null));
	}

	public List<String> getChildren(String parent, String child) throws Exception {
		return getChildren(pathOf(parent, child));
	}

	public List<String> getChildren(String path) throws Exception {
		return zk.getChildren(path, false);
	}

	protected String pathOf(String...nodes) {
		StringBuilder sb = new StringBuilder();
		for(String node: nodes)
			sb.append(node).append(SEPARATOR);
		return sb.deleteCharAt(sb.length() - 1).toString();
	}
	
	protected String pathOf(String parent, String child) {
		return new StringBuilder(parent).append(SEPARATOR).append(child)
				.toString();
	}

	protected String pathOf(String parent, int child) {
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
			if (zk.exists(path, null) != null)
				return;

			String curPath = "";
			for (String node : nodes) {
				curPath = pathOf(curPath, node);
				if (zk.exists(path, null) != null)
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
