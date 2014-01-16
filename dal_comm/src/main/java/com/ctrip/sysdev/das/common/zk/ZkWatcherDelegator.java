package com.ctrip.sysdev.das.common.zk;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO need more test to verify different cases
 * @author jhhe
 *
 */
public class ZkWatcherDelegator implements Watcher {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected ZooKeeper zk;
	private Set<String> pathes = new HashSet<String>();
	private Set<String> parentPathes = new HashSet<String>();
	private Watcher watcher;

	public ZkWatcherDelegator(ZooKeeper zk, Watcher watcher) {
		this.zk = zk;
		this.watcher = watcher;
	}
	
	public ZkWatcherDelegator watch(String path) {
		pathes.add(path);
		return this;
	}

	public ZkWatcherDelegator watchChildren(String path) {
		parentPathes.add(path);
		return this;
	}
	
	public boolean contains(String path) {
		return pathes.contains(path);
	}

	public boolean containsParent(String path) {
		return parentPathes.contains(path);
	}

	public void startWatch() {
		for (String path : pathes) {
			try {
				zk.exists(path, this);
			} catch (Exception e) {
				logger.info("Faild watch on: " + path, e.toString());
			}
		}

		for (String parentPath : parentPathes) {
			try {
				zk.getChildren(parentPath, this);
			} catch (Exception e) {
				logger.info("Faild watch on: " + parentPath, e.toString());
			}
		}
	}

	@Override
	public void process(WatchedEvent event) {
		logger.info(event.toString());
		// Delegate to real watcher
		watcher.process(event);
		startWatch();
	}
	
	public static void main(String[] args) {
		Watcher a = new Watcher() {
			public void process(WatchedEvent event) {
				System.out.printf("handled " + event);
			}
		};
		
		ZkWatcherDelegator d;
		try {
			d = new ZkWatcherDelegator(new ZooKeeper(args[0], 30 * 1000, null), a);
			d.watch(args[1]);
			d.watchChildren(args[1]);
			d.startWatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
