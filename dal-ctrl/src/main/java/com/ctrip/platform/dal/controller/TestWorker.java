package com.ctrip.platform.dal.controller;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Worker should watch both the NODE, WORKER and PORT
 * @author jhhe
 *
 */
public class TestWorker extends DasService {
	private static Logger logger = LoggerFactory.getLogger(TestWorker.class);
	
	private static final String hostPort = "csm-haddop02.dev.sh.ctripcorp.com:2181,csm-haddop03.dev.sh.ctripcorp.com:2181,csm-haddop04.dev.sh.ctripcorp.com:2181";
	private String path;
	private String port;
	private String parent;

	TestWorker(String port, String parent) throws Exception {
		super(hostPort);
		this.parent = parent;
		this.port = port;
	}
	
	protected void initService() {
		path = pathOf(pathOf(WORKER, ip), port);
		watch(path).watch(pathOf(PORT, port)).watch(pathOf(NODE, ip));
	}

	protected boolean validate() {
		try {
			return zk.exists(path, this) == null;
		} catch (Exception e) {
			logger.error("Error during validate worker path", e);
			return false;
		}
	}

	protected boolean register() {
		try {
			zk.create(path, parent.getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL);
			
			return true;
		} catch (Exception e) {
			logger.error("Error during register worker path", e);
			return false;
		}
	}

	protected void shutdown() {
		try {
			logger.info("Stopping worker");
			if(zk.exists(path, false) != null)
				zk.delete(path, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected boolean isDead(WatchedEvent event) {
		switch (event.getType()) {
		case None:
			return event.getState() == Event.KeeperState.Expired;
		case NodeDeleted:
			return contains(event.getPath());
		default:
			try {
				if(!event.getPath().equals(path))
					return false;
				
				byte[] data = zk.getData(path, false, null);
				return (data == null) || (new String(data).equals(parent) == false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	public static void main(String[] args) {
		logger.info("Started at port " + args[0]);
		try {
			new TestWorker(args[0], args[1]).run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("End");
	}
}
