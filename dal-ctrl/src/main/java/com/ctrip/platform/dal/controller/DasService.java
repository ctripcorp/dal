package com.ctrip.platform.dal.controller;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DasService implements DasControllerConstants, Watcher {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected ZooKeeper zk;
	protected String ip;
	private boolean dead;
	private Set<String> pathes = new HashSet<String>();
	private Set<String> parentPathes = new HashSet<String>();

	abstract protected void initService();

	abstract protected boolean validate();

	abstract protected boolean register();

	abstract protected boolean isDead(WatchedEvent event);

	abstract protected void shutdown();

	public DasService(String hostport) throws Exception {
		zk = new ZooKeeper(hostport, 30 * 1000, null);
		ip = InetAddress.getLocalHost().getHostAddress();
	}

	public void run() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				if(!dead)
					shutdown();
			}
		}));

		initService();

		if (!validate()) {
			return;
		}

		if (!register()) {
			return;
		}
		
		try {
			resetWatch();
			synchronized (this) {
				while (!dead) {
					wait();
				}
			}
		} catch (InterruptedException e) {
			logger.info("Service is interrupted");
		}

		shutdown();
		logger.info("End of service");
	}

	public void process(WatchedEvent event) {
		if(dead){
			logger.info("Server is in close stage while recieving event from ZK: " + event);
			return;
		}
		
		if (dead = isDead(event)) {
			synchronized (this) {
				notifyAll();
			}
		} else {
			resetWatch();
		}
	}

	protected DasService watch(String path) {
		pathes.add(path);
		return this;
	}

	protected DasService watchChildren(String path) {
		parentPathes.add(path);
		return this;
	}

	protected boolean contains(String path) {
		return pathes.contains(path);
	}

	protected boolean containsParent(String path) {
		return parentPathes.contains(path);
	}

	protected void resetWatch() {
		for (String path : pathes) {
			try {
				zk.exists(path, this);
			} catch (Exception e) {
				logger.error("Faild watch on: " + path, e);
			}
		}

		for (String parentPath : parentPathes) {
			try {
				zk.getChildren(parentPath, this);
			} catch (Exception e) {
				logger.error("Faild watch on: " + parentPath, e);
			}
		}
	}

	protected String pathOf(String parent, String child) {
		return new StringBuilder(parent).append(SEPARATOR).append(child).toString();
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
}
