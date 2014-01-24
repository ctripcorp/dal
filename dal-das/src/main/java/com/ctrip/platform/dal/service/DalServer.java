package com.ctrip.platform.dal.service;

import java.io.File;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.common.cfg.DasConfigureService;
import com.ctrip.platform.dal.common.db.DasWorkerConfigureReader;
import com.ctrip.platform.dal.common.db.DruidDataSourceWrapper;
import com.ctrip.platform.dal.common.to.Deployment;
import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.common.zk.DasZkAccesssorFactory;
import com.ctrip.platform.dal.controller.DasService;
import com.ctrip.platform.dal.service.monitors.ErrorReporter;
import com.ctrip.platform.dal.service.monitors.PerformanceMonitorTask;
import com.ctrip.platform.dal.service.monitors.StatusReportTask;
import com.ctrip.platform.dal.service.netty4.Netty4Server;

public class DalServer extends DasService {
	private static Logger logger = LoggerFactory.getLogger(DalServer.class);
	public static boolean senderEnabled = true;
	public static String consoleAddr = "localhost:8080";
	public static double GC_THRESHHOLD = 0.70;
	public static DruidDataSourceWrapper DATA_SOURCE;

	private String path;
	private String port;
	private String parent;

	private Netty4Server dasService;
//	private ServerInfoMXBean serverInfoMXBean;

	public DalServer(String hostPort, String port, String parent) throws Exception {
		super(hostPort);
		this.parent = parent;
		this.port = port;
	}

	protected void initService() {
		path = pathOf(pathOf(WORKER, ip), port);
		watch(path);
		watch(pathOf(PORT, port));
		watch(pathOf(NODE, ip));
	}

	protected boolean validate() {
		try {
			if(zk.exists(path, this) == null)
				return true;
			
			logger.error("Worker's corresponding path is already in use: " + path);
			
			return false;
		} catch (Throwable e) {
			logger.error("Error during validate worker path", e);
			return false;
		}
	}

	protected boolean register() {
		try {
			// TODO clear all ZK related
			logger.info("Registering: " + path);
			zk.create(path, parent.getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL);
			logger.info("Register success.");
			
			initConfigure();
			initDataSource();
			initMonitors();
			
			GuiceObjectFactory factory = new GuiceObjectFactory();
			dasService = factory.getInstance(Netty4Server.class);
			dasService.start(Integer.parseInt(port));
			
			initJMX();
			
			return true;
		} catch (Throwable e) {
			logger.error("Error during register worker path", e);
			// SingleInstanceDaemonTool.bailout(-1);
			return false;
		}
	}
	
	private void initConfigure() {
		Configuration.addResource("conf.properties");
	}
	
	private void initDataSource() throws Exception {
		DasConfigureService cs = new DasConfigureService("localhost:8080", new File("e:/snapshot.json"));
		DasZkAccesssorFactory af = new DasZkAccesssorFactory(zk);
		DasWorkerConfigureReader reader = new DasWorkerConfigureReader(cs, af);
		Deployment d = af.getDeploymentAccessor().getDeployment(ip, port);
		DATA_SOURCE = new DruidDataSourceWrapper(d, reader);
	}
	
	private void initMonitors() throws Exception {
//		StatusReportTask.initInstance("http://172.16.155.184:8080", 50);
		StatusReportTask.initInstance(consoleAddr, 50);
		ErrorReporter.initInstance(ip, port, consoleAddr);
		PerformanceMonitorTask.start(port, ip);
	}

	private void initJMX() throws Exception {
//		SingleInstanceDaemonTool watcher = SingleInstanceDaemonTool
//		.createInstance(serverInfoMXBean.getName());
//		watcher.init();
//		MBeanUtil.registerMBean(serverInfoMXBean.getName(),
//		serverInfoMXBean.getName(), serverInfoMXBean);
	}
	
	protected void shutdown() {
		try {
			logger.info("Stopping worker");
			
			// This node must be deleted first, otherwise it may delete the node created by others 
			if (zk.exists(path, false) != null){
				String parentId = new String(zk.getData(path, false, null));
				if(parentId.equals(parent)){
					logger.info("Removing worker path: " + path);
					zk.delete(path, -1);
				}
			}
			zk.close();
		} catch (Throwable e) {
			logger.error("Error during shutdown worker", e);
		}
		
		try{
			dasService.stop();
		} catch (Throwable e) {
			logger.error("Error during shutdown worker", e);
		}
		
		PerformanceMonitorTask.shutdown();
		StatusReportTask.shutdown();
		ErrorReporter.shutdown();
		DATA_SOURCE.close();
	}

	protected boolean isDead(WatchedEvent event) {
		switch (event.getType()) {
		case None:
			// TODO shall we keep this instance?
			// return event.getState() == Event.KeeperState.Expired;
			return false;
		case NodeDeleted:
			return contains(event.getPath());
		default:
			try {
				if (!event.getPath().equals(path))
					return false;

				byte[] data = zk.getData(path, false, null);
				return (data == null)
						|| (new String(data).equals(parent) == false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	public static void main(String[] args) {
		logger.info("ZK host:port" + args[0]);
		logger.info("Started at port " + args[1]);
		try {
			if(args.length > 3) {
				DalServer.consoleAddr = args[3];
			}

			if(args.length > 4) {
				DalServer.senderEnabled = Boolean.parseBoolean(args[4]);
			}

			new DalServer(args[0], args[1], args[2]).run();
		} catch (Exception e) {
			logger.error("Error starting server", e);
		}
		logger.info("End");
	}
}
