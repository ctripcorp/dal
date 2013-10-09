package com.ctrip.sysdev.das.controller;

import java.util.HashSet;
import java.util.Set;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Assumption: the dead/live status of Das worker does not affect the dead/live
 * status of controller
 * 
 * @author jhhe
 * 
 */
public class DasController extends DasService {
	private static Logger logger = LoggerFactory.getLogger(DasController.class);

	private String availableServerPath;
	private String controllerPath;
	private String workerPath;
	private DasWorkerManager workerManager;
	private String workerJarLocation;
	
	private Set<String> startingWorker = new HashSet<String>(); 

	public DasController(String hostPort, String workerJarLocation) throws Exception {
		super(hostPort);
		this.workerJarLocation = workerJarLocation;
	}

	protected void initService() {
		workerManager = new DasWorkerManager(zk, workerPath, workerJarLocation);

		availableServerPath = pathOf(NODE, ip);
		controllerPath = pathOf(CONTROLLER, ip);
		workerPath = pathOf(WORKER, ip);

		watch(availableServerPath).watch(controllerPath)
				.watchChildren(workerPath).watchChildren(PORT);
	}

	protected boolean validate() {
		try {
			// If the ip is not on the list
			if (zk.exists(availableServerPath, null) == null)
				return errorByFalse("The ip: " + ip + " does not exist under "
						+ availableServerPath);

			// There is an existing controller
			if (zk.exists(controllerPath, null) != null)
				return errorByFalse("The ip: " + ip
						+ " is already exist under " + controllerPath);
			return true;
		} catch (Exception e) {
			logger.error("Error during validate controller and worker path", e);
			return false;
		}
	}

	protected boolean register() {
		try {
			zk.create(controllerPath, new byte[0], Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL);

			if (zk.exists(workerPath, null) == null) {
				logger.info("No worker path for ip " + ip
						+ " found. Create path at " + workerPath);
				zk.create(workerPath, new byte[0], Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
			}

			checkWorkers();
			
			return true;
		} catch (Exception e) {
			logger.error("Error during register controller path", e);
			return false;
		}
	}

	protected boolean isDead(WatchedEvent event) {
		switch (event.getType()) {
		case None:
			return logOnTrue(event.getState() == Event.KeeperState.Expired,
					"Session expired!!!");
		case NodeDeleted:
			if (event.getPath().equals(availableServerPath))
				return logOnTrue(true,
						"Host ip is removed from available DAS node list");
			else
				return logOnTrue(event.getPath().equals(controllerPath),
						"Host ip is removed from current controller node list");
		case NodeChildrenChanged:
			if (containsParent(event.getPath()))
				checkWorkers();
		default:
			return false;
		}
	}

	public void checkWorkers() {
		try {
			Set<String> workers = new HashSet<String>(zk.getChildren(
					workerPath, null));
			Set<String> workerCandidate = new HashSet<String>(zk.getChildren(PORT,
					null));

			// Start all workers on the available list but not on the running
			// list
			// Remove existing worker
			workerCandidate.removeAll(workers);
			startingWorker.removeAll(workers);
			workerCandidate.removeAll(startingWorker);
			startingWorker.addAll(workerCandidate);
			
			workerManager.startAll(workerCandidate, String.valueOf(this.hashCode()));

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	protected void shutdown() {
		try {
			logger.info("Stopping controller");
			if (zk.exists(controllerPath, false) != null)
				zk.delete(controllerPath, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			logger.error("Error: parameter incorrect. Parameters: 1. zookeeper host:port 2. Das worker jar path");
			return;
		}

		logger.info("The zookeeper host:port: " + args[0]);
		logger.info("Das worker jar path: " + args[1]);
		
		try {
			new DasController(args[0], args[1]).run();
		} catch (Exception e) {
			logger.error("Can not start DAS controller: ", e);
			return;
		}
	}
}
