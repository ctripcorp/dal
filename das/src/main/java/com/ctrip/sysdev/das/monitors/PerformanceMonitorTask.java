package com.ctrip.sysdev.das.monitors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.DalServer;

public class PerformanceMonitorTask implements Runnable {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@SuppressWarnings("restriction")
	private com.sun.management.OperatingSystemMXBean osMBean;
	private long lastSystemTime;
	private long lastProcessorCpuTime;
	private String workerId;
	private String ip;
	
	private static ScheduledExecutorService sender;
	
	@SuppressWarnings("restriction")
	public PerformanceMonitorTask(String ip, String workerId) {
		this.workerId = workerId;
		this.ip = ip;
		osMBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
		lastSystemTime = System.nanoTime();
		lastProcessorCpuTime = osMBean.getProcessCpuTime();
	}

	public static void start(String workerId, String ip) {
		sender = Executors.newSingleThreadScheduledExecutor();
		sender.scheduleAtFixedRate(new PerformanceMonitorTask(ip, workerId), 1, 1, TimeUnit.SECONDS);
	}
	
	public static void shutdown() {
		sender.shutdown();
	}

	@SuppressWarnings("restriction")
	@Override
	public void run() {
		if(!DalServer.senderEnabled)
			return;
		
		URL url;
		String result = "";
		
		long start = lastSystemTime;
		double systemCpuUsage = osMBean.getSystemCpuLoad();
//		double systemCpuUsage = 0.0;
		double processCpuUsage = getProcessorCpuUsage();
		long end = lastSystemTime;
		long freeMemory = Runtime.getRuntime().freeMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		long sysTotalMemory = osMBean.getTotalPhysicalMemorySize();
		long sysFreeMemory = osMBean.getFreePhysicalMemorySize();
		
		logger.debug("processCpuUsage/systemCpuUsage/totalMemory/freeMemory" + processCpuUsage + systemCpuUsage + totalMemory + freeMemory);
		
		try {
			url = new URL("http://" + DalServer.consoleAddr + "/rest/console/monitor/performance");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(
					conn.getOutputStream());

			StringBuilder sb = new StringBuilder();
			sb.append("id=").append(workerId).
				append("&ip=").append(ip).
				append("&processCpuUsage=").append(processCpuUsage).
				append("&systemCpuUsage=").append(systemCpuUsage).
				append("&start=").append(start).
				append("&end=").append(end).
				append("&freeMemory=").append(freeMemory).
				append("&totalMemory=").append(totalMemory).
				append("&sysFreeMemory=").append(sysFreeMemory).
				append("&sysTotalMemory=").append(sysTotalMemory);
			
			result = sb.toString();
			writer.write(sb.toString());
			writer.flush();
			writer.close();
			
			InputStreamReader reder = new InputStreamReader(conn.getInputStream(), "utf-8");  
	        BufferedReader breader = new BufferedReader(reder);  
	        while ((breader.readLine()) != null) {}
	        breader.close();
		} catch (Exception e) {
			logger.error("Error sending performance statistics: " + result, e);
		}
	}
	
	@SuppressWarnings("restriction")
	private double getProcessorCpuUsage() {
		// The following method not work
//		double processcpuUsage = osMBean.getProcessCpuLoad();
		long systemTime = System.nanoTime();
		long processorCpuTime = osMBean.getProcessCpuTime();
		double processorCpuUsage = ((double)(processorCpuTime - lastProcessorCpuTime))/(systemTime - lastSystemTime);

		lastSystemTime = systemTime;
		lastProcessorCpuTime = processorCpuTime;
		
		return processorCpuUsage;
	}
}
