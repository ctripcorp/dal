package com.ctrip.sysdev.das.controller;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DasWorkerManager implements DasControllerConstants {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private String workingDirectory = "D:/Users/gawu/Documents/worker/4888";
	private String startingHeapSizeInMegabytes = "50";
	private String maximumHeapSizeInMegabytes = "100";
	private String javaRuntime = "java";

	private String mainClass = "com.ctrip.sysdev.das.DalServer";
	
	private ZooKeeper zk;
	private String workerRoot;
	private String workerJarLocation;
	
	public DasWorkerManager(ZooKeeper zk, String workerPath, String workerJarLocation) {
		this.zk = zk;
		this.workerRoot = workerPath;
		this.workerJarLocation = workerJarLocation;
	}
	
	public void startAll(Collection<String> workerPorts, String monitorId) {
		for(String port: workerPorts) {
			try {
				startWorker(port, monitorId);
			} catch (Exception e) {
				logger.error("Error during start worker at port: " + port, e);
			}
		}
	}
	
	private void startWorker(String port, String monitorId) throws Exception {
		List<String> argumentsList = new ArrayList<String>();
		argumentsList.add(this.javaRuntime);
		argumentsList.add(MessageFormat.format("-Xms{0}M",
				String.valueOf(this.startingHeapSizeInMegabytes)));
		argumentsList.add(MessageFormat.format("-Xmx{0}M",
				String.valueOf(this.maximumHeapSizeInMegabytes)));
		argumentsList.add("-classpath");
		argumentsList.add(workerJarLocation);
		argumentsList.add(this.mainClass);

		argumentsList.add(port);
		argumentsList.add(monitorId);

		ProcessBuilder processBuilder = new ProcessBuilder(
				argumentsList.toArray(new String[argumentsList.size()]));
		processBuilder.redirectErrorStream(true);
		
		processBuilder.directory(new File(this.workingDirectory));

		logger.info("Starting worker on port: " + port);
		Process p = processBuilder.start();
		
		new StreamWriter(p.getInputStream(), System.out);
		new StreamWriter(p.getErrorStream(), System.err);
	}

	public void stopAll(Collection<String> workers) {
		logger.info("Work on ports: " + workers + " will be removed");
		for(String port: workers){
			logger.info("Stopping worker on port: " + port);
			String path = workerPath(port);
			try {
				zk.delete(path, -1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private String workerPath(String port) {
		return workerRoot + SEPARATOR + port;
	}
}
