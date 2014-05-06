package com.ctrip.platform.dal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class DasConfigure implements DasControllerConstants {
	private ZooKeeper zk;

	public DasConfigure(ZooKeeper zk) {
		this.zk = zk;
	}

	private void init() throws Exception {
//		Map<String, String> db2ConnMap = getNodeMaps(DB, configureMonitor);
//		Map<String, String> portDbMap = getNodeMaps(PORT, configureMonitor);
	}

	private Map<String, String> getNodeMaps(String node, Watcher watcher)
			throws Exception {
		List<String> children = zk.getChildren(node, watcher);
		Map<String, String> map = new HashMap<String, String>();

		for (String child : children) {
			String path = new StringBuilder(node).append(SEPARATOR)
					.append(child).toString();
			Stat stat = new Stat();
			map.put(child, new String(zk.getData(path, watcher, stat)));
		}

		return map;
	}
}
