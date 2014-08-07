package com.ctrip.platform.dal.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZklessController implements Runnable {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private com.sun.management.OperatingSystemMXBean osMBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
	private String[] ports;
	private int maxSize;
	private String javaRuntime = "java";
	private String jarLoc;
	private String workDir;
	private String mainClass = "com.ctrip.sysdev.das.ZklessDas";
	private List<Process> pl = new ArrayList<Process>();

	public void test(String jarLoc, String workDir) throws Exception {
		this.jarLoc = jarLoc;
		this.workDir = workDir;
		readConfig();
		startDas();
		waitEnd();
		stopDas();
	}
	
	private void readConfig() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("das.conf")));
			ports = reader.readLine().split(",");
			maxSize = Integer.parseInt(reader.readLine());
			
			System.out.println("DAS ports: ");
			int i = 0;
			for(String port: ports)
				System.out.print(String.format("Port %d:%s ", i++, port));
			System.out.println();
			System.out.println("Max heap size: " + maxSize);
			reader.close();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("das.conf")));
//			ports = reader.readLine();
//			maxMem = reader.readLine();
//
//			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//			
//			System.out.print("DAS ports: ");
//			ports = reader.readLine().split(",");

//			int i = 0;
//			for(String port: ports)
//				System.out.print(String.format("Port %d:%s", i++, port));
//
//			System.out.println();
//			System.out.print("Max heap size: ");
//			maxSize = Math.abs(Integer.parseInt(reader.readLine()));
		} catch (IOException e) {
		}
	}
	
	private void startDas() throws Exception {
		for(String port: ports) {
			pl.add(startWorker(port, maxSize)); 
		}
		
		sender = Executors.newSingleThreadScheduledExecutor();
		sender.scheduleAtFixedRate(this, 10, 5, TimeUnit.SECONDS);
		initMemory = osMBean.getTotalPhysicalMemorySize() - osMBean.getFreePhysicalMemorySize();
	}
	
	private Process startWorker(String port, int maxHeap) throws Exception {
		List<String> argumentsList = new ArrayList<String>();
		argumentsList.add(this.javaRuntime);
		argumentsList.add("-server");
		argumentsList.add(String.format("-Xms%dM",maxHeap));
		argumentsList.add(String.format("-Xmx%dM",maxHeap));
		argumentsList.add("-classpath");
		argumentsList.add(jarLoc);
		argumentsList.add(this.mainClass);

		argumentsList.add(port);
		argumentsList.add("aaa");
		argumentsList.add("localhost:8080");
		argumentsList.add("false");

		ProcessBuilder processBuilder = new ProcessBuilder(
				argumentsList.toArray(new String[argumentsList.size()]));
		processBuilder.redirectErrorStream(true);
		
		processBuilder.directory(new File(workDir + port));

		logger.info("Starting worker on port: " + port);
		Process p = processBuilder.start();
		
		new StreamWriter(p.getInputStream(), System.out);
		new StreamWriter(p.getErrorStream(), System.err);
		return p;
	}

	
	private void waitEnd() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String cmd;
			System.out.println("Press Enter to terminate");
			cmd = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	long initMemory;
	double maxCpuUsage;
	long maxMemory;
	
	double totalCpuUsage;
	long totalMemory;
	int count;

	private static ScheduledExecutorService sender;

	@Override
	public void run() {
		displayLoad();		
	}

	private void displayLoad() {
		count++;
		
		double cpuUsage = osMBean.getSystemCpuLoad();
		totalCpuUsage += cpuUsage;
		
		if(maxCpuUsage < cpuUsage)
			maxCpuUsage = cpuUsage;
		
		long memory = osMBean.getTotalPhysicalMemorySize() - osMBean.getFreePhysicalMemorySize() - initMemory;
		totalMemory += memory;
		
		if(maxMemory < memory)
			maxMemory = memory;

		System.out.println(String.format("CPU: %f Memory: %d", cpuUsage*100, memory/(1024*1024)));
	}
	
	private void stopDas(){
		sender.shutdown();
		logger.info("Stopping...");
		for(Process p: pl) {
			p.destroy();
		}
		logger.info("Stopped");
		System.out.println(String.format("Max CPU: %f Max Memory: %d", maxCpuUsage*100, maxMemory/(1024*1024)));
		System.out.println(String.format("Average CPU: %f Average Memory: %d", totalCpuUsage*100/count, totalMemory/(count*1024*1024)));
	}
	
	public static void main(String[] args) {
		try {
			new ZklessController().test(args[0], args[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
